package com.huawei.app.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lettino on 2019/3/12
 *
 * 每个CarStatus只是记录了一条车道内的一辆车的状态（和单双向车道无关）
 *
 */
public class CarStatus implements Comparable<CarStatus>{
    public int carId;//车辆ID
    public Car car;

    // 表示是等待状态中的小车是否下一时刻真的可以到nextRoad上去
    public int flag = 0;

    /**
     * 是否堵塞
     */
    public int isBlock;
    /**
     * 在当前道路上该车最大可行的速度
     * 	由该速度确定是否切换RUNNING为SCHEDULING
     */
    public int curRoadSpeed;
    /**
     * 是否已经出发
     */
    public int isStart;

    /**
     * 要驶向的路口id
     */
    public Integer tagCrossId;
    /**
     * 刚通过的路口id
     */
    public Integer frmCrossId;
    /**
     * 行驶的路id
     */
    public Integer curRoadId;
    /**
     * 行驶的道id
     */
    public Integer curChannelId;

    /**
     * 真实出发时间
     */
    public int relStartTime;

    /**
     * 记录所有路过的Road
     */
    public List<Integer> passedRoadRec= new LinkedList<>();
    //在当前道路上该车最大可行的速度
    //由该速度确定是否切换RUNNING为SCHEDULING
    // min(car.maxspeed,road.maxspeed)

    // 最近一次状态被更新绝对时间
    public int curSAT;
    // 最近一次状态更新车处于通道的位置
    public int curChannelLocal;

    // 当前车辆处于行为，
    // 默认处于启动阶段
    public CarActions action = CarActions.START;

    // 注意以下两个值可以在实际通过路口时更改，并且重新加入优先队列选择中
    // 下一条路的ID,可以是假设最优的下一条路径，但并不是最优
    // 如果已经是在最后一条道路上了，则为-1;
    public Integer nextRoadId;

    // 接下来车的转向，该值也为假设值，若不按照原定方向行驶，
    // 可以更改当前值，
    // 如果无下一条路径则设为直行 DriveDirection.FORWARD
    public int turnDirected;

    @Override
    public int compareTo(CarStatus o) {
        // 最后更新时间优先
        if (curSAT != o.curSAT) {
            return curSAT - o.curSAT;
        }
        /**
         * 首先调度在行驶状态的车，前面的先开
         */
        if (action == CarActions.RUNNING) {
            if (curSAT != o.curSAT) {
                return curSAT - o.curSAT;
            }
            // 先调度前排的车辆
            if (curChannelLocal != o.curChannelLocal) {
                return o.curChannelLocal - curChannelLocal;
            }
            // 否则先调度车道号小的
            return curChannelId - o.curChannelId;
        }
        /**
         *
         * TODO
         * 处理处于等待状态的车辆，主要是要结合任务书P23那种多路口的情况
         * 初步设想：
         * 先按入路口ID进行排序，根据路口的车辆转向的目的道路的空余情况来做优先级判断
         *
         */
        if (action == CarActions.SCHEDULING) {
            if (curSAT != o.curSAT) {
                return curSAT - o.curSAT;
            }

//            if (tagCrossId != o.tagCrossId) {
//                return tagCrossId - o.tagCrossId;
//            }
            /**
             * 对自己过路口转向的那个道路的优先级判断
             * 即如果两辆车要驶向的是同一条道路，那么需要根据转向的优先级判断
             */

            // 如果转向的是同一条路，那么就会发生冲突，此时需要根据转向，道路，位置，车道进行排序
            if (nextRoadId == o.nextRoadId && (flag == 1 && o.flag == 1)) {
                // 转向
                if (turnDirected != o.turnDirected) {
                    if (turnDirected == DriveDirection.FORWARD.getAction()) return -1;
                    if (o.turnDirected == DriveDirection.FORWARD.getAction()) return 1;
                    if (turnDirected == DriveDirection.LEFT.getAction() && o.turnDirected == DriveDirection.RIGHT.getAction())
                        return -1;
                    if (turnDirected == DriveDirection.RIGHT.getAction() && o.turnDirected == DriveDirection.LEFT.getAction())
                        return 1;
                }
                // 再处理道路Id小的
                if (o.curRoadId == curRoadId) {
                    return curRoadId - o.curRoadId;
                }
                // 先车道中的位置，再车道
                if (curChannelLocal != o.curChannelLocal) {
                    return o.curChannelLocal - curChannelLocal;
                }
                if (o.curChannelId != curChannelId) {
                    return curChannelId - o.curChannelId;
                }
            }
            // 如果转向的不是同一条道路,那么，根据道路从小到大，位置靠前，车道号小，转向排序
            // 再处理道路Id小的
            if (o.curRoadId == curRoadId) {
                return curRoadId - o.curRoadId;
            }
            // 先车道中的位置，再车道
            if (curChannelLocal != o.curChannelLocal) {
                return o.curChannelLocal - curChannelLocal;
            }
            if (o.curChannelId != curChannelId) {
                return curChannelId - o.curChannelId;
            }
            // 转向
            if (turnDirected != o.turnDirected) {
                if (turnDirected == DriveDirection.FORWARD.getAction()) return -1;
                if (o.turnDirected == DriveDirection.FORWARD.getAction()) return 1;
                if (turnDirected == DriveDirection.LEFT.getAction() && o.turnDirected == DriveDirection.RIGHT.getAction())
                    return -1;
                if (turnDirected == DriveDirection.RIGHT.getAction() && o.turnDirected == DriveDirection.LEFT.getAction())
                    return 1;
            }
        }
        else if (action == CarActions.START) {
            if(!this.car.getCarSpeedLimit().equals(o.car.getCarSpeedLimit())){
                return car.getCarSpeedLimit() - o.car.getCarSpeedLimit();
            }
            return carId - o.carId; // 准备上路的车，以车id小的优先，
        }
        return 0;
    }

    public CarStatus(int carId, Car car, int curSAT) {
        this.carId = carId;
        this.car = car;
        this.curSAT = curSAT;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getIsBlock() {
        return isBlock;
    }


    public enum CarActions{

        START, // 准备上路

        RUNNING, // 终止状态

        SCHEDULING, // 等待状态

        BLOCK_SCHEDULING,

        STOP; // 到达状态


    }

    // 添加车辆经过的roadId
    public void addPassedRoad(int roadId) {
        passedRoadRec.add(roadId);
    }
}

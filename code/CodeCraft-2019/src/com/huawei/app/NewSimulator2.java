package com.huawei.app;

import com.huawei.app.model.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import com.huawei.app.model.CarStatus.CarActions;
/**
 *
 * @author wcc
 *
 * 新的模拟器
 *
 * 通过3个优先队列，终止状态队列，等待状态队列，准备出发状态队列模拟车辆的形式
 * 终止状态 > 等待状态 > 准备出发状态
 *
 * 终止状态队列：
 * 排序规则：先调度道路前排的车辆，在调度道路中车道号小的车辆
 * 因此，遍历时 最开始 拿到的车一定是车道内最靠前的车，因此判断该车是否可以通过路口：
 * 1.可以通过，其中有可能是已经达到终点，除此之外，进入SCHEDULING队列
 * 2.不可以通过，此时这条道路前面的一些可能车已经被处理了，其中可能会有一部分是已经达到终止状态的，但是还处于路口前的，也有
 * 一部分可能是因为要通过路口所以变成等待状态的，因此要试探性探测位置，使自己达到终止状态或者等待状态
 * 3.不可以通过，此时这条道路前面除了自己没有其他的车，那么只要将本车达到终止状态就可以了
 *
 * 等待状态队列：
 * 排序规则： 先调度路口Id小的，再调度道路Id小的，再处理道路中前排的车辆，再调度车道号小的，最后再根据转向进行调度
 *
 * // TODO 结合任务书，这里应该有问题,即转向的因素，需要改变CarStatus的判断
 * // TODO 最重要的是，对于SCHEDULING队列的调度，其中的nextRoad，下一个时刻并不一定能到，即下一个时刻可能还只是行驶到本道路的最前端
 * // TODO 因此，此时的转向也就非常重要了，即比较的时候可能nextRoad都相同(一个确实是可以，一个不行),但是那个不行的确实直行，可以的却是左转或右转
 * // TODO 所以，考虑在CarStatus中使用flag，即表示该车下一时刻却是可以通过路口达到nextRoad上
 *
 * 注意，对于路口调度的时候，只调度进入该路口的方向的道路，即道路要通过这个路口进行转向了
 * 因此只有2种可能会进入等待队列
 * 1.目前道路中最前面的车要过路口了 2.前面的车是等待状态的，因此自己也是等待状态的
 * 因为我们取的时候取的是最前排的车，因此遍历时 最开始 拿到的车一定是车道内最靠前的车，到了后面，拿到的车还可能是前面已经处理完了的，因此为了综合判断
 * 我们直接看这两车前面是不是有车/或者这两车前面有车的话，前面的车的状态是什么， 如果前面有车且是终止状态，则直接再根据本车的速度进行判断行驶；
 * 如果前面有车且是等待状态，则本车还是等待状态,等待下一时刻的调度
 *
 * 只有当所有车都是终止状态了，那么此轮调度才算结束
 *
 * */
public class NewSimulator2 {
    private Context ctx = null;
    private Map<Integer, Car> cars;
    private Map<Integer, Road> roads = null;
    private Map<Integer, Cross> crosses = null;

    // 这个是全局的CarStatus信息
    private Map<Integer, CarStatus> statues = null;

    private PriorityQueue<CarStatus> runningQue = null;
    private PriorityQueue<CarStatus> schedulingQue = null;
    private PriorityQueue<CarStatus> startQue = null;

    /**
     * 当前系统时间
     */
    private int curSAT = 0;

    /**
     * 记录每一时刻内，位置更新和路口调度的计数
     * 若无任何更新，则可能存在死锁，结束模拟
     */

    private int modCot = 0;
    /**
     *  在道路上行驶的车辆数量，
     */
    private int remCarCot = 0;

    /**
     *所有还没有到达的车
     * 用于控制模拟器结束
     */
    private int allCarCot = 0;

    //规划器
    Dispatcher dispatcher = null;

    public NewSimulator2(Context ctx) {
        this.ctx= ctx;
        cars = ctx.getCarMap();
        roads = ctx.getRoadMap();
        crosses = ctx.getCrossMap();
        runningQue = new PriorityQueue<>();
        schedulingQue = new PriorityQueue<>();
        startQue = new PriorityQueue<>();
    }

    public class SimStatus{
        // 获得当前系统中车辆的数量
        private SimStatus() {}
        public int getRemCarCot() {
            return remCarCot;
        }
        public int getCurSAT() {
            return curSAT;
        }
        // 获得还剩多少车辆没有完成行程
        public int getNotOutCar() {
            return allCarCot;
        }
    }
    // 注册规划器
    public void registerPlanner(Dispatcher p) {
        this.dispatcher = p;
    }

    public void init() {
        // 初始化所有CarStatus
        // 将所有准备上路的车加入的路口调度队列当中
        statues = new HashMap<>();
        cars.values().forEach(car->{
            CarStatus cs = new CarStatus(car.getCarId(), car,
                    car.getStartTime());
            // 启动行为
            cs.action=CarActions.START;
            cs.curSAT=car.getStartTime();

            // 当前道路ID设置为-1，
            cs.curRoadId = -1;

            cs.frmCrossId = car.getOriCrossId();
            // 获取道路出口的CrossId
            cs.tagCrossId = car.getOriCrossId();
            // 假设准备上路为直走，并不影响路口调度
            cs.turnDirected = DriveDirection.FORWARD.getAction();
            statues.put(car.getCarId(),cs);
            // 添加准备上路的车辆
            startQue.add(cs);
        });
        ctx.setCarStatusMap(statues);

        allCarCot = cars.size();
        // 设置系统时间为车辆最开始上路时间
        if (startQue.size() > 0) {
            curSAT = startQue.peek().curSAT;
        }

    }

    // 开始模拟调度
    public int run() {
        System.err.println("Simulator start run,AST=" + curSAT + ", car.size=" + remCarCot);
        CarStatus cs = null;
        while(true) {
            // 当前模拟器中还有车辆在行驶
            // 重置操作计数
            modCot = 0;
            // 首先处理道路中处于RUNNING行为的车辆位置更新,把车中最新时间是系统时间的先全部处理完
            while (!runningQue.isEmpty() &&
                    (cs = runningQue.peek()).curSAT == curSAT) {
                // 从running队列中取出当前时刻要更新位置的车辆
                runningQue.poll();
                // 更新cs的状态
                cs = updateRunningCarStatus(cs);
                if (cs.action == CarActions.SCHEDULING) {
                    schedulingQue.add(cs);
                }
                else if (cs.action == CarActions.RUNNING){
                    // 既然本轮调度完了是终止状态，那么就应该等到下一个时间片在调度了，因此curSAT+1
                    cs.curSAT++;
                    runningQue.add(cs);
                }
                else if (cs.action == CarActions.STOP) {
                    // 该车到达终点
                    remCarCot--;
                    allCarCot--;
                    dispatcher.onStop(cs.carId, cs.tagCrossId);
                }
            }
            // 处理路口调度的车
            while (!schedulingQue.isEmpty() &&
                    (cs = schedulingQue.peek()).curSAT == curSAT) {
                // 从scheduling中取出要路口调度的车
                schedulingQue.poll();
                // 将车进行路口调度
                cs = schedulingCarStatus(cs);
                if (cs.action == CarActions.RUNNING){
                    // 既然本轮调度完了是终止状态，那么就应该等到下一个时间片在调度了，因此curSAT+1
                    cs.curSAT++;
                    runningQue.add(cs);
                }
                else if (cs.action == CarActions.SCHEDULING)
                    // 上一时刻车辆通过路口失败，需要继续等待路口调度
                    schedulingQue.add(cs);
                else if (cs.action == CarActions.STOP) {
                    // 该车到达终点
                    remCarCot--;
                    allCarCot--;
                    dispatcher.onStop(cs.carId, cs.tagCrossId);
                }

            }

            // 处理准备上路的车
            while (!startQue.isEmpty() &&
                    (cs = startQue.peek()).curSAT == curSAT) {
                // 从scheduling中取出要路口调度的车
                startQue.poll();
                // 将车进行路口调度
                cs = startCarStatus(cs);
                if (cs.action == CarActions.RUNNING) {
                    // 路口调度成功，继续道路行驶
                    cs.curSAT++;
                    runningQue.add(cs);
                } else if (cs.action == CarActions.START)
                    // 上一时刻车辆通过路口失败，需要继续等待路口调度
                    startQue.add(cs);
            }
            System.out.println("Simulator modCot=" + modCot + " remCarCot=" + remCarCot + " allCarCot=" + allCarCot);
            if (modCot == 0) {
                // System.out.println("死锁啦!!!");
                System.err.println("Simulator may be dead locked!");
                break;
            }
            // System.out.println("完成了一轮");
            if(allCarCot > 0) curSAT++;//继续执行模拟
            else break;// 正常结束
        }// end while
        System.out.println("Simulator finished,AST=" + curSAT);
        return curSAT;
    }

    // 检测本条车道前方是否还有其他车辆,如果有的话返回最近的前方车辆的位置；如果没有，则返回-1
    public int detectAndGetFront(CarStatus cs) {
        Road curroad = roads.get(cs.curRoadId);
        // 获得当前行驶的车道
        Chanel curChannel = curroad.
                getInCrossChannels(cs.tagCrossId)[cs.curChannelId];
        // 获得当前车道中所有车的状态
        CarStatus[] cc = curChannel.getCarStatus();
        int cLength = curChannel.getRoadLength();
        // 获得当前车所在的当前车道中的位置信息
        int loc = cs.curChannelLocal;

        while (++loc < cLength) {
            if (cc[loc] != null) {
                return loc;
            }
        }
        return -1;
    }

    // 更新当前汽车位置状态
    // 队列中的一定是车道内前后顺序排序
    public CarStatus updateRunningCarStatus(CarStatus cs) {
        Road curroad = roads.get(cs.curRoadId);
        // 获得当前行驶的车道
        Chanel curChannel = curroad.
                getInCrossChannels(cs.tagCrossId)[cs.curChannelId];
        // 获得当前车道中所有车的状态
        CarStatus[] cc = curChannel.getCarStatus();
        int cLength = curChannel.getRoadLength();
        // 获得当前车所在的当前车道中的位置信息
        int loc = cs.curChannelLocal;
        // 探测道路前方的状况
        int detectResult = detectAndGetFront(cs);
        // 如果前面没有车
        if (detectResult == -1) {
            // 如果前面没车，并且本车可以过路口
            if (loc >= cLength - cs.curRoadSpeed) {
                // 进行判断是否到达终点，如果没有到达终点，那么加入等待队列中
                // 根据当前整体的权重，调用updateG规划出本车的行驶路线，根据该路线返回本车应该走的下一条道路ID
                cs.nextRoadId = dispatcher.onScheduling(cs.carId, cs.tagCrossId);
                // 到达终点了
                if (cs.nextRoadId < 0) {
                    cs.turnDirected = DriveDirection.FORWARD.getAction();
                    cs.action = CarActions.STOP;
                    cc[loc] = null;
                    modCot++; // 到终点了，位置空出来了，表示移动了
                } else {
                    // 没到终点
                    // TODO 这里要提前判断，判断该车是能够通过路口到达下一条道路，还是过不了路口，只能到达本车道的最前端
                    // TODO 以此来提前做出汽车的转向判断
                    cs.action = CarActions.SCHEDULING;
                    // 设置转向
                    // TODO 注意这里，如果每次 能够过路口 的时候都会设置一次转向
//                    cs.turnDirected = crosses.get(cs.tagCrossId)
//                            .getTurnDireByRoad(cs.curRoadId, cs.nextRoadId);

                    // 如果到不了终点，那么就要根据当前路口的情况进行车辆调度了
                    // 这里可能可以行驶到另一条道路上，也可能行驶不过去，只能停留在当前挡路的最前端
                    curroad = roads.get(cs.nextRoadId);
                    int nextRoadMaxSpeed = Math.min(curroad.getSpeedLimit(), cs.car.getCarSpeedLimit());

                    // 当前道路已行驶距离
                    // TODO 这里的curChannelLocal的值需要仔细看下，最大是不是可以达到curChannel.getRoadLength()还是curChannel.getRoadLength()-1
                    int S1 = curChannel.getRoadLength() - cs.curChannelLocal - 1;

                    // 这是下一条路单位时间内最大行驶距离SV2与在当前道路的行驶距离S1之差
                    int nrage = nextRoadMaxSpeed - S1;

                    // 得到从汽车要驶向的路口出去的所有车道,注意这里是road，这个road就是要驶向的那条路的Id
                    Chanel[] rcs = curroad.getOutCrossChannels(cs.tagCrossId);
                    // 查看要驶向的那条路中的车道是不是还有空位置
                    CheckedResult ckres = null;
                    // 表示下一个调度单位确实可以通过路口行驶到下一条道路上，那么设置转向
                    if (!(nrage <= 0 && (ckres = checkNextRoad(rcs, nrage)) == null && S1 >= nextRoadMaxSpeed)) {
                        cs.flag = 1; // 表示本车下一时刻确实可以移动到nextRoad上去，用于CarStatus中路口的nextRoad比较函数中
                        cs.turnDirected = crosses.get(cs.tagCrossId).getTurnDireByRoad(cs.curRoadId, cs.nextRoadId);
                    }
                }
            } else {
                // 前面没有车，过不了路口，那么直接开到最终状态
                loc += cs.curRoadSpeed;
                if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                cc[cs.curChannelLocal] = null;
                cs.curChannelLocal = loc;
                cc[cs.curChannelLocal] = cs;
                cs.action = CarActions.RUNNING;
                //cs.curSAT++; // 本车这一时刻已经调度完了
            }
        } else {
            // 如果前面有车
            if (cc[detectResult].action == CarActions.RUNNING) {
                // 如果前面的是终止状态
                if (loc + cs.curRoadSpeed >= detectResult) {
                    // 如果能超过前面的，那么就只开到前面的后一格
                    loc = detectResult - 1;

                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    // 本车为终止状态
                    cs.action = CarActions.RUNNING;
                    // cs.curSAT++;
                } else {
                    // 如果不能超过前面的，那么久能开多远开多远
                    loc += cs.curRoadSpeed;
                    if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    cs.action = CarActions.RUNNING;
                    //cs.curSAT++;
                }
            } else if (cc[detectResult].action == CarActions.SCHEDULING) {
                // 如果前面是等待状态
                if (loc + cs.curRoadSpeed >= detectResult) {
                    // 如果本车会超过前面呈等待状态的车，那么本车也视作等待状态
                    cs.action = CarActions.SCHEDULING;
                    // schedulingQue.add(cs);
                } else {
                    // 如果本车不会超过前面呈等待状态的车，那么能开多远开多远，并且本车视作终止状态 TODO 这里是否正确？
                    loc += cs.curRoadSpeed;
                    if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    cs.action = CarActions.RUNNING;
                    //cs.curSAT++;
                }
            }
        }
        return cs;
    }

    /**
     *
     * 路口的调度
     * 对于等待状态的车辆的调度,2种车会到达等待状态：
     * 1.目前道路中最前面的车要过路口了 2.前面的车是等待状态的，因此自己也是等待状态的
     * 由于队列中取的顺序一定是前面没有阻碍的，即取出来的车前面一定是没有障碍的（即使是因为速度不匹配无法通过路口而放在道路最前面的已经更新过的车，但是他的curSAT已经更新了，因此不再考虑范围内）
     *  因此只需要判断过路口的状态就可以了
     *
     * */
    public  CarStatus schedulingCarStatus(CarStatus cs) {
        // 如果是等待中的车，记得要先检查前面的状况
        Road road = roads.get(cs.curRoadId);
        // 获得当前行驶的车道
        Chanel curChannel = road.
                getInCrossChannels(cs.tagCrossId)[cs.curChannelId];
        // 获得当前车道中所有车的状态
        CarStatus[] cc = curChannel.getCarStatus();
        int cLength = curChannel.getRoadLength();
        // 获得当前车所在的当前车道中的位置信息
        int loc = cs.curChannelLocal;
        // 探测道路前方的状况
        int detectResult = detectAndGetFront(cs);
        // 如果前面没有车
        if (detectResult == -1) {
            // 如果可以过路口，那么本车就是该车道中最前面的可以过路口的，等待调度的车辆
            if (loc >= cLength - cs.curRoadSpeed) {
                // 进行判断是否到达终点，如果没有到达终点，进行路线规划判断
                // 根据当前整体的权重，调用updateG规划出本车的行驶路线，根据该路线返回本车应该走的下一条道路ID
                cs.nextRoadId = dispatcher.onScheduling(cs.carId, cs.tagCrossId);
                // 到达终点了
                if (cs.nextRoadId < 0) {
                    cs.turnDirected = DriveDirection.FORWARD.getAction();
                    cs.action = CarActions.STOP;
                    cc[loc] = null;
                    modCot++;
                } else {
                    // 如果到不了终点，那么就要根据当前路口的情况进行车辆调度了
                    // 这里可能可以行驶到另一条道路上，也可能行驶不过去，只能停留在当前挡路的最前端
                    road = roads.get(cs.nextRoadId);
                    int nextRoadMaxSpeed = Math.min(road.getSpeedLimit(), cs.car.getCarSpeedLimit());

                    // 当前道路已行驶距离
                    // TODO 这里的curChannelLocal的值需要仔细看下，最大是不是可以达到curChannel.getRoadLength()还是curChannel.getRoadLength()-1
                    int S1 = curChannel.getRoadLength() - cs.curChannelLocal - 1;

                    // 这是下一条路单位时间内最大行驶距离SV2与在当前道路的行驶距离S1之差
                    int nrage = nextRoadMaxSpeed - S1;

                    // 得到从汽车要驶向的路口出去的所有车道,注意这里是road，这个road就是要驶向的那条路的Id
                    Chanel[] rcs = road.getOutCrossChannels(cs.tagCrossId);
                    // 查看要驶向的那条路中的车道是不是还有空位置
                    CheckedResult ckres = null;
                    // 如果SV2-S1<=0 或者 下一个车道没有空位可以停 或者 S1>=SV2
                    // 那么只能将车放在本道路内最前面的位置
                    if (nrage <= 0 || (ckres = checkNextRoad(rcs, nrage)) == null || S1 >= nextRoadMaxSpeed) {
                        loc = curChannel.getRoadLength() - 1;
                        // 操作计数加一
                        if (loc > cs.curChannelLocal) modCot++;
                        // 移动位置
                        cc[cs.curChannelLocal] = null;
                        cc[loc] = cs;// 设置位置
                        cs.curChannelLocal = loc;
                        // 已经达到了终止状态了
                        cs.action = CarActions.RUNNING;
                        // TODO 这里就是动态路径规划的应用，即要过路口的时候运行一次规划器，根据当前全局情况的权重来重新规划小车的路径
                        cs.nextRoadId = dispatcher.onScheduling(cs.carId, cs.tagCrossId);
                        cs.turnDirected = crosses.get(cs.tagCrossId)
                                .getTurnDireByRoad(cs.curRoadId, cs.nextRoadId);
                        return cs;// 不继续通过路口
                    }
                    // 如果可以跨越路口到对面的道路上去
                    // 清除位置
                    cc[cs.curChannelLocal] = null;
                    cs.action = CarActions.RUNNING;
                    // 更新道路中车最大速度
                    cs.curRoadSpeed = nextRoadMaxSpeed;
                    cs.curRoadId = cs.nextRoadId;
                    cs.curChannelId = ckres.channelId;
                    cs.curChannelLocal = ckres.channelLocal;
                    cs.frmCrossId = cs.tagCrossId;
                    cs.tagCrossId = road.getAnotherCrossId(cs.frmCrossId);
                    modCot++;
                    // 记录行驶路径
                    cs.addPassedRoad(cs.curRoadId);
                    //更新新车道中的位置
                    cc = rcs[cs.curChannelId].getCarStatus();
                    cc[cs.curChannelLocal] = cs;
                    return cs;
                }
            } else {
                // 前面没有车，过不了路口，那么直接开到最终状态
                loc += cs.curRoadSpeed;
                cc[cs.curChannelLocal] = null;
                cs.curChannelLocal = loc;
                cc[cs.curChannelLocal] = cs;
                cs.action = CarActions.RUNNING;
                modCot++;
            }
        } else {
            // 如果前面有车
            if (cc[detectResult].action == CarActions.RUNNING) {
                // 如果前面的是终止状态
                if (loc + cs.curRoadSpeed >= detectResult) {
                    // 如果能超过前面的，那么就只开到前面的后一格
                    loc = detectResult - 1;
                    if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    // 本车为终止状态
                    cs.action = CarActions.RUNNING;
                    // cs.curSAT++;
                } else {
                    // 如果不能超过前面的，那么久能开多远开多远
                    loc += cs.curRoadSpeed;
                    if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    cs.action = CarActions.RUNNING;
                    //cs.curSAT++;
                }
            } else if (cc[detectResult].action == CarActions.SCHEDULING) {
                // 如果前面是等待状态
                if (loc + cs.curRoadSpeed >= detectResult) {
                    // 如果本车会超过前面呈等待状态的车，那么本车也视作等待状态
                    cs.action = CarActions.SCHEDULING;
                    // schedulingQue.add(cs);
                } else {
                    // 如果本车不会超过前面呈等待状态的车，那么能开多远开多远，并且本车视作终止状态 TODO 这里是否正确？
                    loc += cs.curRoadSpeed;
                    if (loc > cs.curChannelLocal) modCot++; // 如果移动了，modCot才++
                    cc[cs.curChannelLocal] = null;
                    cs.curChannelLocal = loc;
                    cc[cs.curChannelLocal] = cs;
                    cs.action = CarActions.RUNNING;
                    //cs.curSAT++;
                }
            }
        }
        return cs;
    }

    public CarStatus startCarStatus(CarStatus cs) {
        // 处理准备上路的车
        CarStatus[] cc = null;
        if (cs.action == CarActions.START) {
            // 规划器判断当前是否需要再使车辆上路
            if (!dispatcher.onTryStart(cs.carId, cs.tagCrossId, remCarCot)) {
                // 不允许车辆上路,推迟上路
                cs.curSAT++;
                return cs;
            }

            //可以行驶到路口，检查能否到进入下一条路
            cs.nextRoadId = dispatcher.onScheduling(cs.carId, cs.tagCrossId);
            Road nextRoad = roads.get(cs.nextRoadId);
            int nextRoadMaxSpeed = Math.min(nextRoad.getSpeedLimit(),
                    cs.car.getCarSpeedLimit());
            // 下一条道路最大可行长度
            int nrage = nextRoadMaxSpeed;

            // 下一条车的车道
            Chanel[] rcs = nextRoad.getOutCrossChannels(cs.tagCrossId);
            CarStatus[] curRoadCS = null;
            int roadLength = nextRoad.getLength();
            int chanelNum = nextRoad.getChanelNumber();
            int allChanelNum = roadLength * chanelNum;
            int cout = 0;
            // TODO 这里加上一个测试道路的拥堵情况，决定准备上路的车是否要进入下一条道路
            for (Chanel rc : rcs) {
                curRoadCS = rc.getCarStatus();
                for (CarStatus ct : curRoadCS) {
                    if (ct != null && ct.carId >= 0) {
                        cout++;
                    }
                }
            }
            // 当前的拥堵情况为下一条道路上车所占的比例
            double blockState = cout / allChanelNum;
            if (blockState >= 0.3) {
                cs.curSAT++;
                return cs;
            }
            // 准备上路的车无法进入下一条道路
            CheckedResult ckres = checkNextRoad(rcs, nrage);
            if (ckres == null) {
                // 推迟行驶
                cs.curSAT++;
                return cs;
            }

            // 通知规划器正式上路
            dispatcher.onStart(cs.carId, cs.tagCrossId);
            // 更新计数
            modCot++;
            // 更新车辆数量
            remCarCot++;
            cs.action = CarActions.RUNNING;
            // 更新道路中车最大速度
            cs.curRoadSpeed = nextRoadMaxSpeed;
            cs.curRoadId = cs.nextRoadId;
            cs.curChannelId = ckres.channelId;
            cs.curChannelLocal = ckres.channelLocal ;
            // 这里等待上路车辆初始化的时候tarCrossId就是起始点
            cs.frmCrossId = cs.tagCrossId;
            cs.tagCrossId = nextRoad.getAnotherCrossId(cs.frmCrossId);

            // 记录最开始的时刻，记录行驶路径
            cs.relStartTime = cs.curSAT;
            cs.addPassedRoad(cs.curRoadId);
            // cs.curSAT++;
            //更新新车道中的位置
            cc = rcs[cs.curChannelId].getCarStatus();
            cc[cs.curChannelLocal] = cs;
            return cs;
        }
        // 出现无效Action出现在当前处理中
        else {
            throw new IllegalArgumentException("illegel Action " + cs.action);
        }
    }
    public class CheckedResult{
        int channelId;// 车道号
        int channelLocal;// 车道内位置
        CheckedResult(int cId,int cLoc){
            this.channelId=cId;
            this.channelLocal=cLoc;
        }
    }

    // 因为小车进入驶向的道路要从第1号车道开始，所以从1号车道开始遍历
    public CheckedResult checkNextRoad(Chanel[] rcs,int maxRange) {
        int cId = 0, cLoc = 0;
        CarStatus[] cc = null;
        for (cId = 0; cId < rcs.length; cId++) {
            cc = rcs[cId].getCarStatus();
            // 对于最里面的车道，如果最后面的位置都没有了，那么从直接从第2个车道开始找
            if (cc[0] != null) continue;
            // 如果最后面的位置有，那么逐个往前找，直到找到maxRange要求的位置
            for (cLoc = 1; cLoc < maxRange; cLoc++)
                if (cc[cLoc] != null) break;
            cLoc--;
            break;
        }// end for;
        // 车道无法进入,即所有车道都已经遍历过了，都已经满了
        if (cId >= rcs.length) {
            return null;
        } else {
            // 返回车道和在车道内的位置
            return new CheckedResult(cId, cLoc);
        }
    }
}
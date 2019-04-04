package com.huawei.app.model;

/**调度器接口
 * Created by DanLongChen on 2019/3/14
 **/
public interface Dispatcher {

    /**
     * 用于调度已经到达的车辆
     * @param carID     车辆的ID
     * @param crossID   车所在的路口ID
     * @return          是否已经到达目的地
     */
    public boolean onStop(int carID,int crossID);


    /**
     * >返回carId车接下来要进入的RoadId
     * @return 正常返回一个道路ID,curCrossId已经和目的地相同，则需要返回-1
     * 如果下一条道路的id和当前车的id相同，将报出异常
     *
     */
    public int onScheduling(int carId,int curCrossId);


    /**
     * 当前准备上路的车,根据路况返回是否可以上路
     * remCars 表示当期模拟器中车辆的数量
     * @return
     */
    public boolean onTryStart(int carId,int crossId,int remcot);

    /**
     *  车辆正式上路时通知
     * @param carId
     * @param crossId
     * @return
     */
    public void onStart(int carId,int crossId);

}

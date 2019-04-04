package com.huawei.app.dispatcher;

import com.huawei.app.AG.DJ;
import com.huawei.app.AG.Seek;
import com.huawei.app.model.CarPassPath;
import com.huawei.app.model.Context;
import com.huawei.app.model.Dispatcher;
import com.huawei.app.utils.InitUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DanLongChen on 2019/3/14
 **/
public class KingDispatcher implements Dispatcher {
    /**
     * 全局信息
     */
    private Context context=new Context();

    /**
     * 具体进行路径规划的算法实现
     */
    Seek DJ=new DJ();


    /**
     * 传入的文件路径
     */
    String carPath=null;
    String roadPath=null;
    String crossPath=null;


    /**
     * 用来存放全部小车调度的路径
     */
    Map<Integer, CarPassPath> carPassPathMap=new HashMap<Integer,CarPassPath>();

    /**
     * 构造函数（自己加载数据）
     * @param carPath   小车的文件路径
     * @param roadPath  道路的文件路径
     * @param crossPath 路口的文件路径
     */
    public KingDispatcher(String carPath,String roadPath,String crossPath){
        this.carPath=carPath;
        this.roadPath=roadPath;
        this.crossPath=crossPath;
    }

    /**
     * 由Application加载数据的情况
     * @param context
     */
    public KingDispatcher(Context context){
        this.context=context;
    }

    /**
     * 初始化全局信息
     */
    public void init(){
        InitUtils.doInit(carPath,roadPath,crossPath,context);
    }

    /**
     * 在道路上的车辆进行调度
     * @param carID     车辆ID
     * @param curCrossID  当前所在的路口
     * @return      下一跳道路的ID
     */

    public int onRoad(int carID, int curCrossID) {
        System.out.println(carID+" onRoad "+curCrossID);
        //获取这个车的目的地ID
       int toCross=context.getCarMap().get(carID).getDesCrossId();
       if(toCross==curCrossID){
           return -1;
       }
       CarPassPath now;
       //当前车辆还没有规划路径
       if((now=carPassPathMap.get(carID))==null){
           now= DJ.startSeek(context.getGraph(),context.getCarMap().get(carID),context.getCarMap().get(carID).getOriCrossId(),context.getCarMap().get(carID).getDesCrossId());
           System.out.println("车辆ID为"+carID+"的车初始化路径规划好了");
           carPassPathMap.put(carID,now);
       }
        System.out.println("cur"+curCrossID);
        while(now!=null && now.getCurCrossId()!=curCrossID){
            //打印这条路径
            System.out.print(now.getCurCrossId()+"-> ");
            now=now.getNext();
       }
        if(now==null){
            throw new IllegalArgumentException("now is null");
        }
       return now.getNextRoadID();
    }

    /**
     * 还未出发的车进行调度，选择是否出发 当前道路上的车小于300辆就可以上路
     * @param carID     小车的ID
     * @param crossID    当前的路口
     * @param onRoadCar     道路上的车辆
     * @return
     */

    public boolean onStart(int carID, int crossID, int onRoadCar) {
        if (onRoadCar < 600) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否到达终点（到达终点之后要从carList去除，就不需要调度了）
     * @param carID     车辆的ID
     * @param crossID   车所在的路口ID
     * @return
     */
    @Override
    public boolean onStop(int carID, int crossID) {
       if(context.getCarMap().get(carID).getDesCrossId()==crossID){
           return true;
       }else{
           return false;
       }
    }

    @Override
    public int onScheduling(int carId, int curCrossId) {
        return 0;
    }

    @Override
    public boolean onTryStart(int carId, int crossId, int remcot) {
        return false;
    }

    @Override
    public void onStart(int carId, int crossId) {

    }
}

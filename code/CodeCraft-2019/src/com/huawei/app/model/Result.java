package com.huawei.app.model;

/**
 * 存放最终某辆小车形式的结果
 * Created by DanLongChen on 2019/3/14
 **/
public class Result {
    /**
     * 对应于小车的ID
     */
    private int carId;
    /**
     * 对应于小车实际开始行驶的时间
     */
    private int realStartTime;
    /**
     * 对应于小车行驶过的道路ID
     */
    private int[] roads;

    public Result(int carId,int realStartTime,int[] roads){
        this.carId=carId;
        this.realStartTime=realStartTime;
        this.roads=roads;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(int realStartTime) {
        this.realStartTime = realStartTime;
    }

    public int[] getRoads() {
        return roads;
    }

    public void setRoads(int[] roads) {
        this.roads = roads;
    }
}

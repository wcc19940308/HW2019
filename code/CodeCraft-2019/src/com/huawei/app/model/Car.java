package com.huawei.app.model;

/**
 * Created by Lettino on 2019/3/11
 */
public class Car {
    /**
     * 车辆ID
     */
    private Integer carId;
    /**
     * 出发地点的CrossId
     */
    private Integer oriCrossId ;
    /**
     * 目的地
     */
    private Integer desCrossId;
    /**
     * 车辆最高速度
     */
    private Integer carSpeedLimit;
    /**
     * 出发时刻
     */
    private Integer startTime;



    public Car(int[] args) {
        this.carId = args[0];
        this.oriCrossId = args[1];
        this.desCrossId = args[2];
        this.carSpeedLimit = args[3];
        this.startTime = args[4];
    }




    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getOriCrossId() {
        return oriCrossId;
    }

    public void setOriCrossId(Integer oriCrossId) {
        this.oriCrossId = oriCrossId;
    }

    public Integer getDesCrossId() {
        return desCrossId;
    }

    public void setDesCrossId(Integer desCrossId) {
        this.desCrossId = desCrossId;
    }

    public Integer getCarSpeedLimit() {
        return carSpeedLimit;
    }

    public void setCarSpeedLimit(Integer carSpeedLimit) {
        this.carSpeedLimit = carSpeedLimit;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", oriCrossId=" + oriCrossId +
                ", desCrossId=" + desCrossId +
                ", carSpeedLimit=" + carSpeedLimit +
                ", startTime=" + startTime+"}";
    }
}

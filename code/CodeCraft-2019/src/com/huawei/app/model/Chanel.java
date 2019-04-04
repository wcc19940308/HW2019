package com.huawei.app.model;

/**
 * Created by Lettino on 2019/3/11
 */
public class Chanel {
    int carCount = 0;//车道内的汽车数量
    private int roadId = 0;
    private int channelId = 0;//车道号
    // 当前车道新车进入的初始速度
    // 当前车道无车时，车道速度恢复为 Road的 maxSpeed;
    private int cMaxSpeed;
    /**
     * 这个车道中所有者的状态
     */
    private CarStatus[] carStatus = null;
    /**
     * 道的长度
     */
    private int roadLength;
    public Chanel(int roadId, int channelId, int cMaxSpeed,int roadLength) {
        this.roadId = roadId;
        this.channelId = channelId;
        this.cMaxSpeed = cMaxSpeed;
        this.roadLength= roadLength;
    }


    public int getRoadLength() {
        return roadLength;
    }

    public void setRoadLength(int roadLength) {
        this.roadLength = roadLength;
    }

    public CarStatus[] getCarStatus() {
        if(carStatus==null)
        {
            carStatus = new CarStatus[roadLength];
        }
        return carStatus;
    }

    public void setCarStatus(CarStatus[] carStatus) {
        this.carStatus = carStatus;
    }



    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getcMaxSpeed() {
        return cMaxSpeed;
    }

    public void setcMaxSpeed(int cMaxSpeed) {
        this.cMaxSpeed = cMaxSpeed;
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }
}

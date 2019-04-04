package com.huawei.app.model;

import java.util.PriorityQueue;

/**
 * Created by Lettino on 2019/3/11
 */
public class Road {
    //道路id，道路长度，最高限速，车道数目，起始点id，终点id，是否双向
    private Integer id;
    private int length;
    private int speedLimit;
    private int chanelNumber;
    private int fromCrossId;
    private int toCrossId;
    private int isDuplex ;
    /**
     * 源到目的地的车道
     */
    private Chanel[] fromChannels = null;
    /**
     * 目的地到源的车道
     */
    private Chanel[] toChannels = null;

    public Road(Integer id, int length, int speedLimit, int channelNumber, int fromCrossId, int toCrossId, int isDuplex) {
        this.id = id;
        this.length = length;
        this.speedLimit = speedLimit;
        this.chanelNumber = channelNumber;
        this.fromCrossId = fromCrossId;
        this.toCrossId = toCrossId;
        this.isDuplex = isDuplex;
    }

    public Road(int[] args) {
        this.id = args[0];
        this.length = args[1];
        this.speedLimit = args[2];
        this.chanelNumber = args[3];
        this.fromCrossId = args[4];
        this.toCrossId = args[5];
        this.isDuplex = args[6];
    }

    public boolean isDuplex() {
        return this.isDuplex == 1;
    }

    /**
     *  > 该路与crossId的路口相连，并且存在车道进入crossId，获得
     *  > 方向指向crossId的所有车道
     *
     * @param crossId
     * @return
     */
    public Chanel[] getInCrossChannels(int crossId) {
        if (isDuplex() && fromCrossId == crossId) {
            // 双向通道，并且to到from方向
            if (toChannels == null) {
                initToRoadChannels();
            }
            return toChannels;
        }
        if (toCrossId != crossId) {
            throw new IllegalArgumentException("toCrossId !=crossId err " + crossId);
        }
        if (fromChannels == null) {
            initFromRoadChannels();
        }
        return fromChannels;
    }

    /**
     *  > 该路与crossId的路口相连，并且存在车道从crossId出去，获得
     *  > 方向从crossId的出去所有车道
     * @param crossId
     * @return
     */
    public Chanel[] getOutCrossChannels(int crossId) {
        if (isDuplex() && toCrossId == crossId) {
            // 双向通道，并且from到to方向
            if (toChannels == null) {
                initToRoadChannels();
            }
            return toChannels;
        }
        if (fromCrossId != crossId) {
            throw new IllegalArgumentException("fromCrossId !=crossId err " + crossId);
        }
        if (fromChannels == null) {
            initFromRoadChannels();
        }
        return fromChannels;
    }
    /**
     * >通过道路一边的crossId计算另一边的CrossId
     *
     * @param oneCrossId 获得道路对面的一个crossId
     * @return
     */
    public int getAnotherCrossId(int oneCrossId) {
        if (fromCrossId == oneCrossId) {
            return toCrossId;
        } else if (toCrossId == oneCrossId) {
            return fromCrossId;
        } else {
            throw new IllegalArgumentException(oneCrossId + " not in Road " + id);
        }
    }

    /**
     * 初始化来的车道
     */
    private void initFromRoadChannels() {
        fromChannels= new Chanel[chanelNumber];
        for (int i = 0; i < chanelNumber; i++) {
            fromChannels[i] = new Chanel(id, i, speedLimit, length);
        }
    }

    /**
     * 初始化去的车道
     */
    private void initToRoadChannels() {
        toChannels = new Chanel[chanelNumber];
        for (int i = 0; i < chanelNumber; i++) {
            toChannels[i] = new Chanel(id, i, speedLimit, length);
        }
    }
    public int getChanelNumber() {
        return chanelNumber;
    }

    public void setChanelNumber(int chanelNumber) {
        this.chanelNumber = chanelNumber;
    }
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public int getFromCrossId() {
        return fromCrossId;
    }

    public void setFromCrossId(int fromCrossId) {
        this.fromCrossId = fromCrossId;
    }

    public int getToCrossId() {
        return toCrossId;
    }

    public void setToCrossId(int toCrossId) {
        this.toCrossId = toCrossId;
    }

    public int getIsDuplex() {
        return isDuplex;
    }

    public void setIsDuplex(int isDuplex) {
        this.isDuplex = isDuplex;
    }

    @Override
    public String toString() {
        return "Road{" +
                "id=" + id +
                ", length=" + length +
                ", speedLimit=" + speedLimit +
                ", fromCrossId=" + fromCrossId +
                ", toCrossId=" + toCrossId +
                ", isDuplex=" + isDuplex +
                '}';
    }
}

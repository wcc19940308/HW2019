package com.huawei.app.model;

/**由算法返回的小车形式的路径(独立的链表)
 * Created by DanLongChen on 2019/3/14
 **/
public class CarPassPath {
    /**
     * 存放当前的节点ID
     */
    private int curCrossId;
    /**
     *吓一跳道路ID
     */
    private int nextRoadID;
    /**
     *路径的下一个节点
     */
    CarPassPath next;
    public CarPassPath(int curCrossId,int nextRoadID,CarPassPath next){
        this.curCrossId=curCrossId;
        this.nextRoadID=nextRoadID;
        this.next=next;
    }

    public int getCurCrossId() {
        return curCrossId;
    }

    public void setCurCrossId(int curCrossId) {
        this.curCrossId = curCrossId;
    }

    public int getNextRoadID() {
        return nextRoadID;
    }

    public void setNextRoadID(int nextRoadID) {
        this.nextRoadID = nextRoadID;
    }

    public CarPassPath getNext() {
        return next;
    }

    public void setNext(CarPassPath next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return getCurCrossId()+" "+getNextRoadID()+"   "+getNextRoadID();
    }
}

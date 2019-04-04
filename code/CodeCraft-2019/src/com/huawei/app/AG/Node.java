package com.huawei.app.AG;

/**
 * Created by DanLongChen on 2019/3/15
 **/
public class Node {
    private int CrossId;
    private Node parent;
    private int F;
    private int G;
    private int H;
    public Node(int crossId){
        this.CrossId=crossId;
    }

    public int getCrossId() {
        return CrossId;
    }

    public void setCrossId(int crossId) {
        CrossId = crossId;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getF() {
        return F;
    }

    public void setF(int f) {
        F = f;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getH() {
        return H;
    }

    public void setH(int h) {
        H = h;
    }
}

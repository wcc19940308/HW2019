package com.huawei.app.graph;


import com.huawei.app.model.Cross;
import com.huawei.app.model.Road;

/**
 * 定义邻接表类型
 * 需要一个存储自身结点
 * Created by Lettino on 2019/3/12
 */
public class Edge {
    /**
     * 出发定点
     */
    private Cross source;
    /**
     *  出发目的
     */
    private Cross target;
    /**
     * 道路代表边
     */
    private Road road;
    private int weight; ;//代价

    public Edge(Cross source, Cross target,Road road){
        this.source = source;
        this.target = target;
        this.weight = road.getLength()*road.getChanelNumber();
        this.road = road;
    }

    public Cross getSource() {
        return source;
    }

    public void setSource(Cross source) {
        this.source = source;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }


    public Cross getTarget() {
        return target;
    }

    public void setTarget(Cross target) {
        this.target = target;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source.toString() +
                ", target=" + target.toString() +
                ", road=" + road.toString() +
                ", weight=" + weight +
                '}';
    }
}

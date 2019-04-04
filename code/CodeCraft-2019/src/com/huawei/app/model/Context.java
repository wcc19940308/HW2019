package com.huawei.app.model;

import com.huawei.app.graph.Graph;

import java.util.Map;

/**用于保存当前时刻的全局信息
 * Created by DanLongChen on 2019/3/14
 **/
public class Context {
    /**
     * 保存全局地图
     */
    private Graph graph=null;
    /**
     * 当前车的全局信息
     */
    private Map<Integer,Car> carMap=null;
    /**
     * 当前车的状态信息，对应于车辆
     */
    private Map<Integer,CarStatus> carStatusMap=null;
    /**
     * 当前道路的状态信息，对应于车辆
     */
    private Map<Integer,Road> roadMap = null;

    /**
     * 当前的路口信息
     */
    private Map<Integer,Cross> crossMap=null;



    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Map<Integer, Car> getCarMap() {
        return carMap;
    }

    public void setCarMap(Map<Integer, Car> carMap) {
        this.carMap = carMap;
    }

    public Map<Integer, CarStatus> getCarStatusMap() {
        return carStatusMap;
    }

    public void setCarStatusMap(Map<Integer, CarStatus> carStatusMap) {
        this.carStatusMap = carStatusMap;
    }


    public Map<Integer, Road> getRoadMap() {
        return roadMap;
    }

    public void setRoadMap(Map<Integer, Road> roadMap) {
        this.roadMap = roadMap;
    }

    public Map<Integer, Cross> getCrossMap() {
        return crossMap;
    }

    public void setCrossMap(Map<Integer, Cross> crossMap) {
        this.crossMap = crossMap;
    }
}

package com.huawei.app.AG;


import com.huawei.app.graph.Graph;
import com.huawei.app.model.Car;
import com.huawei.app.model.CarPassPath;

/**定义搜索算法接口
 * Created by DanLongChen on 2019/3/14
 **/
public interface Seek {
    /**
     * 启动搜索算法
     * @param graph 全局的地图
     * @param car   当前要调度的小车
     * @param startId   汽车的起始点ID
     * @param endId     汽车的终点路口ID
     * @return  汽车路径链表
     */
    public CarPassPath startSeek(Graph graph, Car car, int startId, int endId);
}

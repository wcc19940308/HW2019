package com.huawei.app.utils;

import com.huawei.app.graph.Graph;
import com.huawei.app.model.Car;
import com.huawei.app.model.Context;
import com.huawei.app.model.Cross;
import com.huawei.app.model.Road;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**初始化全局信息类
 * Created by DanLongChen on 2019/3/14
 **/
public class InitUtils {

    /**
     * 初始化全局信息
     * @param carPath   传入的车的文件路径
     * @param roadPath  传入的道路的文件路径
     * @param crossPath 传入的路口的文件路径
     * @param context   传入的context
     */
    public static void doInit(String carPath, String roadPath, String crossPath, Context context){
        /**
         * 从文件中获取小车，道路，路口的信息，加载到全局变量中
         */
        List<Car> cars=FileUtils.formatCat(FileUtils.formatInput(carPath));
        List<Road> roads=FileUtils.formatRoad(FileUtils.formatInput(roadPath));
        List<Cross> crosses=FileUtils.formatCross(FileUtils.formatInput(crossPath));
        /**
         * 格式转化
         */
        Map<Integer,Car> carMap=cars.stream().collect(Collectors.toMap(Car::getCarId, Function.identity(),(keys1,keys2)->keys1));
        Map<Integer,Road> roadMap=roads.stream().collect(Collectors.toMap(Road::getID, Function.identity(),(keys1,keys2)->keys1));
        Map<Integer,Cross> crossMap=crosses.stream().collect(Collectors.toMap(Cross::getCrossId, Function.identity(),(keys1,keys2)->keys1));
        /**
         * 进行加载操作
         */
        Graph graph = new Graph(crosses, roads);
        context.setGraph(graph);
        context.setCarMap(carMap);
        context.setRoadMap(roadMap);
        context.setCrossMap(crossMap);

    }
}

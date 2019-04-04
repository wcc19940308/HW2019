package com.huawei.app.graph;



import com.huawei.app.model.Cross;
import com.huawei.app.model.Road;

import java.util.*;

/**
 * Created by Lettino on 2019/3/12
 * 以crossID出发点的邻接表
 * 目前还没有想好边上的权重用来表示什么  所以暂时设置为-1
 */
public class Graph {
//    private Map<String, Vertex> vertices;// 顶点ID作为KEY，value为顶点类里面包含这个顶点的所有路径，和这个顶点连接的所有
//    Map<Cross,LinkedList<Edge>> startEdge ; //cross 直接当做顶点
//    public List<List<Edge>> GlobalList = new LinkedList<>();
    /**
     * 顶点Map Integer是CrossID
     */
    public static final Map<Integer, List<Edge>> globalGraph = new LinkedHashMap<>();



    /**
     * 存储cross
     */
    private Map<Integer, Cross> crossMap = new HashMap<>();
    /**
     * 记录边的数量
     */
    private int eCut = 0;



    private int nodeCut = 0;

    public Graph(List<Cross> crossList, List<Road> roadList) {
        initNode(crossList);
        initEdge(roadList);
    }

    public void initNode(List<Cross> crossList) {
        //图的初始化
        for (Cross node : crossList) {
            nodeCut++;
            LinkedList<Edge> nodeList = new LinkedList<>();
            globalGraph.put(node.getCrossId(), nodeList);
            crossMap.put(node.getCrossId(), node);
        }
    }

    public void initEdge(List<Road> roadList) {
        int i = 0;
        for (Road road : roadList) {
            i++;
            if (road == null) {
                break;
            }
            Cross source = crossMap.get(road.getFromCrossId());
            Cross target = crossMap.get(road.getToCrossId());
            if (source.getCrossId() == target.getCrossId() ) {
                System.out.println("边的顶点序号无效，退出运行");
                return;
            }
            addEdge(source, target, road);
            if(road.isDuplex()){
                addEdge(target,source, road);
            }
        }
    }

    public void addEdge(Cross source, Cross target, Road road) {
        int v1 = source.getCrossId();
        int v2 = target.getCrossId();
        Edge theEdge = new Edge(source, target, road);
        if (v1 < 0 || v2 < 0 || v1 == v2) {
            System.out.println("边的顶点序号无效，退出运行");
            System.exit(0);
        }
        List<Edge> edgeList = globalGraph.get(source.getCrossId());
        edgeList.add(theEdge);
        eCut++;
    }

    /**
     * <p>
     * 删除边， 取得两个顶点即可:
     * 查找原顶点对应的邻接表的边，边的另一端等于目标顶点就是要删除的边
     * <p>
     *
     * @param src  原顶点
     * @param dest 目标顶点
     * @return
     */
    public Edge delEdge(String src, String dest) {
        return null;
    }


    public void disPlayGraph() {
        int i=0;
        for (Integer id: globalGraph.keySet()) {
            System.out.println("这是节点为"+id+"的边：");
            List<Edge> edgeList = globalGraph.get(id);
            System.out.println(edgeList.size());
            for (Edge edge: edgeList ) {
                System.out.println(edge.toString());
                i++;
            }
        }
        System.out.println("边的数量："+i);
    }

    /**
     * 根据两个端点来获取道路的ID
     * @param from
     * @param to
     * @return 若没有找到则返回-1
     */
    public int findRoadIdByCrossId(int from,int to){
        System.out.println(from);
        List<Edge> list = globalGraph.get(from);
        for (Edge edge:list){
            if(edge.getTarget().getCrossId()==to){
                return edge.getRoad().getID();
            }
        }
        return -1;
    }

    public int geteCut() {
        return eCut;
    }

    public void seteCut(int eCut) {
        this.eCut = eCut;
    }

    public int getNodeCut() {
        return nodeCut;
    }

    public void setNodeCut(int nodeCut) {
        this.nodeCut = nodeCut;
    }

    public Map<Integer, Cross> getCrossMap() {
        return crossMap;
    }

    public void setCrossMap(Map<Integer, Cross> crossMap) {
        this.crossMap = crossMap;
    }

}

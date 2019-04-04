package com.huawei.app.AG;

import com.huawei.app.graph.Edge;
import com.huawei.app.graph.Graph;
import com.huawei.app.model.Car;
import com.huawei.app.model.CarPassPath;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by DanLongChen on 2019/3/15
 **/
public class AStart implements Seek{
    @Override
    public CarPassPath startSeek(Graph graph, Car car, int startId, int endId) {
        List<Node> openList=new ArrayList<Node>();
        List<Node> closeList=new ArrayList<Node>();
        Node endNode=new Node(endId);
        openList.add(new Node(startId));
        while (!openList.isEmpty()){
            Node curNode=findMinFNOde(openList);
            openList.remove(curNode);
            closeList.add(curNode);
            List<Node> temp=findNeiborNode(graph,curNode.getCrossId(),curNode);
            for(Node node:temp){
                if(openList.contains(node)){

                }else{

                }
            }
            if (find(openList,endNode)!=null){
                System.out.println("找到了");
            }

        }

        return null;
    }

    /**
     * 找到openList中代价最小的节点
     * @param list
     * @return
     */
    public Node findMinFNOde(List<Node> list){
        Node temp=list.get(0);
        for(int i=1;i<list.size();i++){
            if(list.get(i).getF()<temp.getF()){
                temp=list.get(i);
            }
        }
        return temp;
    }

    /**
     * 找到当前节点的所有邻居
     * @param
     * @return  返回邻居列表
     */
    public List<Node> findNeiborNode(Graph graph,int curCrossId,Node parent){
        List<Edge> list=graph.globalGraph.get(curCrossId);
        List<Node> result=new ArrayList<Node>();
        for(Edge edge:list){
            if(edge!=null){
                Node temp=new Node(edge.getTarget().getCrossId());
                temp.setParent(parent);
                result.add(temp);
            }
        }
        return result;
    }

    /**
     * 判断终点是否在当前的openList中，若在则返回终点
     * @param list
     * @param endNode
     * @return
     */
    public Node find(List<Node> list, Node endNode){
        for(Node node:list){
            if(node.getCrossId()==endNode.getCrossId()){
                return node;
            }
        }
        return null;
    }

}

package com.huawei.app.AG;


import com.huawei.app.graph.Edge;
import com.huawei.app.graph.Graph;
import com.huawei.app.model.Car;
import com.huawei.app.model.CarPassPath;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DanLongChen on 2019/3/13
 **/
public class DJ implements Seek {
    @Override
    public CarPassPath startSeek(Graph graph, Car car, int startId, int endId) {
        int nodeNum = graph.getNodeCut();//获取图中的顶点数
        int edgeNum = graph.geteCut();//获取边的数目
        int[][] G = new int[nodeNum+1][nodeNum+1];//获取整个图的邻接矩阵，方便后面更新dis
        int[][] C = new int[nodeNum+1][nodeNum+1];//对应于每一条道路的容量，容量可能需要实时更新，当容量《=1的时候表示该道路不可通行，要换一条道路
        int[] dis = new int[nodeNum+1];//对应start点到其他所有顶点的距离，这里限制一个条件，搜索到指定的点的时候就终止
        Arrays.fill(dis,Integer.MAX_VALUE);//初始化为不可达
        boolean[] flag = new boolean[nodeNum+1];//反应当前路口是否已经被搜索过
        int start=0;//下一个开始搜索的节点
        int[] path=new int[nodeNum+1];//记录到终点走过的路口
        Arrays.fill(path,-1);
        /**
         * 初始化当前的图的邻接矩阵，初始化容量图
         */
        for (int i = 1; i <= nodeNum; i++) {
            List<Edge> list = graph.globalGraph.get(i);//以文件读入的ID为准
            for (int j = 1; j <=nodeNum; j++) {
                for (Edge edge : list) {
                    if (edge.getTarget().getCrossId() == j) {
                        /**
                         * 路径的代价=道路长度+小车速度/道路限速
                         */
                        G[i][j] = edge.getRoad().getLength()*(car.getCarSpeedLimit()/edge.getRoad().getSpeedLimit());
                        C[i][j]=edge.getWeight();
                    } else {
                        G[i][j] = 10000;//防止越界
                        C[i][j]=-1;//-1则表示容量没有了，车不能进入
                    }
                }
            }
        }

        /**
         * 初始化当前节点到其他节点的dis
         */
        for(int i = 1;i<=nodeNum;i++){
            dis[i]=G[startId][i];
            flag[i]=false;
            if(dis[i]!=10000){//找到以源点为pre的点
                path[i]=startId;
            }
        }
//        List<Edge> list = graph.globalGraph.get(startId);//当前起始点为起点的边的集合
//        for(Edge edge:list){
//            dis[edge.getTarget().getCrossId()-1]=edge.getRoad().getLength();
//        }
        /**
         * DJ算法主体部分
         */
        flag[startId]=true;

        for(int i=1;i<=nodeNum;i++){
            int min=Integer.MAX_VALUE;
            start=-1;
            for(int j=1;j<=nodeNum;j++){
                if(flag[j]==false && dis[j]<min){
                    min=dis[j];
                    start=j;
                }
            }
            if(start==-1){
                break;
//                throw new IllegalArgumentException("start < -1");
            }
            flag[start]=true;
            for(int j=1;j<=nodeNum;j++){
                if(dis[j]>dis[start]+G[start][j]){
                    dis[j]=dis[start]+G[start][j];
                    path[j]=start;
                }
            }
        }


        CarPassPath next = new CarPassPath(endId, -1, null);
        int pre=-1,now=endId;
        while(path[now]!=startId){
            pre=path[now];
            if(pre==-1){
                break;
            }
            next=new CarPassPath(pre,graph.findRoadIdByCrossId(pre,now),next);
            now=pre;
        }
        next=new CarPassPath(startId,graph.findRoadIdByCrossId(startId,now),next);
        return next;
    }


//    public CarPassPath startSeek1(Graph graph, Car car, int startId, int endId) {
//        int nodeNum = graph.getNodeCut();//获取图中的顶点数
//        int edgeNum = graph.geteCut();//获取边的数目
//        int[] dis = new int[nodeNum+1];//对应start点到其他所有顶点的距离，这里限制一个条件，搜索到指定的点的时候就终止
////        LinkedList<Integer> path = new LinkedList<>();
//        for (int i=1;i<nodeNum+1;i++) {
//            dis[1] = Integer.MAX_VALUE;
//        }

        /**
         *
         * @param startIndex dijkstra遍历的起点节点下标
         * @param destIndex dijkstra遍历的终点节点下标
         */
//        public void dijkstraTravasal(int startIndex,int destIndex)
//        {
//            int start = vertexList.get(startIndex);
//            int dest = vertexList.get(destIndex);
//            String path = "["+dest+"]";
//
//            setRoot(start);
//            updateChildren(vertexList.get(startIndex));
//
//            int shortest_length = dest.getAdjuDist();
//
//            while((dest.getParent()!=null)&&(!dest.equals(start)))
//            {
//                path = "["+dest.getParent().getName()+"] --> "+path;
//                dest = dest.getParent();
//            }
//
//            System.out.println("["+vertexList.get(startIndex).getName() +"] to ["+
//                    vertexList.get(destIndex).getName()+"] dijkstra shortest path :: "+path);
//            System.out.println("shortest length::"+shortest_length);
//        }
//
//        /**
//         * 从初始节点开始递归更新邻接表
//         * @param v
//         */
//        private void updateChildren(Integer v)
//        {
//            if (v==null) {
//                return;
//            }
//
//            if (ver_edgeList_map.get(v)==null||ver_edgeList_map.get(v).size()==0) {
//                return;
//            }
//            //用来保存每个可达的节点
//            List<Vertex> childrenList = new LinkedList<Graph.Vertex>();
//            for(Edge e:ver_edgeList_map.get(v))
//            {
//                Vertex childVertex = e.getEndVertex();
//
//                //如果子节点之前未知，则进行初始化，
//                //把当前边的开始点默认为子节点的父节点，长度默认为边长加边的起始节点的长度，并修改该点为已经添加过，表示不用初始化
//                if(!childVertex.isKnown())
//                {
//                    childVertex.setKnown(true);
//                    childVertex.setAdjuDist(v.getAdjuDist()+e.getWeight());
//                    childVertex.setParent(v);
//                    childrenList.add(childVertex);
//                }
//
//                //此时该子节点的父节点和之前到该节点的最小长度已经知道了，则比较该边起始节点到该点的距离是否小于子节点的长度，
//                //只有小于的情况下，才更新该点为该子节点父节点,并且更新长度。
//                int nowDist = v.getAdjuDist()+e.getWeight();
//                if(nowDist>=childVertex.getAdjuDist())
//                {
//                    continue;
//                }
//                else {
//                    childVertex.setAdjuDist(nowDist);
//                    childVertex.setParent(v);
//                }
//            }
//
//            //更新每一个子节点
//            for(Vertex vc:childrenList)
//            {
//                updateChildren(vc);
//            }
//        }

//    }
}

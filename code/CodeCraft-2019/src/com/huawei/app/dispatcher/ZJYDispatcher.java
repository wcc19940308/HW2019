package com.huawei.app.dispatcher;

import com.huawei.app.Config;
import com.huawei.app.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Lettino on 2019/3/16
 */
public class ZJYDispatcher implements Dispatcher {

    private Context ctx = null;
    private Map<Integer,Car> cars = null;
    private Map<Integer,Road> roads = null;
    private Map<Integer,Cross> crosses = null;
    private Map<Integer,Integer> crossReIdx=null;
    private List<Integer>  crossIdx =null;
    private int[]  crossStart = null;
    private int[] crossStop = null;
    private int[] crossPassed = null;

    // 道路图
    private Road[][] graph = null;
    // 权重图
    private int[][] G = null;

    // 更新
    private int UPDATE_DELAY = 10;

    // 当前系统时间
    private int curSAT = -1;

    // 用于记录车的行驶路线的链表
    private Map<Integer, CarPathNode> initCarPath = null;

    private class CarPathNode{
        int curCrossId;
        int nextRoadId;
        CarPathNode next;

        CarPathNode(int crossId, int roadid, CarPathNode next) {
            this.curCrossId = crossId;
            this.nextRoadId = roadid;
            this.next = next;
        }
    }


    public ZJYDispatcher(Context ctx) {
        this.ctx = ctx;
        cars = ctx.getCarMap();
        roads = ctx.getRoadMap();
        crosses = ctx.getCrossMap();
        createCrossIdx(crosses.keySet());
        initCarPath = new HashMap<>();
        crossStart = new int[crosses.size()];
        crossStop = new int[crosses.size()];
        crossPassed = new int[crosses.size()];
    }



    /**
     *  初始化将所有车初始方法缓存
     */
    public void init() {
        // 初始化图
        graph = new Road[crosses.size()][crosses.size()];
        G = new int[crosses.size()][crosses.size()];
        roads.values().forEach(road -> {
            int i = cIdx(road.getFromCrossId());
            int j = cIdx(road.getToCrossId());
            graph[i][j] = road;
            if (road.isDuplex()) {
                graph[j][i] = road;
            }
        });
    }

    private void createCrossIdx(Collection<Integer> crossIds){
        Map<Integer,Integer> res = new HashMap<>();
        List<Integer> ids = crossIds.stream()
                .sorted((a, b) -> Integer.compare(a, b))
                .collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < ids.size(); i++) {
            res.put(ids.get(i), i);
        }
        crossIdx = ids;
        crossReIdx = res;
    }
    // 找到所有存在的道路，并且判断小车现在在该路上的行驶代价
    private void updateG(CarStatus cs) {
        for (int i = 0; i < G.length; i++) {
            for (int j = 0; j < G.length; j++) {
                if (graph[i][j] == null) {
                    G[i][j] = Integer.MAX_VALUE;
                } else {
                    Road road = graph[i][j];
                    if (road.getID() == cs.curRoadId) {
                        // 因为这两车要从这条道路上开往别的道路了，所以将车当前行驶的这条路的代价设置为无穷大，表示不能选择当前的路了
                        G[i][j] = Integer.MAX_VALUE;
                        G[j][i] = Integer.MAX_VALUE;
                    } else {
                        // 判断其他联通的道路的代价
                        G[i][j] = cost(road, cs);
                    }
                }
            }
        }
    }
    /**
     * idx
     * @param crossId
     * @return
     */
    private int cIdx(int crossId) {
        return crossReIdx.get(crossId);
    }

    private int cReId(int cidx) {
        return crossIdx.get(cidx);
    }

    /**
     * 返回利用当前耗费,这辆车在这条路上跑的耗费
     * 根据道路上车道中的车的数量来判断当前的代价
     * TODO 这里可以优化，对于代价的判别，而且这里没有判断道路的单双向
     * @param road
     * @param cs
     * @return
     */
    private int cost(Road road,CarStatus cs) {
        int spd = Math.min(road.getSpeedLimit(), cs.car.getCarSpeedLimit());
        int baseTime = (int) Math.floor(road.getLength() * 1.0 / spd) + 1;
        // int roadCost = road.getLength() * (cs.getCar().getCarSpeedLimit() / road.getSpeedLimit());
        int flag = 0;
        int cout = 0;
        CarStatus[] cc = null;
        Chanel[] rcs = road.getOutCrossChannels(road.getFromCrossId());
        int roadLength = road.getLength();
        int chanelNum = road.getChanelNumber();
        int allChanelNum = roadLength * chanelNum;
        for(Chanel rc :rcs) {
            cc = rc.getCarStatus();
            for(CarStatus ct:cc)
            {
                if (ct != null && ct.carId >= 0) {
                    cout++;
                }
            }
        }
        if (Config.carNumber == 3000) {
            return baseTime + cout * 2;
        } else {
            return baseTime + (int)(cout * 2.5);
            //return baseTime + (cout / allChanelNum) * 100;
        }
        // return baseTime + cout;
    }

    // TODO 路径判断算法可以优化
    // 根据实时更新的图的代价往CarPath中添加驶过的道路
    private CarPathNode dij(Car car,int oriCrossId,int desCrossId) {

        if (oriCrossId == desCrossId) {
            System.err.println(car.getDesCrossId() + " " + car.getOriCrossId());
        }
        if (car == null || oriCrossId == desCrossId) {

            throw new IllegalArgumentException("car==null or oriCrossId==desCrossId");
        }
        int[] dist = new int[crosses.size()];
        int[] path = new int[crosses.size()];

        int ori = cIdx(oriCrossId);
        int des = cIdx(desCrossId);

        // 初始化计算
        Set<Integer> set = new HashSet<>(crosses.size());
        for (int i = 0; i < crosses.size(); i++) {
            if (i != ori) {
                set.add(i);
            }
        }
        Arrays.fill(path, -1);
        for (int i = 0; i < G.length; i++) {
            dist[i] = G[ori][i];
            if (graph[ori][i] != null) {
                path[i] = ori;
            }
        }

        out:
        for (int i = 1; i < crosses.size(); i++) {
            int tmp = Integer.MAX_VALUE;
            int k = -1;
            for (int v : set) {
                if (dist[v] < tmp) {
                    tmp = dist[v];
                    k = v;
                }
            }
            ///// 如果这里k<-1 表示遇到死路口 ////////
            if (k < 0) {
                throw new IllegalArgumentException("k<0!");
            }
            if (k == des) {
                break out;// 已经寻找到结尾
            }
            set.remove(k);
            for (int v : set) {
                if (G[k][v] < Integer.MAX_VALUE &&
                        (tmp = dist[k] + G[k][v]) < dist[v]) {
                    dist[v] = tmp;
                    path[v] = k;//更新父节点
                }
            }
        }// end

        // 路径恢复
        // 创建一个尾节点
        CarPathNode next = new CarPathNode(cReId(des), -1, null);
        int par = -1, son = des;
        while ((par = path[son]) != ori) {
            next = new CarPathNode(cReId(par),
                    graph[par][son].getID(), next);
            son = par;
        }
        next = new CarPathNode(cReId(par),
                graph[par][son].getID(), next);

        return next;
    }

    // 返回carId车接下来要进入的RoadId
    @Override
    public int onScheduling(int carId, int curCrossId) {

        // 记录每个路口所经过的次数,应该是用来判断拥塞程度的
        crossPassed[cIdx(curCrossId)]++;

        CarStatus cs = ctx.getCarStatusMap().get(carId);
        Car car = cs.car;
        // 注意已经到达目的地，返回-1
        if (curCrossId == car.getDesCrossId()) {
            return -1;
        }

        CarPathNode cur;
        if ((cur = initCarPath.get(carId)) == null) {
            // 先更新权重图，在根据权重判断行驶的路线
            updateG(cs);
            cur = dij(car, curCrossId, car.getDesCrossId());
            initCarPath.put(carId, cur);
        }
        // 找到车的行驶路线中的路口Id是当前的路口Id
        while (cur != null && cur.curCrossId != curCrossId) {
            cur = cur.next;
        }
        if (cur == null) {

            throw new IllegalArgumentException("CrossId:" + curCrossId + " is not in carpath");
        }
        return cur.nextRoadId;
    }

    // TODO 关于车辆是否可以上路，应该可以做进一步优化
    @Override
    public boolean onTryStart(int carId, int crossId,int remcot) {
        return remcot < Config.carNumber;
    }

    @Override
    public boolean onStop(int carId, int crossId) {
//        System.err.println("Car:"+carId+"->Cross:"+crossId+"->time:");
        crossStop[cIdx(crossId)]++;
        return false;
    }

    public String showPath(int carId) {
        StringBuffer sb = new StringBuffer();
        CarPathNode node = initCarPath.get(carId);
        while (node != null) {
            sb.append("(" + node.curCrossId + "," + node.nextRoadId + ")->");
            node = node.next;
        }
        return sb.toString();
    }

    public void showCal() {
        System.err.println(Arrays.toString(crossStart));
        System.err.println(Arrays.toString(crossStop));
        System.err.println(Arrays.toString(crossPassed));
        double sum = 0.0;
        for (int a : crossPassed) {
            sum += a * 1.0 / crossPassed.length;
        }
        System.err.println(sum);
    }

    @Override
    public void onStart(int carId, int crossId) {
        crossStart[cIdx(crossId)]++;
    }
}

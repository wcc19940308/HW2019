注意事项：
1.对于道路上的车的遍历顺序是由第一排到最后一排进行遍历的（出道路处为第一排，入道路处为最后一排，代码中，curChannelLocal大的是出道路处）

2.道路中所有等待状态的车辆（路口，道路）是统一处理的。即按照路口ID升序进行调度各个路口，路口内各道路按道路ID升序进行调度（路口只调度道路出路口方向）

3.仔细研读任务书第20页，等待状态也是有优先级的

4.当需要通过路口转向的时候需要实施规划器，即每次都要调度dispatcher.onScheduling(cs.carId, cs.tagCrossId);

5.一个道路上同一时刻只能有1辆车处于准备通过路口的状态（BLOCK_SCHEDULING），而后面的车都是等待状态（SCHEDULING）
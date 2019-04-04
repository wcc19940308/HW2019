package com.huawei.app;

import com.huawei.app.dispatcher.KingDispatcher;
import com.huawei.app.dispatcher.ZJYDispatcher;
import com.huawei.app.model.*;
import com.huawei.app.utils.FileUtils;
import com.huawei.app.utils.InitUtils;


import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lettino on 2019/3/13
 */
public class Application {

    public static void run(String[] args) {

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];

//        System.out.println("carPath = " + carPath + "\nroadPath = " + roadPath +
//                "\ncrossPath = " + crossPath + "\nanswerPath = " + answerPath);

        Context context = new Context();

        InitUtils.doInit(carPath,roadPath,crossPath,context);


        System.out.println("Load Finish!\ncars.size="+context.getCarMap().size()+
                "\nroads.size="+context.getRoadMap().size()+"\ncrosses.size="+context.getCrossMap().size());

        if (context.getCrossMap().size() <= 143) {
            Config.carNumber = 3000;
        } else {
            Config.carNumber = 1250; // 这里好像只能1200
        }

        Instant now = Instant.now();

        // 完成cars、roads、crosses的一些基础工作
        preprocess(context);
        ZJYDispatcher dispatcher=new ZJYDispatcher(context);
        // 创建模拟器
        NewSimulator2 sim = new NewSimulator2(context);
        // 注册规划器
        sim.registerPlanner(dispatcher);
        // 初始化图
        dispatcher.init();
        // 初始化
        sim.init();
        sim.run();
        dispatcher.showCal();
        long runingtime = Duration.between(now, Instant.now()).toMillis();

        System.out.println("running time:"+runingtime);

        // 记录所有车辆的行程
        FileUtils.saveResult(answerPath, context.getCarStatusMap().values());
    }

    private static void preprocess(Context ctx) {

        // 设置所有Cross中可以驶出的roadId
        ctx.getCrossMap().values().stream()
                .forEach(v -> v.setConnOutRoadIds(ctx.getRoadMap()));
    }
}

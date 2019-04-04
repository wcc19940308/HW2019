package com.huawei.app.AG;

import com.huawei.app.model.Car;
import com.huawei.app.model.CarPassPath;
import com.huawei.app.model.Context;
import com.huawei.app.utils.InitUtils;

/**
 * Created by DanLongChen on 2019/3/16
 **/
public class TestAJ {
    public static void main(String[] args) {
        String basePath="C:\\Users\\Administrator\\Desktop\\Config";
        String carPath=basePath+"\\car.txt";
        String roadPath=basePath+"\\road.txt";
        String crossPath=basePath+"\\cross.txt";
        String answerPath=basePath+"\\answer.txt";
        System.out.println("carPath = " + carPath + "\nroadPath = " + roadPath +
                "\ncrossPath = " + crossPath + "\nanswerPath = " + answerPath);

        Context context = new Context();

        InitUtils.doInit(carPath,roadPath,crossPath,context);

        Seek DJ=new DJ();
        Car car = context.getCarMap().get(10001);
        if (car == null) {
            System.out.println("没找到这辆车");
        }
//        context.getGraph().disPlayGraph();
        CarPassPath now= DJ.startSeek(context.getGraph(),car,car.getOriCrossId(),car.getDesCrossId());

        while (now!=null){
            System.out.print("->"+now.getCurCrossId());
            now = now.getNext();
        }

    }
}

package com.huawei.app;

import com.huawei.app.AG.DJ;
import com.huawei.app.AG.Seek;
import com.huawei.app.graph.Graph;
import com.huawei.app.model.Car;
import com.huawei.app.model.CarPassPath;
import com.huawei.app.model.Context;
import com.huawei.app.utils.InitUtils;

/**
 * Created by DanLongChen on 2019/3/162
 **/
public class Test {
    public static void main(String[] args) {
        String basePath="C:\\Users\\Administrator\\Desktop\\华为高分版本\\config\\map-exam2";
        String carPath=basePath+"\\car.txt";
        String roadPath=basePath+"\\road.txt";
        String crossPath=basePath+"\\cross.txt";
        String answerPath=basePath+"\\answer.txt";
        String[] values={carPath,roadPath,crossPath,answerPath};
        Application.run(values);
    }
}

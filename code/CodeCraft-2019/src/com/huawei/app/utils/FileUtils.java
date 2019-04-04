package com.huawei.app.utils;


import com.huawei.app.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @Author wcc
 * @Description 用于处理输入输出文件的工具类
 * @Date 2019-03-14
 *
 */
public class FileUtils {

    // 格式化输入，去掉#()的内容
    public static List<String> formatInput(String path) {
        List<String> res = new ArrayList<>();
        try {
            res = Files.readAllLines(Paths.get(path),
                    StandardCharsets.UTF_8);
            res = res.stream()
                    .filter(v -> !v.contains("#") && v.length() > 2)
                    .map(v -> v.replaceAll("\\(|\\)", ""))
                    .map(v -> v.replaceAll(" ", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int[] strs2ints(String[] strings) {
        int[] res = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            res[i] = Integer.parseInt(strings[i]);
        }
        return res;
    }

    // 格式化文本信息为具体类型的对象,返回List
    public static List<Car> formatCat(List<String> Cars) {
        List<Car> list = new LinkedList<>();
        Cars.stream().map(v->v.split(",")).forEach((v)->{
            int[] car = strs2ints(v);
            list.add(new Car(car));
        });
        return list;
    }

    public static List<Road> formatRoad(List<String> Roads) {
        List<Road> list = new LinkedList<>();
        Roads.stream().map(v->v.split(",")).forEach((v)->{
            int[] road = strs2ints(v);
            list.add(new Road(road));
        });
        return list;
    }

    public static List<Cross> formatCross(List<String> Crosses) {
        List<Cross> list = new LinkedList<>();
        Crosses.stream().map(v->v.split(",")).forEach((v)->{
            int[] cross = strs2ints(v);
            list.add(new Cross(cross));
        });
        return list;
    }
    // 将输出的结果保存到相应的路径下
    public static void saveResult(String resultPath, Collection<CarStatus> results) {
        String head = "#(carId,StartTime,RoadId...)";
        head += "\r\n";
        try {
            File file = Paths.get(resultPath).toFile();
            if (!file.exists()) file.createNewFile();
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
            bfw.write(head);
            // TODO 根据result的类型自行修改
            for (CarStatus result : results) {
                bfw.write("(" + result.carId + ", " + result.relStartTime + ", ");
                List<Integer> list = result.passedRoadRec;
                for (int i = 0; i < list.size() - 1; i++) {
                    bfw.write(list.get(i)+", ");
                }
                bfw.write(list.get(list.size() - 1) + ")");
                bfw.write("\r\n");
            }
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}




package com.huawei.app.model;

import java.util.LinkedList;

/**
 * Created by DanLongChen on 2019/3/14
 **/
public class FinalResult {
    private LinkedList<Result> list=new LinkedList<Result>() ;

    public LinkedList<Result> getList() {
        return list;
    }

    public void setList(LinkedList<Result> list) {
        this.list = list;
    }
}

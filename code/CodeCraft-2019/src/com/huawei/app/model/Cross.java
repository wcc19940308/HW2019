package com.huawei.app.model;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Lettino on 2019/3/11
 */
public class Cross {
    //路口id,道路id,道路id,道路id,道路id
    private int crossId;
    /**
     * c初始化交叉路口的道路
     * 用来存储逆时针道路ID
     * 按顺时针排序Road的id,
     */
    private int[] connRoadIds;
    /**
     *  注意这里的ID是指可以从这个路口出去的Road
     *     当无出去的路时，为-1;
     */
    private int[] connOutRoadIds= {-1,-1,-1,-1};
    // 无相连的Cross时，为-1;

    public Cross(int crossId, int[] meetingRoad) {
        this.crossId = crossId;
        connRoadIds = meetingRoad;
    }

    public Cross(int[] args) {
        this.crossId = args[0];
        this.connRoadIds = Arrays.copyOfRange(args, 1, 5);
    }



    /**
     *
     * >根据当前车辆行驶路径相对于路口的位置，计算下向前、向左、向右的RoadId
     * > 可以通过getDirectionByRoadId 计算roadDirectInCross
     * >若放回-1 表示选择的方向上无Road或者无效输入
     * @param
     * @return
     */
    public int getNextRoadId(int roadDirectInCross, int needNextDriveDirect) {
        if(roadDirectInCross<Direction.N.getDirection()||roadDirectInCross>Direction.W.getDirection()||
                needNextDriveDirect<DriveDirection.LEFT.getAction()||needNextDriveDirect<DriveDirection.RIGHT.getAction())
        {
            // 无效方向
            return -1;
        }
        else
        {
            return connOutRoadIds[(roadDirectInCross+6+needNextDriveDirect)%4];
        }
    }


    /**
     *  >通过路口的roadId计算该Road相对于路口的位置
     *  >例如知道路在上方
     * @param roadId
     * @return dirction
     *
     */
    public int getDirectionByRoadId(int roadId) {
        int res = 0;
        for(;res<4;res++) {
            if(connRoadIds[res]==roadId) {
                break;
            }
        }
        if (res==4)
        {
            throw new IllegalArgumentException("roadId not in connRoadIds");
        }
        return res;
    }


    /**
     * >根据进入的Road的相对于路口的位置和出去的RoadId判断，该行驶属于向左、直行、向右中的哪种
     * >通过 getDirectionByRoadId计算过inRoadDirection
     * @param inRoadDirection
     * @param outRoadId
     * @return DriveDirection.LEFT RIGHT FORWARD
     */
    public int getTurnDirection(int inRoadDirection,int outRoadId) {
        if(connOutRoadIds[(inRoadDirection+1)%4]==outRoadId)
        {
            return DriveDirection.LEFT.getAction();
        }
        else if(connOutRoadIds[(inRoadDirection+2)%4]==outRoadId)
        {
            return DriveDirection.FORWARD.getAction();
        }
        else if(connOutRoadIds[(inRoadDirection+3)%4]==outRoadId)
        {
            return DriveDirection.RIGHT.getAction();
        }
        else
        {
            throw new IllegalArgumentException("outRoadId not in connOutRoadIds");
        }
    }


    public int getTurnDireByRoad(int inRoadId,int outRoadId) {
        if (inRoadId < 0 || outRoadId < 0 || inRoadId == outRoadId) {
            throw new IllegalArgumentException("Error RoadId:inRoad=" + inRoadId + ",outRoad=" + outRoadId);
        }
        return getTurnDirection(getDirectionByRoadId(inRoadId),outRoadId);
    }


    /**
     * > 这个函数更新connOutRoadIds中的roadId
     *
     */
    public void setConnOutRoadIds(Map<Integer,Road> roads) {
        int rid;
        Road rd = null;
        for (int i = 0; i < 4; i++) {
            if ((rid = connRoadIds[i]) < 0) {
                continue;
            }
            rd = roads.get(rid);
            if (rd.getIsDuplex() == 1 || rd.getFromCrossId() == crossId) {
                //如果是双向车道且来的crossID等于当前的crossID
                connOutRoadIds[i] = rid;
            }
        }
    }


    /**
     *
     */
    public void updateConnCrossIds(Map<Integer,Road> roads) {

    }

    public int getCrossId() {
        return crossId;
    }

    public void setCrossId(int crossId) {
        this.crossId = crossId;
    }

    public int[] getConnRoadIds() {
        return connRoadIds;
    }

    public void setConnRoadIds(int[] connRoadIds) {
        this.connRoadIds = connRoadIds;
    }

    public int[] getConnOutRoadIds() {
        return connOutRoadIds;
    }

    public void setConnOutRoadIds(int[] connOutRoadIds) {
        this.connOutRoadIds = connOutRoadIds;
    }
}

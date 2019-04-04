package com.huawei.app.model;

/**
 * @author Lettino
 */

public enum DriveDirection {
    /**
     * 0表示直行
     */
    FORWARD(0),
    /**
     * -1表示左转
     */
    LEFT(-1),
    /**
     * 1表示右转
     */
    RIGHT(1);
    private int action;

    DriveDirection(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}

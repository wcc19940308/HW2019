package com.huawei.app.model;

public enum Direction {
    /**
     * 0表示直行
     */
    N(1),
    /**
     * -1表示左转
     */
    E(2),
    /**
     * 1表示右转
     */
    S(3),
    /**
     * 1表示右转
     */
    W(4);
    private int way;

    Direction(int way) {
        this.way = way;
    }

    public int getDirection() {
        return way;
    }
}

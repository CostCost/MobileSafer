package com.example.yuxin.mobilesafer.domain;

import android.graphics.drawable.Drawable;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.user.slidingmenu
 * Created by yuxin.
 * Created time 2016/8/21 0021 下午 2:31.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class TrafficInfo {
    private Drawable drawable;
    private String name;
    private  long tx;
    private  long rx;
    private long total;

    public TrafficInfo() {
    }

    public TrafficInfo(Drawable drawable, String name, long rx, long total, long tx) {
        this.drawable = drawable;
        this.name = name;
        this.rx = rx;
        this.total = total;
        this.tx = tx;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRx() {
        return rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTx() {
        return tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    @Override
    public String toString() {
        return "TrafficInfo{" +
                "drawable=" + drawable +
                ", name='" + name + '\'' +
                ", tx=" + tx +
                ", rx=" + rx +
                ", total=" + total +
                '}';
    }
}

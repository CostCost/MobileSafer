package com.example.yuxin.mobilesafer.domain;

import android.graphics.drawable.Drawable;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin All rights reserved.
 * Packname com.example.yuxin.mobilesafer.domain
 * Created by yuxin.
 * Created time 2016/8/24 0024 下午 6:07.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class CacheInfo {
    private Drawable drawable;
    private String name;
    private String packname;
    private long cachesize;

    public CacheInfo() {
    }

    public CacheInfo(long cachesize, Drawable drawable, String name, String packname) {
        this.cachesize = cachesize;
        this.drawable = drawable;
        this.name = name;
        this.packname = packname;
    }

    public long getCachesize() {
        return cachesize;
    }

    public void setCachesize(long cachesize) {
        this.cachesize = cachesize;
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

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    @Override
    public String toString() {
        return "CacheInfo{" +
                "cachesize=" + cachesize +
                ", drawable=" + drawable +
                ", name='" + name + '\'' +
                ", packname='" + packname + '\'' +
                '}';
    }
}

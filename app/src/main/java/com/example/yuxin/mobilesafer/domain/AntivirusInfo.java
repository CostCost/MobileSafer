package com.example.yuxin.mobilesafer.domain;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin All rights reserved.
 * Packname com.example.yuxin.mobilesafer.domain
 * Created by yuxin.
 * Created time 2016/8/23 0023 下午 3:43.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class AntivirusInfo {
    private  String md5;
    private String name;

    public AntivirusInfo() {
    }

    public AntivirusInfo(String md5, String name) {
        this.md5 = md5;
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AntivirusInfo{" +
                "md5='" + md5 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.example.yuxin.mobilesafer.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by yuxin on 2016/8/12 0012.
 */
public class AppInfo {
    private String packname;
    private Drawable icon;
    private String appname;
    private String apkpath;
    private long appsize;
    private boolean setinrom;
    private boolean userapp;
    private String md5;

    public AppInfo() {
    }

    public AppInfo(String apkpath, String appname, long appsize
            , Drawable icon, String packname
            , boolean setinrom, boolean userapp,String md5) {
        this.apkpath = apkpath;
        this.appname = appname;
        this.appsize = appsize;
        this.icon = icon;
        this.packname = packname;
        this.setinrom = setinrom;
        this.userapp = userapp;
        this.md5 = md5;

    }

    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public long getAppsize() {
        return appsize;
    }

    public void setAppsize(long appsize) {
        this.appsize = appsize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public boolean isSetinrom() {
        return setinrom;
    }

    public void setSetinrom(boolean setinrom) {
        this.setinrom = setinrom;
    }

    public boolean isUserapp() {
        return userapp;
    }

    public void setUserapp(boolean userapp) {
        this.userapp = userapp;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "apkpath='" + apkpath + '\'' +
                ", packname='" + packname + '\'' +
                ", icon=" + icon +
                ", appname='" + appname + '\'' +
                ", appsize=" + appsize +
                ", setinrom=" + setinrom +
                ", userapp=" + userapp +
                ", md5='" + md5 + '\'' +
                '}';
    }
}

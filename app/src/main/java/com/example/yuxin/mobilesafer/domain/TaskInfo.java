package com.example.yuxin.mobilesafer.domain;

import android.graphics.drawable.Drawable;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.domain
 * Created by yuxin.
 * Created time 2016/8/18 0018 上午 8:25.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class TaskInfo {
    private Drawable icon;  //图标
    private String name;    //应用名称
    private long memory;    //占用内存
    private String packName;  //包名
    private boolean isUserTask;  //是否是用户进程

    private boolean isClicked;

    public TaskInfo() {
    }

    public TaskInfo(Drawable icon, boolean isClicked, boolean isUserTask, long memory, String name, String packName) {
        this.icon = icon;
        this.isClicked = isClicked;
        this.isUserTask = isUserTask;
        this.memory = memory;
        this.name = name;
        this.packName = packName;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setUserTask(boolean userTask) {
        isUserTask = userTask;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", memory=" + memory +
                ", packName='" + packName + '\'' +
                ", isUserTask=" + isUserTask +
                ", isClicked=" + isClicked +
                '}';
    }
}

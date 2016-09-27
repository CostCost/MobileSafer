package com.example.yuxin.mobilesafer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.activity.Main_activity;
import com.example.yuxin.mobilesafer.engine.Task_info_Engine;
import com.example.yuxin.mobilesafer.receiver.MyAppWidgetProvider;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.service
 * Created by yuxin.
 * Created time 2016/8/20 0020 下午 4:14.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class MyAppWidgetService extends Service {
    private static final int REQUEST_CODE =100;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent i, int startId) {
        super.onStart(i, startId);

        Task_info_Engine.killAllTask(this);
        //获取数据更新小部件
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //获取组件
        ComponentName componentName=new ComponentName(this, MyAppWidgetProvider.class);
        //获取远程view
        RemoteViews remoteView=new RemoteViews(getPackageName(), R.layout.mywidget_layout);
        //设置
        remoteView.setTextViewText(R.id.tv_task_count,"正在运行的进程:"+ Task_info_Engine.getRunTaskCount(this)+"个");
        remoteView.setTextViewText(R.id.tv_avile_mem,"可用内存:"+ Task_info_Engine.getAvailMemory(this));
        //点击手机卫士跳转到主界面
        Intent intent1=new Intent(this,Main_activity.class);
        PendingIntent pendingIntent1=PendingIntent.getActivity(this,REQUEST_CODE,intent1,0);
        remoteView.setOnClickPendingIntent(R.id.ll_main,pendingIntent1);
        //监听一键清理按钮点击操作
        Intent intent=new Intent(this,MyAppWidgetService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,REQUEST_CODE,intent,0);
        remoteView.setOnClickPendingIntent(R.id.bt_clean_all,pendingIntent);
        //更新视图
        appWidgetManager.updateAppWidget(componentName,remoteView);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

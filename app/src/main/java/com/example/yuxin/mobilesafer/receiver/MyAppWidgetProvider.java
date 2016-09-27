package com.example.yuxin.mobilesafer.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.yuxin.mobilesafer.service.MyAppWidgetService;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.receiver
 * Created by yuxin.
 * Created time 2016/8/20 0020 下午 2:42.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class MyAppWidgetProvider extends AppWidgetProvider{
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent=new Intent(context, MyAppWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent=new Intent(context, MyAppWidgetService.class);
        context.stopService(intent);
    }
}

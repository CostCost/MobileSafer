package com.example.yuxin.mobilesafer.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.text.format.Formatter;

import com.example.yuxin.mobilesafer.domain.TrafficInfo;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.user.slidingmenu
 * Created by yuxin.
 * Created time 2016/8/21 0021 下午 2:33.
 * Version   1.0;
 * Describe : 获取流量统计，因为用的TrafficStats.getUidRxBytes(uid)所以
 * 统计的是开机到现在应用所用的流量，数据还没有做持久化操作
 * History:
 * ==============================================================================
 */
public class TrafficInfo_Engine {

    public static List<TrafficInfo> getTrafficInfo(Context context) {
        List<TrafficInfo> infos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo info:installedPackages) {
            TrafficInfo trafficInfo=new TrafficInfo();
            ApplicationInfo applicationInfo = info.applicationInfo;
            Drawable drawable = applicationInfo.loadIcon(pm);
            trafficInfo.setDrawable(drawable);

            String name = applicationInfo.loadLabel(pm).toString();
            trafficInfo.setName(name);

            int uid=applicationInfo.uid;
            long uidRxBytes = TrafficStats.getUidRxBytes(uid);
            long uidTxBytes = TrafficStats.getUidTxBytes(uid);
            long uidtotalBytes=uidRxBytes+uidTxBytes;
            //Formatter.formatFileSize(context,uidRxBytes)
            trafficInfo.setRx(uidRxBytes);
            trafficInfo.setTx(uidTxBytes);
            trafficInfo.setTotal(uidtotalBytes);
            infos.add(trafficInfo);
        }
        return infos;
    }

    public static long getMaxTrafficInfo(List<TrafficInfo> trafficInfo){
        long MAX=0;
        for (int i=0;i<trafficInfo.size();i++){
            if (trafficInfo.get(i).getTotal()>MAX){
                MAX=trafficInfo.get(i).getTotal();
            }
        }
        return MAX;
    }
}

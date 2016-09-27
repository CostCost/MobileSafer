package com.example.yuxin.mobilesafer.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.widget.ProgressBar;

import com.example.yuxin.mobilesafer.activity.Cache_Manager_activity;
import com.example.yuxin.mobilesafer.domain.CacheInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin All rights reserved.
 * Packname com.example.yuxin.mobilesafer.engine
 * Created by yuxin.
 * Created time 2016/8/24 0024 下午 6:06.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class Cache_Manager_Engine {
    private Context context;
    private Cache_Manager_activity activity;
    private int packsize;//存储应用程序数量
    int flag = 0;//统计onGetStatsCompleted方法执行次数
    private ProgressBar cache_pb;

    public Cache_Manager_Engine(Cache_Manager_activity activity, Context context,ProgressBar cache_pb) {
        this.context = context;
        this.activity = activity;
        this.cache_pb=cache_pb;
    }

    //用于存储有缓存的应用信息
    List<CacheInfo> infos = new ArrayList<>();
    IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        //这个方法是一个异步操作，当每次 method.invoke这个方法会自动调用
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            flag++;
            cache_pb.setProgress(flag);
            long cacheSize = pStats.cacheSize;
            //没有缓存不做操作
            if (cacheSize > 0) {
                String packageName = pStats.packageName;
                PackageManager pm = context.getPackageManager();
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = pm.getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String name = applicationInfo.loadLabel(pm).toString();
                Drawable drawable = applicationInfo.loadIcon(pm);
                infos.add(new CacheInfo(cacheSize, drawable, name, packageName));
            }
            //如果调用method.invoke方法次数等于应用程序数量，那么表示加载完毕，回调一个方法通知更新界面
            if (packsize == flag) {
                activity.finishDate(infos);
            }
        }

    };


    public void getCacheInfos() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        packsize = installedPackages.size();
        cache_pb.setMax(packsize);
        for (PackageInfo info : installedPackages) {
            String packageName = info.packageName;

//            public abstract void getPackageSizeInfo(String packageName, int userHandle,
//            IPackageStatsObserver observer);
            try {
                //加载字节码
                Class clazz = Class.forName("android.content.pm.PackageManager");
                //找到方法
                Method method = clazz.getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
                //调用方法
                method.invoke(pm, new Object[]{packageName, mStatsObserver});
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}

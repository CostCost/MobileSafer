package com.example.yuxin.mobilesafer.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.yuxin.mobilesafer.domain.AppInfo;
import com.example.yuxin.mobilesafer.ui.Md5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuxin on 2016/8/12 0012.
 */

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　  ┏┓    ┏┓
 * 　　┏━┛┻━━━━┛┻━━┓
 * 　　┃　　　　　   ┃
 * 　　┃　 　━　    ┃
 * 　　┃  ┳┛　 ┗┳  ┃
 * 　　┃           ┃
 * 　　┃　　　┻     ┃
 * 　　┃　　　　　   ┃
 * 　　┗━━┓　　　 ┏━┛    Code is far away from bug with the animal protecting
 * 　　　　┃　　　┃        神兽保佑,代码无bug
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　 ┣┓
 * 　　　　┃　　　　　 ┏┛
 * 　　　　┗┓┓┏━━━┳┓┏┛
 * 　　　　 ┃┫┫ 　┃┫┫
 * 　　 　　┗┻┛　 ┗┻┛
 *
 * ━━━━━━感觉萌萌哒━━━━━━
 */

public class App_Manager_Engine {


    private static final String TAG ="App_Manager_Engine";

    public static List<AppInfo> getAPPinfos(Context context, ProgressBar soft_pb) {
        int flag=0;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        if ( soft_pb!=null){
        soft_pb.setProgress(0);
        soft_pb.setMax(packinfos.size());
        }
        List<AppInfo> appinfos = new ArrayList<>();
        for (PackageInfo packageinfo : packinfos) {
            if ( soft_pb!=null){
                soft_pb.setProgress(flag);
                flag++;
            }
            AppInfo appinfo = new AppInfo();
            //包名
            String packageName = packageinfo.packageName;
            appinfo.setPackname(packageName);
            //图标
            Drawable icon = packageinfo.applicationInfo.loadIcon(pm);
            appinfo.setIcon(icon);
            //程序名
            String appname = packageinfo.applicationInfo.loadLabel(pm).toString();
            appinfo.setAppname(appname);
            //路径
            String apkpath = packageinfo.applicationInfo.sourceDir;
            appinfo.setApkpath(apkpath);
            //app大小
            File file = new File(apkpath);
            long length = file.length();
            appinfo.setAppsize(length);

            //应用程序安装位置和类型   二进制映射
            int flags = packageinfo.applicationInfo.flags;
            //判断装的位置
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                appinfo.setSetinrom(false);
            } else {
                appinfo.setSetinrom(true);
            }
            //判断程序类型
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                appinfo.setUserapp(false);//系统程序
            } else {
                appinfo.setUserapp(true);//用户程序
            }
            String string = packageinfo.signatures[0].toCharsString();
            String md5 = Md5Utils.encode(string);
            appinfo.setMd5(md5);
            appinfos.add(appinfo);
            appinfo = null;
        }
        return appinfos;

    }

    public static List<AppInfo> getScanerAppInfo(Context context) {
        int flag=0;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);

        List<AppInfo> appinfos = new ArrayList<>();
        for (PackageInfo packageinfo : packinfos) {
            AppInfo appinfo = new AppInfo();
            //包名
            String packageName = packageinfo.packageName;
            appinfo.setPackname(packageName);
            //图标
            Drawable icon = packageinfo.applicationInfo.loadIcon(pm);
            appinfo.setIcon(icon);
            //程序名
            String appname = packageinfo.applicationInfo.loadLabel(pm).toString();
            appinfo.setAppname(appname);
            //路径
            String apkpath = packageinfo.applicationInfo.sourceDir;
            appinfo.setApkpath(apkpath);
            //app大小
            File file = new File(apkpath);
            long length = file.length();
            appinfo.setAppsize(length);

            //应用程序安装位置和类型   二进制映射
            int flags = packageinfo.applicationInfo.flags;
            //判断装的位置
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                appinfo.setSetinrom(false);
            } else {
                appinfo.setSetinrom(true);
            }
            //判断程序类型
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                appinfo.setUserapp(false);//系统程序
            } else {
                appinfo.setUserapp(true);//用户程序
            }
            String string = packageinfo.signatures[0].toCharsString();
            String md5 = Md5Utils.encode(string);
            appinfo.setMd5(md5);
            appinfos.add(appinfo);
            appinfo = null;
        }
        return appinfos;

    }

}

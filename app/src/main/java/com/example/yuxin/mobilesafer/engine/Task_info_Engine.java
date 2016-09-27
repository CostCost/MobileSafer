package com.example.yuxin.mobilesafer.engine;

import android.app.ActivityManager;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.domain.TaskInfo;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.engine
 * Created by yuxin.
 * Created time 2016/8/18 0018 上午 8:36.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class Task_info_Engine {

    private static final String TAG = "Task_info_Engine";

    /**
     * 获取正在运行的进程信息
     *
     * @param context
     * @param runningProcesses
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context, List<ProcessManager.Process> runningProcesses, ProgressBar task_pb) {
        int flag=0;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        Log.i(TAG, "getTaskInfos:" + runningProcesses.size());
        List<TaskInfo> taskinfos = new ArrayList<>();
        //遍历已获取的全部进程
        for (ProcessManager.Process runningProcesse : runningProcesses) {
            if (task_pb!=null){
            task_pb.setProgress(flag);
            flag++;
            }
            TaskInfo taskinfo = new TaskInfo();
            String packname = runningProcesse.name;
            taskinfo.setPackName(packname);

            ApplicationInfo applicationInfo = null;

            try {
                applicationInfo = pm.getApplicationInfo(packname, 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            Drawable drawable = applicationInfo.loadIcon(pm);
            taskinfo.setIcon(drawable);

            String name = applicationInfo.loadLabel(pm).toString();
            taskinfo.setName(name);

            boolean isUsertask = filterApp(applicationInfo);
            taskinfo.setUserTask(isUsertask);

            int pid = runningProcesse.pid;
            int[] pids = new int[]{pid};
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pids);
            int memory = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            taskinfo.setMemory(memory);
            taskinfos.add(taskinfo);

        }
        return taskinfos;
    }

    /**
     * 判断是否是系统进程，返回true表示是系统进程
     *
     * @param applicationInfo
     * @return
     */
    public static boolean filterApp(ApplicationInfo applicationInfo) {
        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;

    }

    /**
     * 获取可用内存
     *
     * @return
     */
    public static String getAvailMemory(Context context) {
        try {
            File file = new File("/proc/meminfo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            String line = br.readLine();
            String content = line.split(":")[1].trim();
            String length = content.substring(0, content.lastIndexOf("k") - 1);
            return Formatter.formatFileSize(context, Long.parseLong(length) * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机总内存
     *
     * @return
     */
    public static String getTotalMemory(Context context) {
        try {
            File file = new File("/proc/meminfo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            String content = line.split(":")[1].trim();
            String length = content.substring(0, content.lastIndexOf("k") - 1);
            return Formatter.formatFileSize(context, Long.parseLong(length) * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户获取进程数目
     * @param context
     * @return
     */
    public static int getRunTaskCount(Context context){
        PackageManager pm = context.getPackageManager();
        List<ProcessManager.Process> Processes = new ArrayList<>();
         List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
        //过滤没有名字的进程
        for (ProcessManager.Process runningProcesse : runningProcesses) {
            String packname = runningProcesse.getPackageName();
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            Processes.add(runningProcesse);
        }
        List<TaskInfo> taskInfos = Task_info_Engine.getTaskInfos(context, Processes,null);

        List<TaskInfo> userTask = new ArrayList<TaskInfo>();
        List<TaskInfo> sysTask = new ArrayList<TaskInfo>();
        for (TaskInfo info : taskInfos) {
            if (info.isUserTask()) {
                userTask.add(info);
            } else {
                sysTask.add(info);
            }
        }
        int size = userTask.size() + sysTask.size();
        return size;
    }

    public static void killAllTask(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ProcessManager.Process> Processes = new ArrayList<>();
        List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
        //过滤没有名字的进程
        for (ProcessManager.Process runningProcesse : runningProcesses) {
            String packname = runningProcesse.getPackageName();
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            Processes.add(runningProcesse);
        }
        long memory=0;
        int count=0;
        for (ProcessManager.Process process : Processes) {
        if (!process.getPackageName().equals(context.getPackageName())) {
            am.killBackgroundProcesses(process.getPackageName());
            int pid = process.pid;
            int[] pids = new int[]{pid};
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pids);
            memory += processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            count++;
        }
        }
        String size=Formatter.formatFileSize(context,memory);
        String string="杀死了"+count+"个进程，释放了"+size+"空间";
        MyToast.makeshow(context,string, Toast.LENGTH_LONG);

    }
}

package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.ActivityManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import android.widget.ListView;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;

import com.example.yuxin.mobilesafer.domain.TaskInfo;

import com.example.yuxin.mobilesafer.engine.ProcessManager;
import com.example.yuxin.mobilesafer.engine.Task_info_Engine;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.e;
import static android.util.Log.i;



/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/12 0022 上午 11:52.
 * Version   1.0;
 * Describe :进程管理器
 * History:
 *
 */
public class Task_Manager_activity extends Activity {

    private static final String TAG = "Task_Manager_activity";
    private static final int LOAD_DATE_SUCCESS = 100;
    private TextView tv_running_process;
    private TextView tv_memory;
    private ListView lv_tasks;
    private RelativeLayout rl_pb;
    private TextView tv_task_lable;
    private ActivityManager am;
    private List<ProcessManager.Process> runningProcesses;
    private List<TaskInfo> userTask;
    private List<TaskInfo> sysTask;
    List<ProcessManager.Process> Processes;
    private MyAdapter adapter;
    private SharedPreferences sp;
    private ProgressBar task_pb;

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //进程加载成功
                case LOAD_DATE_SUCCESS:
                    rl_pb.setVisibility(View.GONE);
                    adapter = new MyAdapter();
                    lv_tasks.setAdapter(adapter);
                    int size = (int) msg.obj;
                    tv_running_process.setText("运行中进程:" +size + "个 ");
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.task_manager_layout);
        //获取所有控件
        tv_running_process = (TextView) findViewById(R.id.tv_running_process);
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        lv_tasks = (ListView) findViewById(R.id.lv_tasks);
        rl_pb = (RelativeLayout) findViewById(R.id.rl_pb);
        tv_task_lable = (TextView) findViewById(R.id.tv_task_lable);
        task_pb = (ProgressBar) findViewById(R.id.task_pb);

        //获取首选项
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        //用户存放所有的过滤好的进程
        Processes = new ArrayList<>();
        PackageManager pm = getPackageManager();
        runningProcesses = ProcessManager.getRunningProcesses();
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

        tv_memory.setText("可用/总内存:" + Task_info_Engine.getAvailMemory(this) + "/" + Task_info_Engine.getTotalMemory(this));

        //加载进程数据
        loadDate(runningProcesses);

        lv_tasks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //根据listview的滚动位置对一个textview内容进行改变
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTask != null && sysTask != null) {
                    if (firstVisibleItem >= (userTask.size() + 1)) {
                        tv_task_lable.setText("系统进程:" + sysTask.size() + "个");
                    } else {
                        tv_task_lable.setText("用户进程:" + userTask.size() + "个");
                    }
                }
            }
        });
        //处理listview的点击操作
        lv_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的条目的对象
                TaskInfo taskinfo = (TaskInfo) adapter.getItem(position);
                //获取点击条目的视图
                ViewHolder holder = (ViewHolder) view.getTag();
                //判断如果是当前的应用的进程则跳过点击时间
                if (taskinfo.getPackName().equals(getPackageName())) {
                    return;
                }
                //如果已经被选中则改为未选中，未被选中再次点击改为选中状态
                if (holder.ck_box.isChecked()) {
                    holder.ck_box.setChecked(false);
                    taskinfo.setClicked(false);
                } else {
                    holder.ck_box.setChecked(true);
                    taskinfo.setClicked(true);
                }
            }
        });


    }

    /**
     * 适配器
     */
    private class MyAdapter extends BaseAdapter {
        //设置两个条目不处理点击响应
        @Override
        public boolean isEnabled(int position) {
            if (position == 0 || position == (userTask.size() + 1)) {
                return false;
            }
            return true;
        }

        //获取条目总数
        @Override
        public int getCount() {
            boolean isshowsystemtask = sp.getBoolean("isshowsystemtask", true);
            if (isshowsystemtask){
                return userTask.size() + sysTask.size() + 2;
            }
            return userTask.size()+1;
        }

        //获取指定条目对象
        @Override
        public Object getItem(int position) {
            if (position == 0 || position == (userTask.size() + 1)) {
                return null;
            }
            TaskInfo taskinfo;
            if (position < (userTask.size() + 1)) {
                taskinfo = userTask.get(position - 1);
            } else {
                int location = position - 2 - userTask.size();
                taskinfo = sysTask.get(location);
            }
            return taskinfo;
        }

        //获取指定条目id
        @Override
        public long getItemId(int position) {
            return position;
        }

        //获取条目的视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //首行位置设置为一个textview
            if (position == 0) {
                TextView tv_userlaber = new TextView(Task_Manager_activity.this);
                tv_userlaber.setBackgroundColor(Color.GRAY);
                tv_userlaber.setTextColor(Color.WHITE);
                tv_userlaber.setText("用户进程:" + userTask.size() + "个");
                return tv_userlaber;
                //userTask.size() + 1的位置设置一个textview
            } else if (position == userTask.size() + 1) {
                TextView tv_syslaber = new TextView(Task_Manager_activity.this);
                tv_syslaber.setBackgroundColor(Color.GRAY);
                tv_syslaber.setTextColor(Color.WHITE);
                tv_syslaber.setText("系统进程:" + sysTask.size() + "个");
                return tv_syslaber;
            }
            //根据所在位置设置taskinfo的对象
            TaskInfo taskinfo;
            if (position < (userTask.size() + 1)) {
                taskinfo = userTask.get(position - 1);
            } else {
                int location = position - userTask.size() - 2;
                taskinfo = sysTask.get(location);
            }
            //对view进行缓存
            View view;
            ViewHolder holder;
            //如果已经缓存了则获取缓存内容
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                //没有缓存则重新加载
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.task_item_layout, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.task_item_iv);
                holder.task_name = (TextView) view.findViewById(R.id.task_name);
                holder.task_size = (TextView) view.findViewById(R.id.task_size);
                holder.ck_box = (CheckBox) view.findViewById(R.id.cb_item);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskinfo.getIcon());
            holder.task_name.setText(taskinfo.getName());
            //如果是当前应用进程则将checkbox设为隐藏
            if (taskinfo.getPackName().equals(getPackageName())) {
                holder.ck_box.setVisibility(View.GONE);
            } else {
                holder.ck_box.setVisibility(View.VISIBLE);
            }
            //对点击状态进行存储
            if (taskinfo.isClicked()) {
                holder.ck_box.setChecked(true);
            } else {
                holder.ck_box.setChecked(false);
            }

            long tasksize = taskinfo.getMemory();
            String size = Formatter.formatFileSize(getApplicationContext(), tasksize);
            holder.task_size.setText(size);
            return view;
        }
    }

    private static class ViewHolder {
        ImageView iv_icon;
        TextView task_name;
        TextView task_size;
        CheckBox ck_box;
    }

    /**
     * 加载数据
     * 将用户进程和系统进程分开存储
     *
     * @param Processes
     */
    private void loadDate(final List<ProcessManager.Process> Processes) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                userTask = new ArrayList<TaskInfo>();
                sysTask = new ArrayList<TaskInfo>();
                task_pb.setProgress(0);
                task_pb.setMax(Processes.size());
                List<TaskInfo> taskInfos = Task_info_Engine.getTaskInfos(Task_Manager_activity.this, Processes,task_pb);

                for (TaskInfo info : taskInfos) {
                    if (info.isUserTask()) {
                        userTask.add(info);
                    } else {
                        sysTask.add(info);
                    }
                }
                int size = userTask.size() + sysTask.size();
                Message msg = new Message();
                msg.what = LOAD_DATE_SUCCESS;
                msg.obj=size;
                mhandler.sendMessage(msg);
            }
        }.start();
    }


    /**
     * 全选
     * 将usertask和systask的isclick全部设置为true
     *
     * @param view
     */
    public void click_allselected(View view) {
        for (TaskInfo taskinfo : userTask) {
            if (!taskinfo.getPackName().equals(getPackageName())) {
                taskinfo.setClicked(true);
            }
        }
        boolean isshowsystemtask = sp.getBoolean("isshowsystemtask", true);
        if (isshowsystemtask){
            for (TaskInfo taskinfo : sysTask) {
                taskinfo.setClicked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 取消
     * 与全选相反
     *
     * @param view
     */
    public void click_cancle(View view) {

        for (TaskInfo taskinfo : userTask) {
            if (!taskinfo.getPackName().equals(getPackageName())) {
                taskinfo.setClicked(false);
            }
        }
        for (TaskInfo taskinfo : sysTask) {
            taskinfo.setClicked(false);
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * 清理
     *将被选中的对象放置在一个新的集合里面，然后移除
     * @param view
     */
    public void click_clean(View view) {
        am= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<TaskInfo> cleanTask=new ArrayList<>();
        for (TaskInfo taskinfo : userTask) {
            if (!taskinfo.getPackName().equals(getPackageName())) {
               if (taskinfo.isClicked()){
                   cleanTask.add(taskinfo);
               }
            }
        }
        for (TaskInfo taskinfo : sysTask) {
            if (taskinfo.isClicked()){
                cleanTask.add(taskinfo);
            }
        }
        long count=0;
        for (TaskInfo taskinfo : cleanTask) {
            if (taskinfo.isUserTask()){
                userTask.remove(taskinfo);
                am.killBackgroundProcesses(taskinfo.getPackName());
                count+=taskinfo.getMemory();
            }else{
                sysTask.remove(taskinfo);
                am.killBackgroundProcesses(taskinfo.getPackName());
                count+=taskinfo.getMemory();
            }
        }
        adapter.notifyDataSetChanged();
        tv_running_process.setText("运行中进程:" + (userTask.size()+sysTask.size()) + "个 ");
        tv_memory.setText("可用/总内存:" + Task_info_Engine.getAvailMemory(this) + "/" + Task_info_Engine.getTotalMemory(this));
        String size=Formatter.formatFileSize(this,count);
        String string="杀死了"+cleanTask.size()+"个进程，释放了"+size+"空间";
        MyToast.makeshow(this,string,Toast.LENGTH_SHORT);
        cleanTask=null;
    }

    /**
     * 设置
     *
     * @param view
     */
    public void click_setting(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.windowFrame_no_title);
        View inflate = LayoutInflater.from(this).inflate(R.layout.task_setting_dialog, null);
        final CheckBox cb_task_setting = (CheckBox) inflate.findViewById(R.id.cb_task_setting);
        builder.setView(inflate);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        boolean isshowsystemtask = sp.getBoolean("isshowsystemtask", true);
        cb_task_setting.setChecked(isshowsystemtask);
        cb_task_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_task_setting.isChecked();
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isshowsystemtask",checked);
                edit.commit();
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Task_Manager_activity.this, Main_activity.class);
            startActivity(main_intent);
            finish();
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

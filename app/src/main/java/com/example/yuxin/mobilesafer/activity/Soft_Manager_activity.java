package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.AppInfo;
import com.example.yuxin.mobilesafer.engine.App_Manager_Engine;
import com.example.yuxin.mobilesafer.ui.MyToast;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Shell;

import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;


/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/12 0022 上午 10:34.
 * Version   1.0;
 * Describe :软件管理
 * History:
 * ==============================================================================
 */
public class Soft_Manager_activity extends Activity {

    private static final int SUCCESS_LOAD_APPINFO = 0;
    private static final String TAG = "Soft_Manager_activity";

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_LOAD_APPINFO:
                    rl_pb.setVisibility(View.GONE);
                    MyAdapter adapter = new MyAdapter();
                    soft_lv.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    private ListView soft_lv;
    private RelativeLayout rl_pb;
    private TextView tv_avile_rom;
    private TextView tv_avile_sd;
    private List<AppInfo> appinfos;
    private List<AppInfo> userapp;
    private List<AppInfo> sysapp;
    private TextView tv_app_lable;
    private PopupWindow popupWindow;
    private AppInfo clickedapp;
    private UninstallReceiver receiver;
    private ProgressBar soft_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.soft_manager_layout);
        soft_lv = (ListView) findViewById(R.id.soft_lv);
        rl_pb = (RelativeLayout) findViewById(R.id.rl_pb);
        tv_avile_rom = (TextView) findViewById(R.id.tv_avile_rom);
        tv_avile_sd = (TextView) findViewById(R.id.tv_avile_sd);

        tv_app_lable = (TextView) findViewById(R.id.tv_app_lable);

        soft_pb = (ProgressBar) findViewById(R.id.soft_pb);

        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();

        String sd_avile = Formatter.formatFileSize(this, sd_freeSpace);
        String rom_avile = Formatter.formatFileSize(this, rom_freeSpace);
        tv_avile_rom.setText("内存可用:" + rom_avile);
        tv_avile_sd.setText("SD卡可用:" + sd_avile);
        //加载包信息
        fillData();
        soft_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if (userapp != null && sysapp != null) {
                    if (firstVisibleItem >= (userapp.size() + 1)) {
                        tv_app_lable.setText("系统程序:" + sysapp.size() + "个");
                    } else {
                        tv_app_lable.setText("用户程序:" + userapp.size() + "个");
                    }
                }
            }
        });

        soft_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = soft_lv.getItemAtPosition(position);

                if (obj != null && obj instanceof AppInfo) {
                    clickedapp = (AppInfo) obj;
                    View popup_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_item_layout, null);
                    LinearLayout ll_uninstall = (LinearLayout) popup_view.findViewById(R.id.ll_uninstall);
                    LinearLayout ll_start = (LinearLayout) popup_view.findViewById(R.id.ll_start);
                    LinearLayout ll_share = (LinearLayout) popup_view.findViewById(R.id.ll_share);
                    LinearLayout ll_set = (LinearLayout) popup_view.findViewById(R.id.ll_set);
                    MyOnClickListener listener = new MyOnClickListener();
                    ll_uninstall.setOnClickListener(listener);
                    ll_start.setOnClickListener(listener);
                    ll_share.setOnClickListener(listener);
                    ll_set.setOnClickListener(listener);

                    dismissPopupWindow();

                    popupWindow = new PopupWindow(popup_view, -2, -2);
                    // 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
                    popupWindow.setBackgroundDrawable(new ColorDrawable(
                            Color.TRANSPARENT));
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(parent, Gravity.LEFT
                            + Gravity.TOP, 300, location[1]);
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
                            1.0f, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(200);
                    AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                    aa.setDuration(200);
                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(aa);
                    set.addAnimation(sa);
                    popup_view.startAnimation(set);

                }
            }
        });

        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);



    }

    private void dismissPopupWindow() {
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow=null;
        }

    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_uninstall:
                    i(TAG, "onClick" + "ll_uninstall:"+clickedapp.getPackname());
                    uninstallApplication();
                    break;
                case R.id.ll_start:
                    i(TAG, "onClick" + "ll_start:"+clickedapp.getPackname());
                    startAppLication();
                    break;
                case R.id.ll_share:
                    i(TAG, "onClick" + "ll_share:"+clickedapp.getPackname());
                    shareAppLication();
                    break;
                case R.id.ll_set:
                    i(TAG, "onClick" + "ll_set:"+clickedapp.getPackname());
                    viewApplication();
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 卸载应用
     */
    private void uninstallApplication() {
        //卸载用户应用
        if (clickedapp.isUserapp()){
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + clickedapp.getPackname()));
            startActivity(intent);
        }else{
            //卸载系统应用
            if (!RootTools.isRootAvailable()){
                MyToast.makeshow(this,"卸载系统应用，必须要root权限",Toast.LENGTH_SHORT);
                return;
            }
            try {
                if (!RootTools.isAccessGiven()){
                    MyToast.makeshow(this,"请授予Simon手机卫士Root权限",Toast.LENGTH_SHORT);
                    return;
                }
                MyToast.makeshow(this,"无法卸载此系统应用!",Toast.LENGTH_SHORT);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    /**
     * 应用详情
     */
    private void viewApplication() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // dat=package:com.itheima.mobileguard
        intent.setData(Uri.parse("package:" + clickedapp.getPackname()));
        startActivity(intent);
    }


    /**
     * 启动应用
     */
    private void startAppLication() {
        // 打开这个应用程序的入口activity。
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(clickedapp
                .getPackname());
        if (intent != null) {
            startActivity(intent);
        } else {
            MyToast.makeshow(this,"该应用没有启动界面",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 分享应用
     */
    private void shareAppLication() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "推荐您使用一款软件，名称叫：" + clickedapp.getAppname()
                        + ",下载路径：https://play.google.com/store/apps/details?id="
                        + clickedapp.getPackname());
        startActivity(intent);
    }


    private class MyAdapter extends BaseAdapter {

        //因为有用户程序和系统程序两个条目，所以要加2
        @Override
        public int getCount() {
            return userapp.size() + sysapp.size() + 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv_userlaber = new TextView(Soft_Manager_activity.this);
                tv_userlaber.setBackgroundColor(Color.GRAY);
                tv_userlaber.setTextColor(Color.WHITE);
                tv_userlaber.setText("用户程序:" + userapp.size() + "个");
                return tv_userlaber;

            } else if (position == userapp.size() + 1) {
                TextView tv_syslaber = new TextView(Soft_Manager_activity.this);
                tv_syslaber.setBackgroundColor(Color.GRAY);
                tv_syslaber.setTextColor(Color.WHITE);
                tv_syslaber.setText("系统程序:" + sysapp.size() + "个");
                return tv_syslaber;
            }
            AppInfo appinfo;
            if (position < (userapp.size() + 1)) {
                appinfo = userapp.get(position - 1);
            } else {
                int location = position - userapp.size() - 2;
                appinfo = sysapp.get(location);
            }
            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.soft_item_layout, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.soft_item_iv);
                holder.tv_appname = (TextView) view.findViewById(R.id.soft_name);
                holder.tv_install = (TextView) view.findViewById(R.id.soft_path);
                holder.tv_appsize = (TextView) view.findViewById(R.id.soft_size);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(appinfo.getIcon());
            holder.tv_appname.setText(appinfo.getAppname() + "");
            if (appinfo.isSetinrom()) {
                holder.tv_install.setText("手机内存");
            } else {
                holder.tv_install.setText("外部存储");
            }
            long appsize = appinfo.getAppsize();
            String size = Formatter.formatFileSize(getApplicationContext(), appsize);
            holder.tv_appsize.setText(size);

            return view;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == (userapp.size() + 1)) {
                return null;
            }
            AppInfo appinfo;
            if (position < (userapp.size() + 1)) {
                appinfo = userapp.get(position - 1);
            } else {
                int location = position - 2 - userapp.size();
                appinfo = sysapp.get(location);
            }
            return appinfo;
        }

    }

    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_appname;
        TextView tv_install;
        TextView tv_appsize;
    }


    /**
     * 加载应用程序信息
     */
    private void fillData() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                appinfos = App_Manager_Engine.getAPPinfos(Soft_Manager_activity.this,soft_pb);
                userapp = new ArrayList<AppInfo>();
                sysapp = new ArrayList<AppInfo>();
                for (AppInfo appinfo : appinfos) {
                    if (appinfo.isUserapp()) {
                        userapp.add(appinfo);
                    } else {
                        sysapp.add(appinfo);
                    }
                }
                Message msg = Message.obtain();
                msg.what = SUCCESS_LOAD_APPINFO;
                mhandler.sendMessage(msg);


            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Soft_Manager_activity.this, Main_activity.class);
            startActivity(main_intent);
            finish();
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 注册广播监听应用卸载，刷新应用列表
     */
    private class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getData().toString();
         i(TAG, "onReceive" + info);
            fillData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

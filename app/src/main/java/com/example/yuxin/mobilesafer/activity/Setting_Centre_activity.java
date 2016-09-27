package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.service.AddressService;
import com.example.yuxin.mobilesafer.service.SmsAbortService;
import com.example.yuxin.mobilesafer.ui.MyToast;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/22 0022 上午 8:27.
 * Version   1.0;
 * Describe :设置中心
 * History:
 * ==============================================================================
 */
public class Setting_Centre_activity extends Activity {

    private static final String TAG = "Setting_Centre_activity";
    private TextView tv_autoupdate_state;
    private CheckBox cb_autoupdate;
    private SharedPreferences sp;
    private TextView tv_show_address_state;
    private CheckBox cb_show_address;
    private RelativeLayout rl_reset_show_address_location;
    private TextView tv_abort_black_state;
    private CheckBox cb_abort_black;
    private TextView tv_add_shaortcut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_centre_layout);

        tv_autoupdate_state = (TextView) findViewById(R.id.tv_autoupdate_state);

        cb_autoupdate = (CheckBox) findViewById(R.id.cb_autoupdate);
        tv_show_address_state = (TextView) findViewById(R.id.tv_show_address_state);

        cb_show_address = (CheckBox) findViewById(R.id.cb_show_address);
        rl_reset_show_address_location = (RelativeLayout) findViewById(R.id.rl_reset_show_address_location);

        tv_abort_black_state = (TextView) findViewById(R.id.tv_abort_black_state);
        cb_abort_black = (CheckBox) findViewById(R.id.cb_abort_black);
        //添加快捷方式至桌面
        tv_add_shaortcut = (TextView) findViewById(R.id.tv_add_shaortcut);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        //自动更新栏
        boolean isautoupdate = sp.getBoolean("isautoupdate", true);
        if (isautoupdate){
            tv_autoupdate_state.setText(R.string.autoupdate_state_open);
            cb_autoupdate.setChecked(isautoupdate);
        }else {
            tv_autoupdate_state.setText(R.string.autoupdate_state_close);
            cb_autoupdate.setChecked(isautoupdate);
        }
        //来电归属栏
        boolean isshowaddress = sp.getBoolean("isshowaddress",false);
        if (isshowaddress){
            tv_show_address_state.setText(R.string.show_address_state_open);
            cb_show_address.setChecked(isshowaddress);
        }else {
            tv_show_address_state.setText(R.string.show_address_state_close);
            cb_show_address.setChecked(isshowaddress);
        }
        //黑名单拦截栏
        boolean isabortblack = sp.getBoolean("isabortblack",false);
        if (isabortblack){
            tv_abort_black_state.setText(R.string.abort_black_state_open);
            cb_abort_black.setChecked(isabortblack);
        }else {
            tv_abort_black_state.setText(R.string.abort_black_state_close);
            cb_abort_black.setChecked(isabortblack);
        }
        //自动更新checkbox监听
        cb_autoupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_autoupdate.isChecked();
                SharedPreferences.Editor edit = sp.edit();
                if (checked){
                    tv_autoupdate_state.setText(R.string.autoupdate_state_open);
                    edit.putBoolean("isautoupdate",checked);
                    edit.commit();
                }else {
                    tv_autoupdate_state.setText(R.string.autoupdate_state_close);
                    edit.putBoolean("isautoupdate",checked);
                    edit.commit();
                }
            }
        });

        //来电归属checkbox监听
        cb_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_show_address.isChecked();
                SharedPreferences.Editor edit = sp.edit();
                if (checked){
                    tv_show_address_state.setText(R.string.show_address_state_open);
                    edit.putBoolean("isshowaddress",checked);
                    edit.commit();
                    Intent addressIntent=new Intent(getApplicationContext(), AddressService.class);
                    startService(addressIntent);
                    i(TAG, "onClick" + "服务开启！");
                }else {
                    tv_show_address_state.setText(R.string.show_address_state_close);
                    edit.putBoolean("isshowaddress",checked);
                    edit.commit();
                    Intent addressIntent=new Intent(getApplicationContext(), AddressService.class);
                    stopService(addressIntent);
                    i(TAG, "onClick" + "服务关闭！");
                }
            }
        });

        rl_reset_show_address_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),ResetAddressLocationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.next_in,R.anim.next_out);
            }
        });

        //黑名单拦截cb监听
        cb_abort_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_abort_black.isChecked();
                SharedPreferences.Editor edit = sp.edit();
                if (checked){
                    tv_abort_black_state.setText(R.string.abort_black_state_open);
                    edit.putBoolean("isabortblack",checked);
                    edit.commit();
                    //开启服务
                    startService(new Intent(getApplicationContext(), SmsAbortService.class));
                    i(TAG, "onClick" + "开启SmsAbortService服务");
                }else {
                    tv_abort_black_state.setText(R.string.abort_black_state_close);
                    edit.putBoolean("isabortblack",checked);
                    edit.commit();
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), SmsAbortService.class));
                    i(TAG, "onClick" + "关闭SmsAbortService服务");
                }

            }
        });
        //添加快捷方式至桌面
        tv_add_shaortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                <!-- Intent received used to install shortcuts from other applications -->
//                <receiver
//                android:name="com.android.launcher2.InstallShortcutReceiver"
//                android:permission="com.android.launcher.permission.INSTALL_SHORTCUT">
//                <intent-filter>
//                <action android:name="com.android.launcher.action.INSTALL_SHORTCUT" />
//                </intent-filter>
//                </receiver>
                Intent intent=new Intent();
                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
                //系统默认允许创建多个桌面快捷方式，传入false，只允许创建一个
                intent.putExtra("duplicate",false);
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.ic_shaotcut_launcher));
                Intent lunch_intent=new Intent();
                lunch_intent.setAction("activity.Main_activity");
                lunch_intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,lunch_intent);
                sendBroadcast(intent);
                MyToast.makeshow(Setting_Centre_activity.this,"已将快捷方式创建至桌面", Toast.LENGTH_SHORT);
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent main_intent=new Intent(Setting_Centre_activity.this,Main_activity.class);
            startActivity(main_intent);
            overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
            finish();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

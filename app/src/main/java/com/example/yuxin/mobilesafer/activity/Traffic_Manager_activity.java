package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.TrafficInfo;
import com.example.yuxin.mobilesafer.engine.TrafficInfo_Engine;
import com.example.yuxin.mobilesafer.ui.MySlidingMenu;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/20 0020 下午 10:48.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 *
 * Copyright (c) 2016  yuxin rights reserved.
 *
 * ClassName Traffic_Manager_activity
 *
 * Created by yuxin.
 *
 * Created time 22-08-2016 16:31.
 *
 * Describe : 一个侧滑的activity，左侧显示流量统计，右侧还没规划
 *
 * History:
 *
 * Version   1.0.
 *
 * ==============================================================================
 */
public class Traffic_Manager_activity extends Activity {
    private static final int SUCESS_LOAD_DATA = 100;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private MySlidingMenu mSlidingMenu;
    private ListView mMenuListView;
    private ImageView mMenuToggle;
    private MenuAdapter mMenuAdapter;
    private List<TrafficInfo> trafficInfo;

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCESS_LOAD_DATA:
                    //设置适配器内容
                    long maxTrafficInfo = (long) msg.obj;
                    mMenuAdapter = new MenuAdapter(getApplicationContext(), trafficInfo,maxTrafficInfo);
                    mMenuListView.setAdapter(mMenuAdapter);
                    mMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
        setContentView(R.layout.traffic_manager_layout);
        mSlidingMenu = (MySlidingMenu) findViewById(R.id.slidingmenu);
        mMenu = (ViewGroup) findViewById(R.id.menu);
        mContent = (ViewGroup) findViewById(R.id.content);

        mMenuListView = (ListView) mMenu.findViewById(R.id.menu_listview);
        //加载流量数据
        initMenuDatas();

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Traffic_Manager_activity.this, "Clicked 菜单" + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        mMenuToggle = (ImageView) mContent.findViewById(R.id.menu_toggle);


        mMenuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggleMenu();
            }
        });
    }

    //异步加载流量信息
    private void initMenuDatas() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //获取所有信息集合
                trafficInfo = TrafficInfo_Engine.getTrafficInfo(Traffic_Manager_activity.this);
                //获取应用的流量使用最大值
                long maxTrafficInfo = TrafficInfo_Engine.getMaxTrafficInfo(trafficInfo);
                //传入主线程
                Message msg = Message.obtain();
                msg.obj=maxTrafficInfo;
                msg.what = SUCESS_LOAD_DATA;
                mhandler.sendMessage(msg);
            }
        }.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Traffic_Manager_activity.this, Main_activity.class);
            startActivity(main_intent);
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
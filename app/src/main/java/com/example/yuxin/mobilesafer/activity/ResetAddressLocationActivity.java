package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yuxin.mobilesafer.R;

import org.apache.commons.logging.impl.LogFactoryImpl;

import static android.util.Log.i;
/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/31 0022 上午 9:57.
 * Version   1.0;
 * Describe :重置归属地信息显示位置
 * History:
 * ==============================================================================
 */
public class ResetAddressLocationActivity extends Activity {

    private static final String TAG = "ResetAddressLocationActivity";
    private LinearLayout ll_reset_view;
    private int screewidth;
    private int screeheight;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reset_address_location_layout);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        ll_reset_view = (LinearLayout) findViewById(R.id.ll_reset_view);

        //获取首选项存储的位置，对控件的位置进行初始化操作
        RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams) ll_reset_view.getLayoutParams();
        int width = ll_reset_view.getWidth();
        int height = ll_reset_view.getHeight();
        int x = sp.getInt("X", 0);
        int y = sp.getInt("Y", 0);
        layoutParams.setMargins(x,y,x+width,y+height);
        ll_reset_view.setLayoutParams(layoutParams);

        //获取当前屏幕的宽高，进行后续的判断控件是否越界的操作
        WindowManager wm = getWindowManager();
        Display defaultDisplay = wm.getDefaultDisplay();
        screewidth = defaultDisplay.getWidth();
        screeheight = defaultDisplay.getHeight();

        //对控件进行触摸监听
        ll_reset_view.setOnTouchListener(new View.OnTouchListener() {

            private int stopY;
            private int stopX;
            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //   i(TAG, "onTouch:ACTION_DOWN");
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // i(TAG, "onTouch:ACTION_MOVE");
                        stopX = (int) event.getX();
                        stopY = (int) event.getY();


                        float moveX = stopX - startX;
                        float moveY = stopY - startY;
                        int l = (int) (ll_reset_view.getLeft() + moveX);
                        int t = (int) (ll_reset_view.getTop() + moveY);
                        int r = (int) (ll_reset_view.getRight() + moveX);
                        int b = (int) (ll_reset_view.getBottom() + moveY);

                        if (l < 0 || t < 0 || r > screewidth || b > screeheight) {
                            //对屏幕越界不进行操作
                        } else {
                            //对控件的位置进行动态设置
                            ll_reset_view.layout(l, t, r, b);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // i(TAG, "onTouch:ACTION_UP");
                        startX = stopX;
                        startY = stopY;
                        //存储当前位置
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putInt("X", ll_reset_view.getLeft());
                        edit.putInt("Y", ll_reset_view.getTop());
                        edit.commit();

                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        ll_reset_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //双击居中操作
                if (starttime != 0) {
                    endtime = System.currentTimeMillis();
                    if (endtime - starttime < 500) {
                        int l = screewidth / 2 - ll_reset_view.getWidth() / 2;
                        int r = screewidth / 2 + ll_reset_view.getWidth() / 2;
                        int t = screeheight / 2 - ll_reset_view.getHeight() / 2;
                        int b = screeheight / 2 + ll_reset_view.getHeight() / 2;
                        ll_reset_view.layout(l, t, r, b);

                    }
                    starttime = 0;

                } else {
                    starttime = System.currentTimeMillis();
                }

            }
        });

    }

    long starttime = 0;
    long endtime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent back_intent = new Intent(ResetAddressLocationActivity.this, Setting_Centre_activity.class);
            startActivity(back_intent);
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

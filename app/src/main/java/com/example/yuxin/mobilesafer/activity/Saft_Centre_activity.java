package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuxin.mobilesafer.R;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/25 0022 上午 10:57.
 * Version   1.0;
 * Describe :防盗中心
 * History:
 * ==============================================================================
 */
public class Saft_Centre_activity extends Activity {

    private TextView tv_saft_number;
    private TextView tv_is_protectd;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.saft_centre_layout);
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_saft_number = (TextView) findViewById(R.id.tv_saft_number);
        String softnumber = sp.getString("softnumber", null);
        tv_saft_number.setText(softnumber);
        tv_is_protectd = (TextView) findViewById(R.id.tv_is_protectd);
        iv = (ImageView) findViewById(R.id.iv_ispertected);
        boolean isprotected = sp.getBoolean("isprotected", false);
        if (isprotected){
            tv_is_protectd.setText("防盗保护开启");
            iv.setImageResource(R.drawable.close);
        }else{
            tv_is_protectd.setText("防盗保护关闭");
            iv.setImageResource(R.drawable.close_press);
        }
    }

    public void returntosetting(View view) {
        Intent intent=new Intent(this,Soft_viewpage_activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in,R.anim.pre_out);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(getApplicationContext(), Main_activity.class);
        startActivity(intent);
        finish();
       overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
        return super.onKeyDown(keyCode, event);
    }
}

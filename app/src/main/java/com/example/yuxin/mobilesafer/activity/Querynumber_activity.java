package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.dao.Addressdao;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/27 0022 上午 9:57.
 * Version   1.0;
 * Describe :归属地查询
 * History:
 * ==============================================================================
 */
public class Querynumber_activity extends Activity {

    private EditText et_query_number;
    private Button bt_query_number;
    private TextView tv_query_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.querynumber_layout);
        et_query_number = (EditText) findViewById(R.id.et_query_number);
        bt_query_number = (Button) findViewById(R.id.bt_query_number);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
        //检查数据库文件是否复制到/data/data/目录下，没有的话执行复制操作
        if (!Addressdao.isExist(this)){
            Addressdao.copyFileToFiles(this);
        }
        bt_query_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_query_number.getText().toString().replace(" ","");
                if (!TextUtils.isEmpty(number)){
                String address = Addressdao.getAddress(getApplicationContext(), number);
                tv_query_result.setText("归属地:"+address);

                }else{
                    //加载动画
                    Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    //执行动画
                    et_query_number.startAnimation(animation);
                }

            }
        });
        et_query_number.addTextChangedListener(new TextWatcher() {
            //改变之前，
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //改变中
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //改变之后
            @Override
            public void afterTextChanged(Editable s) {
                String dynamicResult = Addressdao.getDynamicResult(getApplicationContext(), s.toString().trim());
                tv_query_result.setText("归属地:"+dynamicResult);

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent main_intent=new Intent(Querynumber_activity.this,Tools_activity.class);
            startActivity(main_intent);
            finish();
            overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

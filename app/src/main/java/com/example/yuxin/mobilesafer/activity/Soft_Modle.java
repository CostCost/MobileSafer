package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.ui.MyToast;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/20 0022 上午 10:34.
 * Version   1.0;
 * Describe :手机防盗模块，对话框实现
 * History:
 * ==============================================================================
 */
public class Soft_Modle {
    private static final String TAG ="Soft_Modle" ;
    //设置密码框提交按钮
    private Button bt_commit;
    //设置密码框取消按钮
    private Button bt_cancel;
    //输入密码框提交按钮
    private Button bt_put_commit;
    //输入密码框取消按钮
    private Button bt_put_cancel;
    //设置密码框，输入框1
    private EditText set_pwd_one;
    //设置密码框，输入框2
    private EditText set_pwd_two;
    //输入密码框，输入框
    private EditText put_pwd;
    //上下文
    private  Context context;
    //传入所在的activity对象
    private  Activity activity;
    //首选项
    private SharedPreferences sp;

    public Soft_Modle(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;

    }

    /**
     * 这个方法用来作为这个模块的入口
     */
    public void start(){
        //获取首选项根据首选项的值进入不同的登陆界面
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String pwd = sp.getString("pwd",null);
        //对首选项内容进行判断
        if (TextUtils.isEmpty(pwd)) {
            //进入设置密码对话框
            entersetpwd();
        }
        else {
            //进入输入密码对话框
            enterpwd();
        }


    }



    /**
     * 没有设置密码模块
     */
    private void entersetpwd(){
        //新建一个自定义对话框
        AlertDialog.Builder alert=new AlertDialog.Builder(activity,R.style.windowFrame_no_title);
        View view1 = LayoutInflater.from(activity).inflate(R.layout.soft_login_dialog, null);

        //设置EditText中Hint字体的大小
        set_pwd_one = (EditText) view1.findViewById(R.id.set_pwd_1);
        set_pwd_two = (EditText) view1.findViewById(R.id.set_pwd_2);
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString("密码长度大于6位");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(13,true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint的字体大小
        set_pwd_one.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
        set_pwd_two.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失

        alert.setCancelable(false);
        alert.setView(view1);
        //按后退键不能取消
        final AlertDialog alertDialog = alert.create();
        //设置上下没有黑边
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
        bt_cancel = (Button) view1.findViewById(R.id.bt_cancel);
        bt_commit=(Button) view1.findViewById(R.id.bt_commit);
        //取消按键监听
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //确认按键监听
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取两个输入框的值
                String pwd1 = set_pwd_one.getText().toString();
                String pwd2 = set_pwd_two.getText().toString();
                if ((pwd1.equals(pwd2))&&pwd1.length()>5&&pwd2.length()>5){
                    //TODO 需要将密码存储起来
                    i(TAG, "onClick:pwd1" + pwd1);
                    i(TAG, "onClick:pwd2" + pwd2);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("pwd",pwd1);
                    edit.commit();
                    alertDialog.dismiss();
                    MyToast.makeshow(activity,"设置成功,再次点击进入设置向导",Toast.LENGTH_SHORT);
                 // Toast.makeText(context,"设置成功！",Toast.LENGTH_SHORT).show();
                }else{
                    //设置一个自定义的补间动画，用来提示用户输入不符合要求
                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.shake);
                    animation.setFillAfter(true);
                    bt_commit.startAnimation(animation);
                }
            }
        });
    }

    /**
     * 已经设置了密码的模块
     */
    private void enterpwd(){

        //新建一个自定义对话框,应用style
        AlertDialog.Builder alert2=new AlertDialog.Builder(activity,R.style.windowFrame_no_title);
        View view2 = LayoutInflater.from(activity).inflate(R.layout.soft_putpwd_dialog, null);

        //设置EditText中Hint字体的大小
        put_pwd = (EditText) view2.findViewById(R.id.put_pwd);
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString("密码长度大于6位");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(13,true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint的字体大小
        put_pwd.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
        //按后退键不能取消
        alert2.setCancelable(false);
        alert2.setView(view2);

        final AlertDialog alertDialog2 = alert2.create();
        //设置对话框上下没有黑边
        alertDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog2.show();
        bt_put_cancel = (Button) view2.findViewById(R.id.bt_put_cancel);
        bt_put_commit=(Button) view2.findViewById(R.id.bt_put_commit);
        //取消按键监听
        bt_put_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
            }
        });
        //确认按键监听
        bt_put_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取两个输入框的值
                String pwd = put_pwd.getText().toString();
                String get_pwd = sp.getString("pwd", null);
                if (pwd.equals(get_pwd)){
                    alertDialog2.dismiss();
                    MyToast.makeshow(activity,"密码输入正确",Toast.LENGTH_SHORT);
                   // Toast.makeText(context,"密码输入正确！",Toast.LENGTH_SHORT).show();
                    boolean saft_setting_finish = sp.getBoolean("saft_setting_finish", false);
                    if (saft_setting_finish){
                        Intent intent=new Intent(activity.getApplicationContext(),Saft_Centre_activity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    }else {
                        Intent intent=new Intent(activity.getApplicationContext(),Soft_viewpage_activity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    }

                    activity.finish();

                }else{
                    //设置一个自定义的补间动画，用来提示用户输入不符合要求
                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.shake);
                    animation.setFillAfter(true);
                    bt_put_commit.startAnimation(animation);
                }
            }
        });


    }

}

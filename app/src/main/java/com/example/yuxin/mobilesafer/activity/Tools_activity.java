package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.SmsInfo;
import com.example.yuxin.mobilesafer.ui.MyToast;
import com.example.yuxin.mobilesafer.utils.SmsUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/27 0022 上午 9:57.
 * Version   1.0;
 * Describe :高级工具
 * History:
 * ==============================================================================
 */
public class Tools_activity extends Activity {

    private static final int REC_REQUESTCODE = 1100;
    private static final String TAG = "Tools_activity";
    private Button query_phone;
    private Button bt_save_sms;
    private Button bt_down_sms;
    private Button bt_commonnum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tools_layout);
        MyOnClickListener listener = new MyOnClickListener();
        query_phone = (Button) findViewById(R.id.query_phone);
        bt_save_sms = (Button) findViewById(R.id.save_sms);
        bt_down_sms = (Button) findViewById(R.id.down_sms);
        bt_commonnum=(Button) findViewById(R.id.bt_commonnum);

        query_phone.setOnClickListener(listener);
        bt_save_sms.setOnClickListener(listener);
        bt_down_sms.setOnClickListener(listener);
        bt_commonnum.setOnClickListener(listener);
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.query_phone:
                    Intent query_intent = new Intent(Tools_activity.this, Querynumber_activity.class);
                    startActivity(query_intent);
                    finish();
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                    break;
                case R.id.save_sms:
                    SmsBackUp();
                    break;
                case R.id.down_sms:
                    SmsRecovery();
                    break;
                case R.id.bt_commonnum:
                    Intent commonnum = new Intent(Tools_activity.this, Commonnum_activity.class);
                    startActivity(commonnum);
                    finish();
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                default:
                    break;
            }
        }
    }

    /**
     * 调用系统文件选择器，获取xml文件
     */
    private void SmsRecovery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REC_REQUESTCODE);
    }

    /**
     * 短信备份
     */
    private void SmsBackUp() {

        final ProgressDialog pb = new ProgressDialog(this);
        pb.setMessage("正在进行短信备份...");
        pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pb.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                int smscount = SmsUtils.backup(Tools_activity.this, pb);
                //判断是否备份成功
                if (smscount != -1) {
                    Looper.prepare();
                    pb.dismiss();
                    MyToast.makeshow(Tools_activity.this, "成功备份" + smscount + "条短信！", Toast.LENGTH_SHORT);
                    Looper.loop();
                } else {
                    Looper.prepare();
                    pb.dismiss();
                    MyToast.makeshow(Tools_activity.this, "短信备份失败！", Toast.LENGTH_SHORT);
                    Looper.loop();
                }

            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REC_REQUESTCODE) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String string = uri.toString();
            final File file;
            String a[] = new String[2];
            //判断文件是否在sd卡中
            if (string.indexOf(String.valueOf(Environment.getExternalStorageDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getExternalStorageDirectory()));
                //获取到file
                file = new File(Environment.getExternalStorageDirectory(), a[1]);
            } else if (string.indexOf(String.valueOf(Environment.getDataDirectory())) != -1) { //判断文件是否在手机内存中
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getDataDirectory()));
                //获取到file
                file = new File(Environment.getDataDirectory(), a[1]);
            } else {
                //出现其他没有考虑到的情况
                MyToast.makeshow(this, "文件路径解析失败！", Toast.LENGTH_SHORT);
                return;
            }
            final ProgressDialog repb = new ProgressDialog(this);
            repb.setMessage("正在进行短信还原...");
            repb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            repb.show();

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    List<SmsInfo> re_sms=new ArrayList<SmsInfo>();
                    int recount=-1;
                    List<SmsInfo> smsInfos = SmsUtils.RecoverySms(Tools_activity.this, file);
                   //判断选择的xml是否解析出了sms对象
                    if (smsInfos==null){
                        MyToast.makeshow(Tools_activity.this, "请选择正确的短信还原文件！", Toast.LENGTH_SHORT);
                        recount=-1;
                        repb.dismiss();
                    }else {
                        for (SmsInfo info : smsInfos) {
                            //判断要添加的短信是否已经存在，根据date（时间）为13位具有一定的唯一性，来判断是否是相同的短信
                            boolean smsExist = SmsUtils.isSmsExist(Tools_activity.this, info);
                           if (!smsExist){
                               re_sms.add(info);
                           }
                        }
                        for (SmsInfo info:re_sms) {
                            i(TAG, "run" + "info"+info.toString());

                        }
                        //获取还原短信条数，如果返回-1，表示出现错误，还原失败
                        recount= SmsUtils.UpdateSms(Tools_activity.this, re_sms, repb);
                        i(TAG, "run" + recount);
                    }
                    //判断已经还原短信的条数，跳出不同提示框
                    if (recount != -1) {
                         if (recount ==0) {
                            Looper.prepare();
                            repb.dismiss();
                            MyToast.makeshow(Tools_activity.this, "未读取到短信或由于系统限制无法写入短信！", Toast.LENGTH_SHORT);
                            Looper.loop();
                        }else{
                             Looper.prepare();
                             repb.dismiss();
                             MyToast.makeshow(Tools_activity.this, "成功还原" + recount + "条短信！", Toast.LENGTH_SHORT);
                             Looper.loop();
                         }
                    } else {
                        Looper.prepare();
                        repb.dismiss();
                        MyToast.makeshow(Tools_activity.this, "短信还原失败！", Toast.LENGTH_SHORT);
                        Looper.loop();
                    }
                }
            }.start();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Tools_activity.this, Main_activity.class);
            startActivity(main_intent);
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

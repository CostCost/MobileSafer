package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.Updateinfo;
import com.example.yuxin.mobilesafer.ui.MyToast;
import com.example.yuxin.mobilesafer.utils.FileUtils;
import com.example.yuxin.mobilesafer.utils.NetUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * =============================================================================
 * Copyright (c) 2016 yuxin All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/18 0023 上午 11:12.
 * Version   1.0;
 * Describe : 欢迎界面
 * History:
 * ==============================================================================
 */
public class Splash_activity extends Activity {

    private static final String TAG = "Splash_activity";
    private static final int NO_UPDATE = 0;
    private static final int NEED_UPDATE = 1;
    private static final int UNABLE_UPDATE =2 ;
    private static final int DOWNLOAD_ERROR = 3;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        //设置版本号
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号"+getVersion());
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean isautoupdate = sp.getBoolean("isautoupdate", true);
        if (isautoupdate){
        //检查版本更新
        checkNetWork();
        }else {
            loadMainActivity();
        }

    }
    /**
     * 对连接服务器后获取的版本更新信息进行分类处理
     */
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NEED_UPDATE:
                    showAlertDialog((Updateinfo) msg.obj);
                break;
                case NO_UPDATE:
                    Log.i(TAG,"已经是最新版本！不用更新");
                    //跳转到主界面
                    loadMainActivity();
                break;
                case UNABLE_UPDATE:
                 //   MyToast.makeshow(Splash_activity.this,"连接服务器失败！无法检查版本更新!",Toast.LENGTH_SHORT);
                    Log.i(TAG,"连接服务器失败！无法检查版本更新");
                    //跳转到主界面
                    loadMainActivity();
                break;
                case DOWNLOAD_ERROR:
                    Log.i(TAG,"最新版本apk下载失败！");
                    MyToast.makeshow(Splash_activity.this,"最新版apk下载失败",Toast.LENGTH_SHORT);
                    break;

            }


        }
    };

    /**
     * 弹出更新提示对话框，传入更新提示信息
     * @param updateinfo
     */
    private void showAlertDialog(final Updateinfo updateinfo) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //提示标题
        builder.setTitle("更新提醒：");
        //提示内容
        builder.setMessage(updateinfo.getDescription());
        //按返回键不能使得对话框消失
        builder.setCancelable(false);
        //对话框左侧按钮监听
        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewVerion(updateinfo.getUrl());

            }
        });
        //对话框右侧按钮监听
        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //跳转到主界面
                loadMainActivity();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * 下载最新版本的apk
     * @param path
     */
    private void downloadNewVerion(final String path) {
        new Thread(){
            @Override
            public void run() {
                //判断是否挂载是sd卡，如果没有直接跳过更新，需要注意的是Environment.MEDIA_MOUNTED是String类型不是int
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                MyToast.makeshow(Splash_activity.this,"没有挂载SD卡!",Toast.LENGTH_SHORT);
                return;
            }
        try {
            //oop的联网操作
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget=new HttpGet(path);
            HttpResponse response = client.execute(httpget);
            //网络连接正常返回200
            if (response.getStatusLine().getStatusCode()==200){
                InputStream is = response.getEntity().getContent();
                //在sd卡new一个file
                File file=new File(Environment.getExternalStorageDirectory(), FileUtils.getFileName(path));
                Log.i(TAG,file.getAbsolutePath());
                FileOutputStream fos=new FileOutputStream(file);
                //边读边写的操作
                byte[] buffer=new byte[1024];
                int lens=0;
                while((lens=is.read(buffer))!=-1){
                    fos.write(buffer,0,lens);
                }
                //关流
                fos.close();
                is.close();
                Log.i(TAG,"文件下载成功！");
//                <intent-filter>
//                <action android:name="android.intent.action.VIEW" />
//                <action android:name="android.intent.action.INSTALL_PACKAGE" />
//                <category android:name="android.intent.category.DEFAULT" />
//                <data android:scheme="file" />
//                <data android:mimeType="application/vnd.android.package-archive" />
//                </intent-filter>
                //激活系统的安装程序，将刚下载的文件传入intent
                Intent intent=new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri data =Uri.fromFile(file);
                intent.setDataAndType(data,"application/vnd.android.package-archive");
                startActivity(intent);
            }else {
                //网络连接状态错误
                Log.i(TAG,"网络连接失败"+response.getStatusLine().getStatusCode());
                Message msg=Message.obtain();
                msg.what=DOWNLOAD_ERROR;
                mhandler.sendMessage(msg);
            }


        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"出现异常，下载失败");
            Message msg=Message.obtain();
            msg.what=DOWNLOAD_ERROR;
            mhandler.sendMessage(msg);
        }
            }
        }.start();


    }


    /**
     * 获取应用程序版本号
     * @return versionName
     */
    private String getVersion(){
        PackageInfo packageInfo = null;
        try {
            //获取包管理器
        PackageManager packageManager = getPackageManager();
            //获取包信息
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //返回应用版本号
        return packageInfo.versionName;

    }

    /**
     * 检查版本更新
     */
    private void checkNetWork() {
        //检查联网状态
        if (NetUtils.isConnected(this)==NetUtils.NO_CONNECTED){
            Log.i(TAG,"联网失败");
            //跳转到主界面
            loadMainActivity();
        }else{
            Log.i(TAG,"联网成功");
          new Thread(new Runnable() {
              private Updateinfo update;
              @Override
              public void run() {
                  try {
                      //oop的联网操作
                      //使用DefaultHttpClient()需要在对应module的buile.gradle中配置 useLibrary 'org.apache.http.legacy'
                      HttpClient client = new DefaultHttpClient();
                      //设置连接超时，当连接服务器时间超过3S，直接进入主界面
                      client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
                      String url = getResources().getString(R.string.updateinfo_url);
                      HttpGet httpGet = new HttpGet(url);
                      HttpResponse response = client.execute(httpGet);
                      if (response.getStatusLine().getStatusCode()==200){
                          InputStream is = response.getEntity().getContent();
                          XmlPullParser parser = Xml.newPullParser();
                          parser.setInput(is,"UTF-8");
                          int eventType = parser.getEventType();
                          //XML的pull解析
                          while (eventType!=XmlPullParser.END_DOCUMENT){
                              switch (eventType){
                                  case XmlPullParser.START_TAG:
                                      String name = parser.getName();
                                      if (name.equals("updateinfo")){
                                          update = new Updateinfo();
                                      }
                                      else if(name.equals("version")){
                                            update.setVersion(parser.nextText());
                                          }
                                      else if(name.equals("url")){
                                          update.setUrl(parser.nextText());
                                      }
                                      else if(name.equals("description")){
                                          update.setDescription(parser.nextText());
                                      }
                                      break;
                                  default:
                                      break;
                              }

                              eventType=parser.next();
                          }
                      }
                    Log.i(TAG,"服务器连接成功"+update.toString());
                      //如果当前版本号与服务器最新版本号相等则不提示升级
                     if (update.getVersion().equals(getVersion())){
                          Message msg=Message.obtain();
                          msg.what=NO_UPDATE;
                          mhandler.sendMessage(msg);
                      }else {
                          Message msg=Message.obtain();
                          msg.what=NEED_UPDATE;
                          msg.obj=update;
                          mhandler.sendMessage(msg);
                      }

                  } catch (Exception e) {
                      Log.i(TAG,"连接服务器获取版本更新失败！");
                      Message msg=Message.obtain();
                      msg.what=UNABLE_UPDATE;
                      mhandler.sendMessage(msg);
                      e.printStackTrace();
                  }

              }
          }).start();

        }
    }

    /**
     * 跳转到用户主界面
     */
    public void loadMainActivity(){
        Intent intent=new Intent(this, Main_activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

}

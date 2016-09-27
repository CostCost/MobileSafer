package com.example.yuxin.mobilesafer.receiver;

import android.Manifest;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.example.yuxin.mobilesafer.R;

import java.util.ArrayList;

import static android.util.Log.i;

/**
 * Created by yuxin on 2016/7/26 0026.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        i(TAG, "onReceive" + "短信来了！！！！");
        final SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean isprotected = sp.getBoolean("isprotected", false);

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : pdus) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            //获取到来电号码
            final String address = message.getDisplayOriginatingAddress();
            //获取短信内容
            String body = message.getDisplayMessageBody();
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName componentName = new ComponentName(context, MyAdmin.class);
            boolean adminActive = dpm.isAdminActive(componentName);
            //验证手机是否处于保护状态
            if (isprotected && adminActive) {
                if ("#*location*#".equals(body)) {
                    //GPS追踪
                    i(TAG, "SMSonReceive" + "GPS追踪");
                    //中断短信广播
                    abortBroadcast();
                    //获得位置管理器
                    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    //验证权限
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //调用定位
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                //经度
                                double longitude = location.getLongitude();
                                //纬度
                                double latitude = location.getLatitude();
                                //向发来请求位置的号码发送当前手机位置
                                String message="您的手机当前位置在经度："+longitude+",纬度："+latitude+"!";
                                SmsManager manager = SmsManager.getDefault();
                                ArrayList<String> list = manager.divideMessage(message);  //因为一条短信有字数限制，因此要将长短信拆分
                                for(String text:list){
                                    manager.sendTextMessage(address, null, text, null, null);
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                        return;
                    }


                }else if("#*alarm*#".equals(body)){
                    //播放报警音乐
                    i(TAG, "SMSonReceive" + "播放报警音乐");
                    //中断短信广播
                  MediaPlayer play=MediaPlayer.create(context,R.raw.alarm);
                    play.setLooping(true);
                    play.setVolume(1.0f,1.0f);
                    play.stop();
                    abortBroadcast();

                }else if("#*wipedata*#".equals(body)){
                    //擦出数据
                    i(TAG, "SMSonReceive" + "擦出数据");
                    //中断短信广播
                    abortBroadcast();
                    //擦除数据，手机将重启恢复出厂设置
                    dpm.wipeData(0);

                }else if("#*lockscreen*#".equals(body)){
                    //重置锁屏
                    i(TAG, "SMSonReceive" + "重置锁屏");
                    //中断短信广播
                    abortBroadcast();
                    //获取到安全卫士密码设置为锁屏密码
                    String pwd = sp.getString("pwd", null);
                    //
                    dpm.resetPassword(pwd,0);
                    dpm.lockNow();

                }

            }
        }
    }
}

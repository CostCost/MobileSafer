package com.example.yuxin.mobilesafer.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.yuxin.mobilesafer.activity.Sms_Call_Safe_activity;
import com.example.yuxin.mobilesafer.dao.SmsBlackdao;
import com.example.yuxin.mobilesafer.engine.Contact_info_Engine;

import java.lang.reflect.Method;

/**
 * 短信与电话拦截服务
 * Created by yuxin on 2016/8/5 0005.
 */
public class SmsAbortService extends Service {

    private static final int ABORT_PHONE = 100;
    private SmsReceiver smsReceiver;
    private SmsBlackdao smsBlackdao;
    private TelephonyManager tm;
    private PhoneStateListener listener;
    long startringing ;
    long endringing;
    private NotificationManager ntf;

    @Override
    public void onCreate() {
        super.onCreate();
        smsReceiver = new SmsReceiver();
        smsBlackdao = new SmsBlackdao(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVER");
        registerReceiver(smsReceiver, filter);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        ntf = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //闲置状态
                        if (startringing>0){
                            //如果响铃时间少于一秒，并却号码不在联系人列表里面
                            endringing=System.currentTimeMillis();
                            if (endringing-startringing>0&&endringing-startringing<1000&& !Contact_info_Engine.queryContact(getContentResolver(),incomingNumber)){

                                //弹出提示框
//                            、String note="这是一条电话通知，来自于手动编写，我要将他显示在通知里面";
//                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                                    NotificationManager  nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                    Notification.Builder notiBuilder = new Notification.Builder(this);
//                                    notiBuilder.setContentTitle("通知标题");
//                                    notiBuilder.setContentText(note);
//                                    notiBuilder.setSmallIcon(android.R.drawable.ic_dialog_email);
//                                    notiBuilder.setTicker("这将会在状态栏滚动显示!这将会在状态栏滚动显示!这将会在状态栏滚动显示!这将会在状态栏滚动显示!");
//                                    Intent intent=new Intent();
//                                    intent.setAction(Intent.ACTION_DIAL);
//                                    intent.setData(Uri.parse("tel:888888"));
//                                    Intent[] intents=new Intent[]{intent};
//                                    PendingIntent pintent=PendingIntent.getActivities(this,100,intents,0);
//                                    notiBuilder.setContentIntent(pintent);
//                                    Notification noti = notiBuilder.build();
//                                    noti.flags=Notification.FLAG_AUTO_CANCEL;
//                                    nm.notify(100,noti);
//                                }

                                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                                builder.setContentTitle("拦截到来电响一声");
                                builder.setContentText("请注意，极有可能是诈骗电话！点击加入黑名单！");
                                builder.setSmallIcon(android.R.drawable.stat_notify_missed_call);
                                builder.setTicker("拦截到来电响一声！");
                                Intent intent=new Intent(getApplicationContext(), Sms_Call_Safe_activity.class);
                                intent.putExtra("number",incomingNumber);

                                Intent[] intents=new Intent[]{intent};
                                PendingIntent pendingintent=PendingIntent.getActivities(getApplicationContext(),ABORT_PHONE,intents,PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingintent);
                                Notification noti = builder.build();
                                noti.flags=Notification.FLAG_AUTO_CANCEL;
                                ntf.notify(ABORT_PHONE,noti);

                            }
                            startringing=0;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        //响铃状态
                        //判断是否是电话拦截号码
                        if (smsBlackdao.isAbortPhone(incomingNumber)) {
                            //挂断电话
                            doEndCall(incomingNumber);
                            return;
                        }
                       startringing=System.currentTimeMillis();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //接听状态
                        break;
                    default:
                        break;
                }
            }


        };



    }

    /**
     * 利用反射和aidl挂断电话
     * ITelephony的源码路径Android_K\frameworks\base\telephony\java\com\android\internal\telephony\ITelephony.aidl
     * 依赖的有一个文件NeighboringCellInfo路径Android_K\frameworks\base\telephony\java\android\telephony\NeighboringCellInfo.aidl
     */
    private void doEndCall(String incomingNumber) {
        //public final class ServiceManager
//        public static IBinder getService(String name) {
//            try {
//                IBinder service = sCache.get(name);
//                if (service != null) {
//                    return service;
//                } else {
//                    return getIServiceManager().getService(name);
//                }
//            } catch (RemoteException e) {
//                Log.e(TAG, "error in getService", e);
//            }
//            return null;
//        }
        try {
            //根据类名找到对应的clazz
            Class clazz = Class.forName("ServiceManager");
            //根据方法名和传参类型找到对应方法
            Method method = clazz.getMethod("getService", String.class);
            //调用getService方法，因为是static方法，所以第一个参数可以为null，iBinder是一个代理对象，需要转化为真实对象
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            //将iBinder代理对象转化为真实对象
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
            //删除通话记录
            Uri uri = CallLog.Calls.CONTENT_URI;
            MyContentObserver observer = new MyContentObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(uri, true, observer);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        private String incomingNumber;

        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Uri uri = CallLog.Calls.CONTENT_URI;
            String where = CallLog.Calls.NUMBER + "=?";

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            getContentResolver().delete(uri, where, new String[]{incomingNumber});
            getContentResolver().unregisterContentObserver(this);

        }

    }



    /**
     * 短信广播接收者，用于拦截短信操作
     */
    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objects) {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String number = smsMessage.getDisplayOriginatingAddress();
                if (smsBlackdao.isAbortSms(number)) {
                    abortBroadcast();
                }

            }

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }
}

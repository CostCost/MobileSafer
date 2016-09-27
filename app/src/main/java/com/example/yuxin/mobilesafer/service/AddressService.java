package com.example.yuxin.mobilesafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.dao.Addressdao;

import static android.util.Log.i;

/**
 * Created by yuxin on 2016/7/29 0029.
 */
public class AddressService extends Service {

    private static final String TAG ="AddressService";
    private WindowManager wm;
    private LayoutInflater mInflater;
    private View view;
    private SharedPreferences sp;
    private MyBroadcastReceiver receiver;
    private int screewidth;
    private int screeheight;
    private WindowManager.LayoutParams params;


    @Override
    public void onCreate() {
        super.onCreate();
        mInflater = LayoutInflater.from(getApplicationContext());
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        Display defaultDisplay = wm.getDefaultDisplay();
        screewidth = defaultDisplay.getWidth();
        screeheight = defaultDisplay.getHeight();

        sp = getSharedPreferences("config", MODE_PRIVATE);

        receiver = new MyBroadcastReceiver();
        IntentFilter fileter=new IntentFilter();
        fileter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
         registerReceiver(receiver, fileter);

    }
    private class  MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            i(TAG, "onReceive" + "我在打电话！");
            String resultData = getResultData();
            ShowAddress(resultData);


        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (view != null) {
                        wm.removeView(view);
                        view=null;
                    }

                    break;
                 case TelephonyManager.CALL_STATE_RINGING:
                     ShowAddress(incomingNumber);
                     break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示电话号码归属地
     * @param incomingNumber 需要查询的号码
     */
    private void ShowAddress(String incomingNumber) {
        //创建一个窗口
        params = new WindowManager.LayoutParams();
        //高度包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //宽度包裹内容
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //半透明状态
        params.format = PixelFormat.TRANSLUCENT;


        //toast风格
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

                     //获取首选项存储的位置，设置控件的位置
                     int x = sp.getInt("X", 0);
                     int y = sp.getInt("Y", 0);

        params.x=x;
        params.y=y;

        //保持屏幕高亮，不获取焦点，不能触摸
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
             //   | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        view = mInflater.inflate(R.layout.show_address, null);
        TextView tv_show_address = (TextView) view.findViewById(R.id.tv_show_address);
        String address = Addressdao.getAddress(getApplicationContext(), incomingNumber);
        i(TAG, "ShowAddress" + address);
        tv_show_address.setText(address+"  ");
        wm.addView(view, params);

        //对控件进行触摸监听
        view.setOnTouchListener(new View.OnTouchListener() {

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


                        params.x= (int) (params.x+moveX);
                        params.y= (int) (params.y+moveY);

                     wm.updateViewLayout(view, params);


                        break;
                    case MotionEvent.ACTION_UP:
                        // i(TAG, "onTouch:ACTION_UP");
                        startX = stopX;
                        startY = stopY;
                        //存储当前位置
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putInt("X", params.x);
                        edit.putInt("Y", params.y);
                        i(TAG, "onTouch" + "X:"+params.x+"Y:"+params.y);
                        edit.commit();

                        break;
                    default:
                        break;
                }
                return false;
            }
        });


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

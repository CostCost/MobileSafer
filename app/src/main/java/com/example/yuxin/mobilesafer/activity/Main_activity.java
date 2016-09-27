package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.ui.MyToast;
import com.example.yuxin.mobilesafer.utils.MyLogger;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/20 0023 上午 11:12.
 * Version   1.0;
 * Describe : 主界面activity
 * History:
 * ==============================================================================
 */
public class Main_activity extends Activity {

    private GridView gv;
    private long mExitTime;
    private SharedPreferences sp;
    private static final  String TAG="Main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        gv = (GridView)  findViewById(R.id.gv);
        MyGridAdapter adapter=new MyGridAdapter();
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new MyGridOnItemClickListener());
        MyLogger log=MyLogger.slog();
        log.i("这是一个很吊的日志管理器");

    }

    /**
     * GridView的item点击操作
     */
    private class MyGridOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    Soft_Modle soft=new Soft_Modle(getApplicationContext(),Main_activity.this);
                    soft.start();
                    break;
                case 1:
                    Intent sms_safe_intent=new Intent(getApplicationContext(),Sms_Call_Safe_activity.class);
                    startActivity(sms_safe_intent);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 2:
                    Intent soft_manager=new Intent(getApplicationContext(),Soft_Manager_activity.class);
                    startActivity(soft_manager);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 3:
                    Intent task_manager=new Intent(getApplicationContext(),Task_Manager_activity.class);
                    startActivity(task_manager);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 4:
                    Intent traffic_manager=new Intent(getApplicationContext(),Traffic_Manager_activity.class);
                    startActivity(traffic_manager);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 5:
                    Intent antivirus_manager=new Intent(getApplicationContext(),Antivirus_activity.class);
                    startActivity(antivirus_manager);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 6:
                    Intent cache_intent=new Intent(getApplicationContext(),Cache_Manager_activity.class);
                    startActivity(cache_intent);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 7:
                    Intent tools_intent=new Intent(getApplicationContext(),Tools_activity.class);
                    startActivity(tools_intent);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                case 8:
                    Intent setting_intent=new Intent(getApplicationContext(),Setting_Centre_activity.class);
                    startActivity(setting_intent);
                    finish();
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * GridView的适配器
     */
    private  class MyGridAdapter extends BaseAdapter{
        String[] strings=new String[]{"手机防盗","通讯卫士","软件管理",
                                        "进程管理","流量统计","手机杀毒",
                                        "缓存清理","高级工具","设置中心"};
        int[] images=new int[]{R.drawable.selector_main_gv_iv_clock,R.drawable.selector_main_gv_iv_person,R.drawable.selector_main_gv_iv_soft,
                R.drawable.selector_main_gv_iv_manager,R.drawable.selector_main_gv_iv_clound,R.drawable.selector_main_gv_iv_scaner,
                R.drawable.selector_main_gv_iv_delete,R.drawable.selector_main_gv_iv_utils,R.drawable.selector_main_gv_iv_home
        };
        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.main_layout_item, null);
            TextView tv= (TextView) view.findViewById(R.id.ic_tv);
            ImageView iv= (ImageView)view. findViewById(R.id.ic_mian);
            tv.setText(strings[position]);
            iv.setBackgroundResource(images[position]);
            return view;
        }
    }
    /**
     * 点击两次后退键，退出应用程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
               // Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                MyToast.makeshow(Main_activity.this,"再按一次退出安全卫士",Toast.LENGTH_SHORT);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);

    }

}

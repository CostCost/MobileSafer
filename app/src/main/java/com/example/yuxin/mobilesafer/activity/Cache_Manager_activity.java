package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.CacheInfo;
import com.example.yuxin.mobilesafer.domain.TaskInfo;
import com.example.yuxin.mobilesafer.engine.Cache_Manager_Engine;
import com.example.yuxin.mobilesafer.engine.ProcessManager;
import com.example.yuxin.mobilesafer.engine.Task_info_Engine;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

;import static android.util.Log.i;


/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/12 0022 上午 11:52.
 * Version   1.0;
 * Describe :缓存管理器
 * History:
 */
public class Cache_Manager_activity extends Activity {

    private static final String TAG = "Cache_Manager_activity";
    private static final int SUCESS = 100;
    private static final int REQUESTCODE =101 ;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //进程加载成功
                case SUCESS:
                    infos = (List<CacheInfo>) msg.obj;
                    adapter = new MyAdapter(infos);
                    lv_cache.setAdapter(adapter);
                    rl_pbandtv.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
    private Button bt_cache_clean;
    private ListView lv_cache;
    private MyAdapter adapter;
    private ProgressBar cache_pb;
    private RelativeLayout rl_pbandtv;
    private List<CacheInfo> infos;
    private Cache_Manager_Engine cache_manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cache_layout);
        bt_cache_clean = (Button) findViewById(R.id.bt_cache_clean);
        lv_cache = (ListView) findViewById(R.id.lv_cache);
        cache_pb=(ProgressBar) findViewById(R.id.cache_pb);
        rl_pbandtv = (RelativeLayout) findViewById(R.id.rl_pbandtv);

        //获取缓存目录
        cache_manager = new Cache_Manager_Engine(Cache_Manager_activity.this, Cache_Manager_activity.this,cache_pb);
        cache_manager.getCacheInfos();

        bt_cache_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // @SystemApi
//                public void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer) {
//                    freeStorageAndNotify(null, freeStorageSize, observer);
//                }
                try {
                PackageManager pm = getPackageManager();
                    Class clazz=Class.forName("android.content.pm.PackageManager");
                    Method method = clazz.getMethod("freeStorageAndNotify", new Class[]{Long.TYPE, IPackageDataObserver.class});
                    method.invoke(pm,new Object[]{getEnvironmentSize(),new IPackageDataObserver.Stub(){

                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                        }
                    }});
                    if (!(infos==null||infos.size()==0)){
                        infos=null;
                        adapter.notifyDataSetChanged();
                    }
                    cache_manager.getCacheInfos();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        lv_cache.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheInfo clickcache = (CacheInfo) adapter.getItem(position);
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                // dat=package:com.itheima.mobileguard
                intent.setData(Uri.parse("package:" + clickcache.getPackname()));
//                startActivity(intent);
               startActivityForResult(intent,REQUESTCODE);
            }
        });

    }

    /**
     * 数据加载完成
     * @param cacheInfos
     */
    public void finishDate(List<CacheInfo> cacheInfos) {
        if (cacheInfos.size() > 0) {
            Message msg = Message.obtain();
            msg.obj = cacheInfos;
            msg.what = SUCESS;
            mhandler.sendMessage(msg);

        }
    }

    /**
     * 获取硬盘大小
     * @return
     */
    public Long getEnvironmentSize(){
        File directory = Environment.getDataDirectory();
        Long size;
        if (directory==null){
            size=0L;
        }else{
            String path = directory.getPath();
            StatFs statfs=new StatFs(path);
            long blockSize = statfs.getBlockSizeLong();
            size= statfs.getBlockCountLong() * blockSize;
        }
        return size;
    }
    /**
     * 适配器
     */
    private class MyAdapter extends BaseAdapter {
        private List<CacheInfo> infos;
        public MyAdapter(List<CacheInfo> infos) {
            this.infos=infos;
        }

        //获取条目总数
        @Override
        public int getCount() {

            return infos.size();
        }

        //获取指定条目对象
        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        //获取指定条目id
        @Override
        public long getItemId(int position) {
            return position;
        }

        //获取条目的视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder=null;
            if (convertView!=null){
                view=convertView;
                holder= (ViewHolder) view.getTag();
            }else{
                 view = LayoutInflater.from(Cache_Manager_activity.this).inflate(R.layout.cache_item_layout, null);
                holder=new ViewHolder();
                holder.cache_item_iv = (ImageView) view.findViewById(R.id.cache_item_iv);
                holder.cache_name = (TextView) view.findViewById(R.id.cache_name);
                holder.cache_size = (TextView) view.findViewById(R.id.cache_size);
                holder.bt_uninstall = (Button) view.findViewById(R.id.bt_uninstall);
                view.setTag(holder);
            }
                holder.cache_item_iv.setImageDrawable(infos.get(position).getDrawable());
                holder.cache_name.setText(infos.get(position).getName());
                holder.cache_size.setText(Formatter.formatFileSize(Cache_Manager_activity.this,infos.get(position).getCachesize()).toString());
            return view;
        }
    }

    private static class ViewHolder {
        ImageView cache_item_iv;
        TextView cache_name;
        TextView cache_size;
        Button bt_uninstall;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //onActivityResultrequestCode:100resultCode:0data:null
        if (requestCode==REQUESTCODE&&requestCode==0){
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Cache_Manager_activity.this, Main_activity.class);
            startActivity(main_intent);
            finish();
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

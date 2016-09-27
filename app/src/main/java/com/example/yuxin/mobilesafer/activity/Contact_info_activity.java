package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.Contact_info;
import com.example.yuxin.mobilesafer.engine.Contact_info_Engine;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/23 0022 上午 9:57.
 * Version   1.0;
 * Describe :获取手机联系人信息
 * History:
 * ==============================================================================
 */
public class Contact_info_activity extends Activity {
    private static final int GET_CONTACT = 1;
    public static final int CONTACT_RESPONSECODE = 200;
    private static final String TAG = "Contact_info_activity";
    private static final int GET_UPDATE_OK =2 ;
    private boolean isLoading=false;
    //初始位置
    private int startid=0;
    //联系人总的个数
    private int total;
    //每页显示的个数
    private int block=15;

    //对控件进行操作
    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (adapter==null){
                    adapter = new MyAdapter(contactInfos);
            }
            switch (msg.what) {
                case GET_CONTACT:
                    listView.addFooterView(footer);
                    listView.setAdapter(adapter);
                    //使覆盖在表层的progressbar消失
                    rl.setVisibility(View.GONE);
                    listView.removeFooterView(footer);
                    break;
                case GET_UPDATE_OK:
                    isLoading=false;
                    adapter.notifyDataSetChanged();
                    listView.removeFooterView(footer);
                    break;
                default:
                    break;
            }

        }
    };
    //用来存储所有联系人的对象
    private List<Contact_info> contactInfos;
    private List<Contact_info> info;
    //用来装滚动信息
    private ListView listView;
    private RelativeLayout rl;
    private  MyAdapter adapter;
    private View footer;
    private ProgressBar contact_pb;

    public Contact_info_activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_info_activity);

        listView = (ListView) findViewById(R.id.contact_lv);
        rl = (RelativeLayout) findViewById(R.id.rl_pb);

        contact_pb = (ProgressBar) findViewById(R.id.contact_pb);
        contactInfos=new ArrayList<Contact_info>();

        //获取联系人总个数
        total=Contact_info_Engine.getAllContactCounts(getContentResolver());

        //加载脚视图
        footer = getLayoutInflater().inflate(R.layout.footer, null);
        i(TAG, "footer:" + footer);
        //获取联系人并显示
        getCantactInfo();

       // listView.removeFooterView(footer);
        //对List进行监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact_info item = (Contact_info) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                //设置返回number
                setResult(CONTACT_RESPONSECODE, intent);
                //关闭当前的Activity
                finish();
                overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            //firstVisibleItem第一个可见item的id， visibleItemCount可见的总item个数，totalItemCount listview当前容纳的item个数
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem+visibleItemCount==totalItemCount){
                    //滑到了底部
                    if (!isLoading){

                        if (total==totalItemCount){
                            isLoading=true;
                            i(TAG, "onScroll" + "已经没有更多的信息了");
                        }else {
                         isLoading=true;
                        listView.addFooterView(footer);
                        getLoadData(totalItemCount,block);

                        }
                    }
                }


            }
        });
    }


    /**
     * 获取联系人
     */
    public void getCantactInfo() {
        //获取联系人信息是耗时操作，写在子线程里面
        i(TAG, "getCantactInfo:" );
        new Thread() {
            @Override
            public void run() {
                super.run();
                //模拟延时加载
                SystemClock.sleep(1000);
                //获取联系人对象

                contactInfos = Contact_info_Engine.getContactInfiByLimit(getContentResolver(),startid,block,null);

                Message message = Message.obtain();
                message.what = GET_CONTACT;
               // message.obj = contactInfi;
                mhandler.sendMessage(message);
            }
        }.start();
    }

    public void getLoadData(final int start, final int blocks){
        //获取联系人信息是耗时操作，写在子线程里面
        new Thread() {
            @Override
            public void run() {
                super.run();
                //使等待一秒让获取联系人操作有足够的时间加载
                SystemClock.sleep(1000);
                //获取联系人对象
                contact_pb.setProgress(0);
                contact_pb.setMax(blocks);
                info = Contact_info_Engine.getContactInfiByLimit(getContentResolver(),start,blocks,contact_pb);
                contactInfos.addAll(info);
                Message message = Message.obtain();
                message.what = GET_UPDATE_OK;
                // message.obj = contactInfi;
                mhandler.sendMessage(message);
            }
        }.start();

    }


    /**
     * 写一个适配器给listview
     */
    private class MyAdapter extends BaseAdapter {
        List<Contact_info> infos;

        public MyAdapter(List<Contact_info> infos) {
            this.infos = infos;
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder = null;
            if (convertView != null) {
                i(TAG, "getView" + "使用缓存获取view，position：" + position);
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                i(TAG, "getView" + "重新加载获取view，position：" + position);
                view = getLayoutInflater().inflate(R.layout.contact_item_layout, null);
                holder = new ViewHolder();
                //对listview里面的控件进行赋值操作
                holder.tv_name = (TextView) view.findViewById(R.id.contact_name);
                holder.tv_number = (TextView) view.findViewById(R.id.contact_number);
                view.setTag(holder);
            }

            holder.tv_name.setText("姓名：" + infos.get(position).getName());
            holder.tv_number.setText("号码：" + infos.get(position).getNumber());
            return view;
        }
    }

    /**
     * 创建一个view的实体
     */
    static class ViewHolder {
        TextView tv_name;
        TextView tv_number;

    }
}

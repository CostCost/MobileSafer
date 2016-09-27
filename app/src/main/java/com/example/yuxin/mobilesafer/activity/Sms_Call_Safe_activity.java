package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.dao.SmsBlackdao;
import com.example.yuxin.mobilesafer.domain.BlackPerson;
import com.example.yuxin.mobilesafer.domain.Contact_info;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.util.List;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/1 0022 上午 9:27.
 * Version   1.0;
 * Describe :黑名单拦截
 * History:
 * ==============================================================================
 */
public class Sms_Call_Safe_activity extends Activity {
    public static final int CONTACT_REQUESTCODE = 100;
    private static final String TAG = "Sms_Call_Safe_activity";
    private Button bt_title_add;
    private ImageView iv_sms_call_person;
    private View dialogView;
    private EditText put_black_number;
    private CheckBox cb_abort_sms;
    private CheckBox cb_abort_phone;
    private Button bt_cancel;
    private Button bt_add_black_number;
    private SmsBlackdao smsdao;
    private AlertDialog.Builder alertDialog = null;
    private String name = "";
    private List<BlackPerson> blackPersons;
    private MyAdapter adapter;
    private AlertDialog alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sms_call_safe_layout);
        //获取数据库操作类
        smsdao = new SmsBlackdao(this);
        //加载要显示的dialog的view对象
        dialogView = LayoutInflater.from(Sms_Call_Safe_activity.this).inflate(R.layout.sms_call_dialog, null);
        //右上角添加按钮
        bt_title_add = (Button) findViewById(R.id.bt_title_add);

        ListView lv_sms_safe = (ListView) findViewById(R.id.lv_sms_safe);
        //查询数据库中已有的全部数据
        blackPersons = smsdao.query();
        adapter = new MyAdapter(blackPersons);
        lv_sms_safe.setAdapter(adapter);

        //对话框的所有控件
        iv_sms_call_person = (ImageView) dialogView.findViewById(R.id.iv_sms_call_person);
        put_black_number = (EditText) dialogView.findViewById(R.id.put_black_number);
        cb_abort_sms = (CheckBox) dialogView.findViewById(R.id.cb_abort_sms);
        cb_abort_phone = (CheckBox) dialogView.findViewById(R.id.cb_abort_phone);
        bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
        bt_add_black_number = (Button) dialogView.findViewById(R.id.bt_add_black_number);

//监听事件的统一处理
        MyOnClickListener listener = new MyOnClickListener();
        bt_title_add.setOnClickListener(listener);
        iv_sms_call_person.setOnClickListener(listener);
        bt_cancel.setOnClickListener(listener);
        bt_add_black_number.setOnClickListener(listener);


        String number = getIntent().getStringExtra("number");
        if (!TextUtils.isEmpty(number)){
            alertDialog = new AlertDialog.Builder(Sms_Call_Safe_activity.this, R.style.windowFrame_no_title);
            alertDialog.setView(dialogView);
            //这一步很重要，因为dialogView已经在上面加载了，而alertDialog在下面每一次都new一个新的对象
            //alertDialog.create()就像一个打包器，每一次吧dialogView和alertDialog打包成一个新的dialog
            //这样的话dialogView的父容器alertDialog每一次都不一样，也就相当于儿子每次都是那个，爸爸却不是那个了
            //这样处理就相当于，尽管他new过了AlertDialog.Builder，但我还是用以前的那个，避免了上面的问题
            put_black_number.setText(number);
            if (alertdialog == null) {
                alertdialog = alertDialog.create();
            }
            alertdialog.show();
        }

    }

    /**
     * listview的adapter
     */
    private class MyAdapter extends BaseAdapter {
        List<BlackPerson> blackPersons;

        //含参构造，将外面的list数据传入
        public MyAdapter(List<BlackPerson> blackPersons) {
            this.blackPersons = blackPersons;
        }

        //用来重新设置listview中的数据
        public void setBlackPersons(List<BlackPerson> blackPersons) {
            this.blackPersons = blackPersons;
        }

        //获取item总数
        @Override
        public int getCount() {
            return blackPersons.size();
        }

        //返回当前的item对象
        @Override
        public Object getItem(int position) {
            return blackPersons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }




        //对item的view的加载
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder = null;

            if (convertView!=null){
                i(TAG, "getView" + "使用缓存获取view，position："+position);
                view=convertView;
                holder= (ViewHolder) view.getTag();
            }else{
                i(TAG, "getView" + "重新加载view，position："+position);
                view = getLayoutInflater().inflate(R.layout.sms_safe_lv_item_layout, null);
                holder=new ViewHolder();

                holder.tv_black_name = (TextView) view.findViewById(R.id.tv_black_name);
                holder.tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_sms_type = (TextView) view.findViewById(R.id.tv_sms_type);
                holder.imageview = (ImageView) view.findViewById(R.id.iv_sms_delete);

                view.setTag(holder);

            }

            //获取view的所有控件

            //获取当前position要存储的值
            BlackPerson blackPerson = blackPersons.get(position);
            holder.tv_black_name.setText("姓名：" + blackPerson.getName());
            holder.tv_black_number.setText("号码：" + blackPerson.getNumber());
            //对拦截号码类型进行判断
            int type = blackPerson.getType();
            switch (type) {
                case 1:
                    holder.tv_sms_type.setText("短信拦截");
                    break;
                case 2:
                    holder.tv_sms_type.setText("电话拦截");
                    break;
                case 3:
                    holder.tv_sms_type.setText("全部拦截");
                    break;
                default:
                    break;
            }
            //对imageview点击事件进行处理
            holder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //创建一个自定义的dialog
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(Sms_Call_Safe_activity.this, R.style.windowFrame_no_title);
                    //加载dialog要设置的view
                    View view = getLayoutInflater().inflate(R.layout.sim_changer_showinfo, null);
                    TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                    TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
                    //对dialog的title进行设置
                    tv_title.setText("提醒");
                    //设置为当前模块的主题色
                    tv_title.setBackgroundResource(R.color.titlegreen);
                    //显示提示信息
                    tv_message.setText("确定要删除这条黑名单号码吗？");
                    alertdialog.setView(view);
                    final AlertDialog alertDialog = alertdialog.create();
                    alertDialog.show();

                    final Button bt_dialog_cancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
                    Button bt_dialog_commit = (Button) view.findViewById(R.id.bt_dialog_commit);
                    //监听取消按钮
                    bt_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    //监听删除操作
                    bt_dialog_commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //获取当前位置id
                            BlackPerson blackPerson = (BlackPerson) adapter.getItem(position);
                            String id = blackPerson.get_id();
                            //删除数据库中的对应数据
                            smsdao.delete(id);
                            //重新查询删除后的数据
                            List<BlackPerson> query = smsdao.query();
                            //重新设置listview里面的值
                            adapter.setBlackPersons(query);
                            //通知listview数据发生变化，进行刷新操作
                            adapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    });


                }
            });
            return view;
        }
        //对item的view的加载
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            //载入自定义的view
//            View view = getLayoutInflater().inflate(R.layout.sms_safe_lv_item_layout, null);
//            //获取view的所有控件
//            TextView tv_black_name = (TextView) view.findViewById(R.id.tv_black_name);
//            TextView tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
//            TextView tv_sms_type = (TextView) view.findViewById(R.id.tv_sms_type);
//            ImageView imageview = (ImageView) view.findViewById(R.id.iv_sms_delete);
//            //获取当前position要存储的值
//            BlackPerson blackPerson = blackPersons.get(position);
//            tv_black_name.setText("姓名：" + blackPerson.getName());
//            tv_black_number.setText("号码：" + blackPerson.getNumber());
//            //对拦截号码类型进行判断
//            int type = blackPerson.getType();
//            switch (type) {
//                case 1:
//                    tv_sms_type.setText("短信拦截");
//                    break;
//                case 2:
//                    tv_sms_type.setText("电话拦截");
//                    break;
//                case 3:
//                    tv_sms_type.setText("全部拦截");
//                    break;
//                default:
//                    break;
//            }
//            //对imageview点击事件进行处理
//            imageview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //创建一个自定义的dialog
//                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(Sms_Call_Safe_activity.this, R.style.windowFrame_no_title);
//                    //加载dialog要设置的view
//                    View view = getLayoutInflater().inflate(R.layout.sim_changer_showinfo, null);
//                    TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
//                    TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
//                    //对dialog的title进行设置
//                    tv_title.setText("提醒");
//                    //设置为当前模块的主题色
//                    tv_title.setBackgroundResource(R.color.titlegreen);
//                    //显示提示信息
//                    tv_message.setText("确定要删除这条黑名单号码吗？");
//                    alertdialog.setView(view);
//                    final AlertDialog alertDialog = alertdialog.create();
//                    alertDialog.show();
//
//                    final Button bt_dialog_cancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
//                    Button bt_dialog_commit = (Button) view.findViewById(R.id.bt_dialog_commit);
//                    //监听取消按钮
//                    bt_dialog_cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                        }
//                    });
//                    //监听删除操作
//                    bt_dialog_commit.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //获取当前位置id
//                            BlackPerson blackPerson = (BlackPerson) adapter.getItem(position);
//                            String id = blackPerson.get_id();
//                            //删除数据库中的对应数据
//                            smsdao.delete(id);
//                            //重新查询删除后的数据
//                            List<BlackPerson> query = smsdao.query();
//                            //重新设置listview里面的值
//                            adapter.setBlackPersons(query);
//                            //通知listview数据发生变化，进行刷新操作
//                            adapter.notifyDataSetChanged();
//                            alertDialog.dismiss();
//                        }
//                    });
//
//
//                }
//            });
//            return view;
//        }
    }

    /**
     * 创建一个view的实体
     */
    static class ViewHolder{
        TextView tv_black_name;
        TextView tv_black_number;
        TextView tv_sms_type;
        ImageView imageview;

    }

    /**
     * 点击事件的统一处理
     */
    private class MyOnClickListener implements View.OnClickListener {



        private MyOnClickListener() {
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //右上角“添加”点击操作
                case R.id.bt_title_add:
                    alertDialog = new AlertDialog.Builder(Sms_Call_Safe_activity.this, R.style.windowFrame_no_title);
                    alertDialog.setView(dialogView);
                    //这一步很重要，因为dialogView已经在上面加载了，而alertDialog在下面每一次都new一个新的对象
                    //alertDialog.create()就像一个打包器，每一次吧dialogView和alertDialog打包成一个新的dialog
                    //这样的话dialogView的父容器alertDialog每一次都不一样，也就相当于儿子每次都是那个，爸爸却不是那个了
                    //这样处理就相当于，尽管他new过了AlertDialog.Builder，但我还是用以前的那个，避免了上面的问题
                    if (alertdialog == null) {
                        alertdialog = alertDialog.create();
                    }
                    alertdialog.show();
                    break;
                //激活一个隐示意图，用来返回点击条目的对象
                case R.id.iv_sms_call_person:
                    // Toast.makeText(getApplicationContext(), "等待处理", Toast.LENGTH_SHORT).show();
                    Intent contact_intent = new Intent(getApplicationContext(), Contact_info_activity.class);
                    startActivityForResult(contact_intent, CONTACT_REQUESTCODE);
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                    break;
                case R.id.bt_cancel:
                    alertdialog.dismiss();
                    break;
                case R.id.bt_add_black_number:
                    //1.号码不能为空
                    //2.电话/短信拦截必须选一个
                    //3.已经存在的号码不能再添加
                    String number = put_black_number.getText().toString().trim();
                    //号码不能为空
                    if (TextUtils.isEmpty(number)) {
                        Animation animation = AnimationUtils.loadAnimation(Sms_Call_Safe_activity.this, R.anim.shake);
                        animation.setFillAfter(true);
                        bt_add_black_number.startAnimation(animation);
                    } else {
                        //电话/短信拦截必须选一个
                        if (cb_abort_phone.isChecked() || cb_abort_sms.isChecked()) {
                            //区分拦截类型，短信拦截为1.电话拦截为2，短信加电话拦截为3
                            int type = 0;
                            if (cb_abort_sms.isChecked()) {
                                type = type + 1;
                            }
                            if (cb_abort_phone.isChecked()) {
                                type = type + 2;
                            }
                            boolean exit = smsdao.isExit(number);
                            //已经存在的号码不能再添加
                            if (exit) {
                                MyToast.makeshow(getApplicationContext(), "号码已经存在黑名单中", Toast.LENGTH_SHORT);
                            } else {
                                smsdao.add(new BlackPerson("", name, number, type));

                                //重新查询添加后的数据
                                List<BlackPerson> query = smsdao.query();
                                //重新设置listview里面的值
                                adapter.setBlackPersons(query);
                                //通知listview数据发生变化，进行刷新操作
                                adapter.notifyDataSetChanged();

                                alertdialog.dismiss();
                            }

                        } else {
                            //不符合输入规则，用一个补间动画提示用户
                            Animation animation = AnimationUtils.loadAnimation(Sms_Call_Safe_activity.this, R.anim.shake);
                            animation.setFillAfter(true);
                            bt_add_black_number.startAnimation(animation);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_REQUESTCODE && resultCode == Contact_info_activity.CONTACT_RESPONSECODE) {
            Contact_info item = (Contact_info) data.getSerializableExtra("item");
            String number = item.getNumber();
            if (number != null) {
                name = item.getName();
                //获取的结果样式为110 -10已经移除了“-”和空格
                put_black_number.setText(number.replace("-", "").replace(" ", ""));
                put_black_number.setSelection(number.replace("-", "").replace(" ", "").length());
            } else {
                put_black_number.setText("");
            }
        }
    }

    //点击返回键，finish当前activity，激活mian_activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent main_intent = new Intent(Sms_Call_Safe_activity.this, Main_activity.class);
            startActivity(main_intent);
            finish();
            overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

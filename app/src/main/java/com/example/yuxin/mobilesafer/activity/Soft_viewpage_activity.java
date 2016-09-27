package com.example.yuxin.mobilesafer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.Contact_info;
import com.example.yuxin.mobilesafer.receiver.MyAdmin;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.io.Serializable;
import java.util.ArrayList;

import static android.util.Log.i;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/7/22 0022 上午 9:57.
 * Version   1.0;
 * Describe :手机防盗，信息绑定界面
 * History:
 *
*/
public class Soft_viewpage_activity extends Activity {
    public static final int CONTACT_REQUESTCODE =100 ;
    private static final int REQUEST_CODE_ENABLE_ADMIN =500 ;
    ViewPager pager = null;
    PagerTabStrip tabStrip = null;
    //用来存储ViewPager中用到的View
    ArrayList<View> viewContainter = new ArrayList<View>();
    //用来存储View的title
    ArrayList<String> titleContainer = new ArrayList<String>();
    public String TAG = "tag";
    private Button tab2_bt;
    private ImageView tab2_iv;
    private SharedPreferences sp;
    private String simSerialNumber;
    private ImageView tab_iv2;
    private Button tab3_bt;
    private EditText tab3_et;
    private CheckBox tab4_cb_protected;
    private Button tab4_finish;
    private TextView tab4_tv;
    private boolean isprotected;
    private SharedPreferences sp1;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.soft_viewpage);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        simSerialNumber = tm.getSimSerialNumber();



        //获取ViewPager
        pager = (ViewPager) this.findViewById(R.id.viewpager);
        //获取tab
        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(this.getResources().getColor(R.color.pictureblue));
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.viewpagetabline));
        tabStrip.setTextSpacing(200);
//获取viewpager中的四个子view
        View view1 = LayoutInflater.from(this).inflate(R.layout.soft_viewpage_tab1, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.soft_viewpage_tab2, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.soft_viewpage_tab3, null);
        View view4 = LayoutInflater.from(this).inflate(R.layout.soft_viewpage_tab4, null);


        //对于tab2的操作
        //写一个MyClickListener用来处理所有的点击操作
        MyClickListener listener=new MyClickListener();
        //获取ViewPager第二页的控件
        tab2_bt = (Button) view2.findViewById(R.id.tab2_bt);
        tab_iv2 = (ImageView) view2.findViewById(R.id.tab2_iv);
        tab2_iv = (ImageView)  view2.findViewById(R.id.tab2_iv);
        //判断是否SIM卡绑定状态设置两个控件的状态
        if (TextUtils.isEmpty(sp.getString("simSerialNumber",null))){
            //将图标设置成灰色表示没有绑定
            tab2_iv.setImageResource(R.drawable.close_press);
            tab2_bt.setText("点击绑定SIM卡");
        }else {
            //将图标设置成亮色表示绑定成功
            tab2_iv.setImageResource(R.drawable.close);
            tab2_bt.setText("点击解绑SIM卡");

        }
        //给按钮设置监听，使得点击可以 改变绑定SIN卡状态
        tab2_bt.setOnClickListener(listener);



        //对于tab3的操作
        tab3_bt = (Button) view3.findViewById(R.id.tab3_bt);
        tab3_et = (EditText) view3.findViewById(R.id.tab3_et);
        String number = sp.getString("softnumber", null);
        tab3_et.setText(number);
        //避免首先项存储的安全号码为空，出现空指针异常
        if (number!=null){
        tab3_et.setSelection(number.length());
        }
        //给按钮设置监听，使得可以从联系人列表中获取联系人号码
        tab3_bt.setOnClickListener(listener);

        //对于tab4的操作，获取子view里面的控件
        tab4_cb_protected = (CheckBox)view4.findViewById(R.id.tab4_cb_protected);
        tab4_finish = (Button)view4.findViewById(R.id.tab4_bt_finish);
        tab4_tv = (TextView)view4.findViewById(R.id.tab4_tv);
        //获取首先项中存储的tab4_cb_protected状态
        isprotected = sp.getBoolean("isprotected", false);
        //根据tab4_cb_protected的状态修改提示信息
        if (isprotected){
            tab4_cb_protected.setChecked(isprotected);
            tab4_tv.setText("防盗保护已经开启");
        }else{
            tab4_cb_protected.setChecked(isprotected);
            tab4_tv.setText("开启防盗保护");
        }
        //给按钮设置监听
        tab4_finish.setOnClickListener(listener);
        //给tab4_cb_protected设置监听
        tab4_cb_protected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SharedPreferences.Editor edit = sp.edit();
                boolean checked = tab4_cb_protected.isChecked();
                //判断是否按下checkbox
                if (checked){
                    //点击checkbox将提交之前view3中的安全号码
                    String softnumber=tab3_et.getText().toString().trim();
                    i(TAG, "onClick:softnumber" + softnumber);
                    edit.putString("softnumber",softnumber);
                    edit.commit();
                    //获取存储在首先项中的安全号码
                    String simSerialNumber = sp.getString("simSerialNumber", null);
                    String number = sp.getString("softnumber", null);
                    //判断首选项中的SIM卡是否绑定
                    if (TextUtils.isEmpty(simSerialNumber)){
                        //如果没有绑定，toast提示用户还没有设置
                        //并将checkbox设置为没有钩选状态
                        MyToast.makeshow(getApplicationContext(),"您的SIM卡没有绑定",Toast.LENGTH_SHORT);
                        tab4_cb_protected.setChecked(false);
                    }else if (TextUtils.isEmpty(number)){
                        //判断首选项中存储的安全号码是否为空，toast提示用户还没有设置
                        //并将checkbox设置为没有钩选状态
                        MyToast.makeshow( getApplicationContext(),"您的安全号码没有设置",Toast.LENGTH_SHORT);
                        tab4_cb_protected.setChecked(false);
                    }else{
                        //这个隐士意图用来跳转到激活设备管理器页面，
                        // 因为是用startActivityForResult()。所以对于结果的操作在onActivityResult()里
                        ComponentName mDeviceAdminSample=new ComponentName(getApplicationContext(),MyAdmin.class);
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"安全卫士需要获取以下权限用于手机防盗的远程锁屏，响铃，定位等功能");
                        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                        overridePendingTransition(R.anim.next_in,R.anim.next_out);

                    }
                }else{
                    //如果checkbox为未勾选状态则，修改首选项的值
                    edit.putBoolean("isprotected",false);
                    edit.commit();
                    tab4_tv.setText("开启防盗保护");
                }

            }
        });

        //viewpager开始添加view
        viewContainter.add(view1);
        viewContainter.add(view2);
        viewContainter.add(view3);
        viewContainter.add(view4);


        //页签项
        titleContainer.add("欢迎使用手机防盗");
        titleContainer.add("手机卡绑定");
        titleContainer.add("设置安全号码");
        titleContainer.add("向导设置完成");
        MyViewPagerAdapter adapter=new MyViewPagerAdapter();

        pager.setAdapter(adapter);


    }

    /**
     * 创建一个适配器
     */
    private class MyViewPagerAdapter extends  PagerAdapter{
        private View mCurrentView;
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentView = (View)object;
        }
        public View getPrimaryItem() {
            return mCurrentView;
        }
        //viewpager中的组件数量
        @Override
        public int getCount() {
            return viewContainter.size();
        }
        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(viewContainter.get(position));
        }
        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(viewContainter.get(position));
            return viewContainter.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //设置Title的样式
            SpannableStringBuilder ssb = new SpannableStringBuilder("  "+titleContainer.get(position)); // space added before text
            // for
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher);
            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());

            ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.textWrite));// 字体颜色设置为绿色

            ssb.setSpan(fcs, 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置字体颜色
            ssb.setSpan(new RelativeSizeSpan(1.2f), 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssb;
        }

    }

    /**
     * 用来处理这个Activity的所以点击事件
     */
    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.tab2_bt:
                   //TODO做绑定SIM卡的操作
                   onbind();
                   break;
               case R.id.tab3_bt:
                   //获取联系人
                   getcontact();
                   break;
               case R.id.tab4_bt_finish:
                   //判断向导是否设置完成，如果设置完成则跳转至手机防盗中心
                   saft_setting_finish();
                   break;
               default:
                   i(TAG, "onClick" + v.getId());
                   break;
           }
        }
    }

    /**
     *跳转到设置主界面
     */

    private void saft_setting_finish() {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("saft_setting_finish",true);
        edit.commit();
        Intent intent= new Intent(this,Saft_Centre_activity.class);
        startActivity(intent);
        finish();
       overridePendingTransition(R.anim.next_in,R.anim.next_out);

    }

    /**
     * 绑定sim卡
     */
    private void onbind() {
            SharedPreferences.Editor edit = sp.edit();
        if (TextUtils.isEmpty(sp.getString("simSerialNumber",null))){
            edit.putString("simSerialNumber",simSerialNumber);
            edit.commit();
            tab2_iv.setImageResource(R.drawable.close);
            tab2_bt.setText("点击解绑SIM卡");
        }else {
            edit.putString("simSerialNumber",null);
            edit.commit();
            tab2_iv.setImageResource(R.drawable.close_press);
            tab2_bt.setText("点击绑定SIM卡");

        }
    }

    /**
     * 跳转到获取联系人界面
     */
    private void getcontact() {
        Intent contact_intent=new Intent(this,Contact_info_activity.class);
        startActivityForResult(contact_intent,CONTACT_REQUESTCODE);
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

    /**
     * 获取startActivityForResult返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //对tab3中的获取联系人返回数据进行操作
        if (requestCode==CONTACT_REQUESTCODE&&resultCode==Contact_info_activity.CONTACT_RESPONSECODE){
            Contact_info item = (Contact_info)data.getSerializableExtra("item");
            String number = item.getNumber();
            if (number!=null){
            //获取的结果样式为110 -10已经移除了“-”和空格
            tab3_et.setText(number.replace("-","").replace(" ",""));
            tab3_et.setSelection(number.replace("-","").replace(" ","").length());
            }else{
                tab3_et.setText("");
            }
        }
        //对tab4中激活设备管理器的结果进行判断
        if(requestCode==REQUEST_CODE_ENABLE_ADMIN){

            //用户点击激活管理员权限
            if (resultCode==Activity.RESULT_OK){
                //排除前面的状态，存储checkbox的状态。修改提示信息
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isprotected",true);
                edit.commit();
                tab4_cb_protected.setChecked(true);
                tab4_tv.setText("防盗保护已经开启");
            }else{
                //用户没有点击激活按钮
                //并将checkbox设置为没有钩选状态
                MyToast.makeshow( getApplicationContext(),"应用获取权限失败",Toast.LENGTH_SHORT);
                tab4_cb_protected.setChecked(false);
            }
        }

    //TODO
    }

    /**
     * 获取并处理点击后退键事件
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //创建一个自定义的dialog，并设置样式
            AlertDialog.Builder alertdialog=new AlertDialog.Builder(this,R.style.windowFrame_no_title);
            View view = getLayoutInflater().inflate(R.layout.soft_backtomain_dialog, null);
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
            //监听返回主界面按钮
            bt_dialog_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //如果中途退出设置，那么将设置开启防盗选项为false
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean("isprotected",false);
                    edit.commit();
                    ///跳转至主界面，关闭当前activity
                    Intent intent=new Intent(getApplicationContext(),Main_activity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
                }
            });

        }

        return super.onKeyDown(keyCode, event);

    }



}

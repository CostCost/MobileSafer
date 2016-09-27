package com.example.yuxin.mobilesafer.receiver;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.activity.Main_activity;
import com.example.yuxin.mobilesafer.service.AddressService;
import com.example.yuxin.mobilesafer.ui.MyToast;

import java.util.ArrayList;

/**
 * Created by yuxin on 2016/7/23 0023.
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    private static final String TAG = "BootCompleteReceiver";


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG,"我发现你已经开机了!");
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_APPEND);
        boolean isshowaddress = sp.getBoolean("isshowaddress", true);
        if (isshowaddress){
            Intent addressIntent=new Intent(context, AddressService.class);
            context.startService(addressIntent);
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        String oldsn = sp.getString("simSerialNumber", null);
        if (!TextUtils.isEmpty(oldsn)){
            if (oldsn.equals(simSerialNumber)){
                Log.i(TAG,"你手机还在！");
            }else {
                boolean isprotected = sp.getBoolean("isprotected", false);
                if (isprotected){
                // 短信发送的操作
                String deviceid = tm.getDeviceId();//获取智能设备唯一编号
                String te1  = tm.getLine1Number();//获取本机号码
                String imei = tm.getSimSerialNumber();//获得SIM卡的序号
                String imsi = tm.getSubscriberId();//得到用户Id

                Log.i(TAG,"小伙子你手机掉了！");
                final String softnumber = sp.getString("softnumber", null);
                String message="发现将你的手机号设置为安全号码的设备SIM卡已变更注意设备丢失，设备唯一ID(IMEI)"+deviceid
                        +";更换的SIM卡号:"+te1+";更换的SIM卡序号："+imei+";用户ID"+imsi+"" +
                        "\n直接回复本条短信相关代码可对丢失手机进行以下操作" +
                        "\n#*location*#(GPS追踪)" +
                        "\n#*alarm*#(播放报警音乐)" +
                        "\n#*wipedata*#(擦除数据，手机将恢复出厂设置)" +
                        "\n#*lockscreen*#(锁屏，锁屏密码为安全助手密码)" +
                        "\n--这条短信来自simon手机安全卫士";
                SmsManager manager = SmsManager.getDefault();
                ArrayList<String> list = manager.divideMessage(message);  //因为一条短信有字数限制，因此要将长短信拆分
                for(String text:list){
                    manager.sendTextMessage(softnumber, null, text, null, null);
                }
                Log.i(TAG,"短信已经发送！");
             //   MyToast.makeshow(context,"发现SIM已变更，已经本机信息发送至安全号码!请及时与失主联系！手机号:"+softnumber+",如未丢失请进行重新绑定SIM卡操作！",Toast.LENGTH_LONG);
                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(context, R.style.windowFrame_no_title);
                    View view =LayoutInflater.from(context).inflate(R.layout.sim_changer_showinfo, null);
                    TextView tv_title  = (TextView) view.findViewById(R.id.tv_title);
                    TextView tv_message  = (TextView) view.findViewById(R.id.tv_message);
                    Button bt_cancle=(Button)view. findViewById(R.id.bt_dialog_cancel);
                    bt_cancle.setVisibility(View.GONE);
                    tv_title.setText("重要提示");
                    tv_message.setText("发现SIM已变更，已将本机信息发送至安全号码!请及时与失主提前设置好的号码联系！\n手机号:"+softnumber+"\n如未丢失请进行重新绑定SIM卡操作！");
                    alertdialog.setView(view);
                    alertdialog.setCancelable(false);
                    final AlertDialog alertDialog = alertdialog.create();
                    //因为这个dialog在receiver中，而只有一个Activity才能添加一个窗体。 所以要设置dialog类型为系统窗口
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();
                    Button bt_dialog_commit = (Button) view.findViewById(R.id.bt_dialog_commit);
                    bt_dialog_commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + softnumber));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }
                    });
                }
            }
        }

    }
}

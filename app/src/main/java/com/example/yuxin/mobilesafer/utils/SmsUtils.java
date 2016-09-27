package com.example.yuxin.mobilesafer.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.example.yuxin.mobilesafer.domain.SmsInfo;
import com.example.yuxin.mobilesafer.ui.MyToast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;

/**
 * Created by yuxin on 2016/8/15 0015.
 */
public class SmsUtils {

    private static final String TAG = "SmsUtils";
    private static final String SEED = "simon";

    /**
     * 短信备份操作
     * 1.判断是否有sdka，没有无法备份
     * 2.短信的读写权限
     * 3.用ContentResolver获取短信
     * 4.XmlSerializer将短信保存成xml文件
     *
     * @param context
     * @param pb
     * @return 返回备份短信的数目，如果返回-1.那么获取短信失败
     */
    public static int backup(Context context, ProgressDialog pb) {
        int smscount = -1;
        try {
            //判断sd是否挂载
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                i(TAG, "SmsUtils" + "SD卡挂载成功！");
                File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");

                FileOutputStream fos = new FileOutputStream(file);

                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(fos, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "smss");


                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms/");
                Cursor cursor = contentResolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
                smscount = count;
                pb.setMax(count);
                int progress = 0;
                pb.setProgress(progress);
                serializer.attribute(null, "size", String.valueOf(count));
                while (cursor.moveToNext()) {
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");


                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    serializer.text(Crypto.encrypt(SEED, cursor.getString(3)));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");
                    progress++;
                    pb.setProgress(progress);
                    SystemClock.sleep(100);
                }
                cursor.close();

                serializer.endTag(null, "smss");
                serializer.endDocument();


                fos.flush();
                fos.close();
                return smscount;

            } else {
                MyToast.makeshow(context, "当前没有挂载sd卡无法备份短信", Toast.LENGTH_SHORT);
                return smscount;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return smscount;
    }

    /**
     * 解析获取到的xml文件，生成List<Smsinfo>集合
     * @param context
     * @param file
     * @return
     */
    public static List<SmsInfo> RecoverySms(Context context, File file) {
        List<SmsInfo> infos =null;
        SmsInfo smsinfo = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(fis, "utf-8");
            int eventType = pullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        infos = new ArrayList<>();
                    break;
                    case XmlPullParser.START_TAG:
                    if (pullParser.getName().equals("sms")) {
                        smsinfo = new SmsInfo();
                    }  else if (pullParser.getName().equals("address")) {
                        eventType = pullParser.next();
                        smsinfo.setAddress(pullParser.getText());
                    } else if (pullParser.getName().equals("date")) {
                        eventType = pullParser.next();
                        smsinfo.setDate(pullParser.getText());
                    }else if (pullParser.getName().equals("type")) {
                        eventType = pullParser.next();
                        smsinfo.setType(pullParser.getText());
                    }else if (pullParser.getName().equals("body")) {
                        eventType = pullParser.next();
                        smsinfo.setBody(pullParser.getText());
                    }
                    break;
                    case XmlPullParser.END_TAG:
                    if (pullParser.getName().equals("sms")) {
                            infos.add(smsinfo);
                            smsinfo = null;
                    }
                    break;
                    default:
                        break;
                }

                eventType = pullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    /**
     * 传入短信的对象，根据date（时间）为13位具有一定的唯一性，来判断是否是相同的短信
     * @param smsinfo
     * @return
     */
    public static boolean isSmsExist(Context context, SmsInfo smsinfo){
        boolean isExist= false;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        String selection = "date=?";
        String[] selectionArgs = new String[]{smsinfo.getDate()};
        Cursor cursor = contentResolver.query(uri,new String[]{"*"},selection,selectionArgs, null);
        if (cursor.moveToFirst()){
            isExist=true;
        }
        return  isExist;
    }

    /**
     * 将备份的短信插入短信数据库中，需要注意的是5.0以后不能对短信数据库进行操作
     * @param context
     * @param smsinfos
     * @param pb
     * @return
     */
    public static int UpdateSms(Context context,List<SmsInfo> smsinfos,ProgressDialog pb){
        int recount=-1;
        try {
        pb.setMax(smsinfos.size());
        int progress = 0;
        pb.setProgress(progress);
        Uri uri = Uri.parse("content://sms/");
        if (smsinfos!=null){
            recount++;
        }
        for(SmsInfo smsInfo:smsinfos){
            ContentValues values = new ContentValues();
            values.put("address", smsInfo.getAddress());
            values.put("date", smsInfo.getDate());
            values.put("type", smsInfo.getType());
            values.put("body",Crypto.decrypt(SEED,smsInfo.getBody()));
            Uri insert = context.getContentResolver().insert(uri, values);
            i(TAG, "UpdateSms" + "insert:"+insert);
            recount++;
            SystemClock.sleep(200);
             progress++;
            pb.setProgress(progress);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recount;
    }


}

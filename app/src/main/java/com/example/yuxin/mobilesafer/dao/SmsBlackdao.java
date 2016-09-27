package com.example.yuxin.mobilesafer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.yuxin.mobilesafer.db.SmsBlackNameSqliteOpenHelper;
import com.example.yuxin.mobilesafer.domain.BlackPerson;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;

/**
 * black.db数据库dao
 * 1.增加
 * 2.删除
 * 3.遍历
 * 4.查看是否已经存在
 * Created by yuxin on 2016/8/1 0001.
 */
public class SmsBlackdao {
    private static final String TAG = "SmsBlackdao";
    public SmsBlackNameSqliteOpenHelper helper;

    //在无参构造中获取数据库操作对象
    public SmsBlackdao(Context context) {
        helper = SmsBlackNameSqliteOpenHelper.getInstance(context);
    }

    /**
     * 对数据库进行添加操作
     *
     * @param blackPerson
     * @return
     */
    public void add(BlackPerson blackPerson) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valus = new ContentValues();
        valus.put("name", blackPerson.getName());
        valus.put("number", blackPerson.getNumber());
        valus.put("type", blackPerson.getType());
        db.insert("blackname", null, valus);

    }

    /**
     * 对数据库的删除操作
     *
     * @param _id 要删除记录的id
     * @return
     */
    public void delete(String _id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{_id};
        int deleteresult = db.delete("blackname", whereClause, whereArgs);

    }

    /***
     * 对blackname进行遍历操作
     *
     * @return List<BlackPerson>
     */
    public List<BlackPerson> query() {
        List<BlackPerson> blackPersons = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("blackname", new String[]{"*"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            int type = cursor.getInt(3);
            blackPersons.add(new BlackPerson(_id, name, phone, type));
        }
        cursor.close();
        db.close();
        return blackPersons;
    }


    /**
     * 对数据库进行查询
     *
     * @param number 要查重的号码
     * @return true 已经存在 false 不存在
     */
    public boolean isExit(String number) {
        boolean isExit = false;
        i(TAG, "isExit" + number);
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = "number=?";
        String[] selectionArgs = new String[]{number};
        Cursor cursor = db.query("blackname", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            isExit = true;
        }
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     *判断号码是否是短信拦截状态
     * @param number 要查的号码
     * @return true 已经存在 false 不需要拦截
     */
    public boolean isAbortSms(String number) {
        boolean isAbortSms = false;
        i(TAG, "isAbortSms" + number);
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = "number=?";
        String[] selectionArgs = new String[]{number};
        Cursor cursor = db.query("blackname", new String[]{"type"}, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(0);
            //区分拦截类型，短信拦截为1.电话拦截为2，短信加电话拦截为3
            switch (type) {
                case 1:
                    isAbortSms = true;
                    break;
                case 2:
                    isAbortSms = false;
                    break;
                case 3:
                    isAbortSms = true;
                    break;
                default:
                    break;
            }
        }
        cursor.close();
        db.close();
        return isAbortSms;
    }
    /**
     *判断号码是否是电话拦截状态
     * @param number 要查的号码
     * @return true 已经存在 false 不需要拦截
     */
    public boolean isAbortPhone(String number) {
        boolean isAbortPhone = false;
        i(TAG, "isAbortPhone" + number);
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = "number=?";
        String[] selectionArgs = new String[]{number};
        Cursor cursor = db.query("blackname", new String[]{"type"}, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(0);
            //区分拦截类型，短信拦截为1.电话拦截为2，短信加电话拦截为3
            switch (type) {
                case 1:
                    isAbortPhone = false;
                    break;
                case 2:
                    isAbortPhone = true;
                    break;
                case 3:
                    isAbortPhone = true;
                    break;
                default:
                    break;
            }
        }
        cursor.close();
        db.close();
        return isAbortPhone;
    }




}

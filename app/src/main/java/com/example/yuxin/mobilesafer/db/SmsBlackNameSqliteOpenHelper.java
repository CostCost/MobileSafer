package com.example.yuxin.mobilesafer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Created by yuxin on 2016/8/1 0001.
 */
public class SmsBlackNameSqliteOpenHelper extends SQLiteOpenHelper {
    private static SmsBlackNameSqliteOpenHelper mInstance;

    //单例获取dbopenhelp对象
    public synchronized static  SmsBlackNameSqliteOpenHelper getInstance(Context context){
        if (mInstance==null){
           mInstance =new SmsBlackNameSqliteOpenHelper(context,"black.db",null,1);

        }
        return mInstance;
    }

    public SmsBlackNameSqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table blackname(_id integer primary key autoincrement," +
                "name text," +
                "number text," +
                "type integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

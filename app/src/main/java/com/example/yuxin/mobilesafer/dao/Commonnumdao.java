package com.example.yuxin.mobilesafer.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * address数据库的访问操作
 * Created by yuxin on 2016/7/28 0028.
 */
public class Commonnumdao {


    /**
     * 判断/data/data/files/路径下是否存在commonnum.db文件
     *
     * @param context
     * @return file.exists()
     */
    public static boolean isExist(Context context) {
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "commonnum.db");
        return file.exists();
    }

    /**
     * 将assert目录下的数据库复制到/data/data/files/目录下
     *
     * @param context
     */
    public static void copyFileToFiles(Context context) {
        try {
            //获取资源管理器
            AssetManager assets = context.getAssets();
            //获取资源目录下的数据库文件的输入流
            InputStream inputStream = assets.open("commonnum.db");
            //输出流
            FileOutputStream fos = context.openFileOutput("commonnum.db", Context.MODE_PRIVATE);
            //边读边写
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取GroupData
     *
     * @param context
     * @return
     */
    public static List<Map<String, String>> getGroupData(Context context) {
        //创建一个存储map的List集合
        List<Map<String, String>> groupData = new ArrayList<>();
        //获取数据库文件
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "commonnum.db");
        //以只读的方式打开数据库文件
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        //判断是否打开文件
        if (db.isOpen()) {
            //查询"idx", "name"两个字段，将单个结果保存在map中，将map保存在List集合中
            Cursor cursor = db.query("classlist", new String[]{"idx", "name"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String idx = cursor.getString(0);
                String name = cursor.getString(1);
                Map<String, String> map = new HashMap<>();
                map.put("idx", idx);
                map.put("name", name);
                groupData.add(map);
            }
            cursor.close();
            db.close();
        }
        return groupData;
    }

    /**
     * 获取ChildData数据
     * @param context
     * @return
     */
    public static List<List<Map<String, String>>> getChildData(Context context) {
        //创建一个存储了map的List集合的List集合，类似于二维数组存储map对象，哭瞎
        List<List<Map<String, String>>> childData = new ArrayList<>();
        //获取数据库文件
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "commonnum.db");
        //打开数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        //获取到groupData
        List<Map<String, String>> groupData = getGroupData(context);
        //遍历groupData根据groupData的长度来查询表
        for (int i = 0; i < groupData.size(); i++) {
            //创建一个存储map的List集合
            List<Map<String, String>> list = new ArrayList<>();
            //获取groupData指定位置的map对象
            Map<String, String> map = groupData.get(i);
            //根据获取了的map对象存储的idx来判断查询哪张表
            String idx = map.get("idx");
            if (db.isOpen()) {
                Cursor cursor = db.query("table" + idx, new String[]{"number", "name"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    //将查询到的m信息存储到map中
                    Map<String, String> m = new HashMap<>();
                    String number = cursor.getString(0);
                    String name = cursor.getString(1);
                    m.put("number", number);
                    m.put("name", name);
                    list.add(m);
                }
                cursor.close();
            }
            childData.add(list);
        }
        return childData;
    }


}

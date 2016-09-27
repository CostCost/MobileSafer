package com.example.yuxin.mobilesafer.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yuxin.mobilesafer.domain.AntivirusInfo;

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
public class Antivirusdao {


    /**
     * 判断/data/data/files/路径下是否存在antivirus.db文件
     *
     * @param context
     * @return file.exists()
     */
    public static boolean isExist(Context context) {
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "antivirus.db");
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
            InputStream inputStream = assets.open("antivirus.db");
            //输出流
            FileOutputStream fos = context.openFileOutput("antivirus.db", Context.MODE_PRIVATE);
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

    public  static  List<AntivirusInfo>  getAntivirus(Context context){
        List<AntivirusInfo> antivirusinfos=new ArrayList<>();
        File filesDir = context.getFilesDir();
        File file=new File(filesDir,"antivirus.db");
        SQLiteDatabase db=SQLiteDatabase.openDatabase(file.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.query("datable", new String[]{"md5", "name"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                AntivirusInfo info = new AntivirusInfo();
                String md5 = cursor.getString(0);
                String name = cursor.getString(1);
                info.setMd5(md5);
                info.setName(name);
                antivirusinfos.add(info);
            }
            cursor.close();
            db.close();
        }
        return antivirusinfos;
    }


}

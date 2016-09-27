package com.example.yuxin.mobilesafer.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * address数据库的访问操作
 * Created by yuxin on 2016/7/28 0028.
 */
public class Addressdao {
    private static final String TAG ="Addressdao'";

    /**
     * 判断/data/data/files/路径下是否存在addre.db文件
     * @param context
     * @return file.exists()
     */
    public static  boolean isExist(Context context){
        File filesDir = context.getFilesDir();
        File file =new File(filesDir,"address.db");
        return file.exists();
    }

    /**
     * 将assert目录下的数据库复制到/data/data/files/目录下
     * @param context
     */
    public static  void copyFileToFiles(Context context){
        try {
            //获取资源管理器
        AssetManager assets = context.getAssets();
            //获取资源目录下的数据库文件的输入流
            InputStream inputStream = assets.open("address.db");
            //输出流
            FileOutputStream fos = context.openFileOutput("address.db", Context.MODE_PRIVATE);
            byte[] buffer=new byte[1024];
            int len=0;
            while ((len=inputStream.read(buffer))!=-1) {
                fos.write(buffer,0,len);
            }
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询电话号码操作方法，
     * @param context
     * @param number 电话号码
     * @return 电话号码归属地
     */
    public static String getAddress(Context context,String number){
        //检查数据库文件是否复制到/data/data/目录下，没有的话执行复制操作
        if (!Addressdao.isExist(context)){
            Addressdao.copyFileToFiles(context);
        }
       //正则表达式过滤号码
        String regularExpression="^1[34578][0-9]{9}$";
        //加载数据库文件
        File file= new File(context.getFilesDir(), "address.db");
        String address=null;
        //获得数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        //长度十一位，过滤手机号
        if (number.matches(regularExpression)){
            String substring = number.substring(0, 7);
           //查询数据库的
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{substring});
            if (cursor.moveToFirst()){
               address = cursor.getString(0);

            }
            cursor.close();

        }else{
         //本地号码。七位或八位
            if (number.length()==7||number.length()==8){
                address="本地号码";
            }else  if(number.length()==11){
                //电话号码3+8|4+7
                //数据库做了精简，将区号第一位0去除
                String substring = number.substring(1, 3);
                Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{substring});
                if (cursor.moveToFirst()){
                    address = cursor.getString(0);

                }
                cursor.close();
                //电话4+7
                if (address==null){
                    String substring2 = number.substring(1, 4);
                    Cursor cursor2 = db.rawQuery("select location from data2 where area=?", new String[]{substring2});
                    if (cursor2.moveToFirst()){
                        address = cursor2.getString(0);

                    }
                    cursor2.close();
                }
                    //电话4+8
            }else  if(number.length()==12){
                String substring = number.substring(1, 4);
                Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{substring});
                if (cursor.moveToFirst()){
                    address = cursor.getString(0);

                }
                cursor.close();

            }else if(number.length()==5){
                address="公共号码";

            } else if(number.length()==4){
                address="模拟器";

            }else if (number.length()==3){
                address="紧急号码";
            }
        }
        db.close();
        if (address==null){
            address="未知号码";
        }
        return  address;

    }

    /**
     *电话号码的动态查询
     * @param context
     * @param number 电话号码
     * @return 返回查询结果
     */
      public static String getDynamicResult(Context context ,String number){

          //加载数据库文件
          File file= new File(context.getFilesDir(), "address.db");
          String address=null;
          //获得数据库对象
          SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
          //如果小于三位无法获知
          if (number.length()<3){
              address="未知";
              //如果大于3小于7
          }else  if(number.length()<7){
                //先查询前三位区号的电话
              String substring = number.substring(1, 3);
              Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{substring});
              if (cursor.moveToFirst()){
                  address = cursor.getString(0);

              }
              cursor.close();
              //如果结果为空
              if (address==null){
                  //当长度大于4时，查询区号为四位的电话
                  if (number.length()>4){
                  String substring2 = number.substring(1, 4);
                  Cursor cursor2 = db.rawQuery("select location from data2 where area=?", new String[]{substring2});
                  if (cursor2.moveToFirst()){
                      address = cursor2.getString(0);

                  }
                  cursor2.close();

                  }
              }
          }else{
           //   当长度大于等于7查询手机号
              String substring = number.substring(0, 7);
              //查询数据库的
              Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{substring});
              if (cursor.moveToFirst()){
                  address = cursor.getString(0);

              }
              cursor.close();
              //手机号没有结果再去查询电话号码
            if (address==null){
                String substring2 = number.substring(1, 3);
                Cursor cursor2 = db.rawQuery("select location from data2 where area=?", new String[]{substring2});
                if (cursor2.moveToFirst()){
                    address = cursor2.getString(0);

                }
                cursor2.close();
                //电话4+7
                if (address==null){

                        String substring3 = number.substring(1, 4);
                        Cursor cursor3 = db.rawQuery("select location from data2 where area=?", new String[]{substring3});
                        if (cursor3.moveToFirst()){
                            address = cursor3.getString(0);

                        }
                        cursor3.close();
                }
            }

          }

          if (address==null){
              address="未知号码";
          }

          return address;
      }

}

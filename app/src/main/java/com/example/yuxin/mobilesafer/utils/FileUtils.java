package com.example.yuxin.mobilesafer.utils;

/**
 * Created by yuxin on 2016/7/19 0019.
 */
public class FileUtils {

    public static String getFileName(String path){
        return path.substring(path.lastIndexOf("/")+1);
    }
}

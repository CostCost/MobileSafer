package com.example.yuxin.mobilesafer;

import com.example.yuxin.mobilesafer.engine.Contact_info_Engine;

/**
 * Created by yuxin on 2016/7/24 0024.
 */
public class MyTest extends ApplicationTest {
     public void test(){
         Contact_info_Engine.getContactInfi(getContext().getContentResolver());
     }
}

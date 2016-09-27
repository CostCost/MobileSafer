package com.example.yuxin.mobilesafer.engine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.yuxin.mobilesafer.domain.Contact_info;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;

/**
 * Created by yuxin on 2016/7/23 0023.
 */
public class Contact_info_Engine {

    private static final String TAG = "Contact_info_Engine";

    /**
     * 用来获取手机存储了的联系人的姓名和号码
     *
     * @param cr 传入一个内容观察者
     * @return List<Contact_info> 存放了获取到的所有联系人对象
     */
    public static List<Contact_info> getContactInfi(ContentResolver cr) {
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts", RAW_CONTACTS);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#", RAW_CONTACTS_ID);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#/data", RAW_CONTACTS_ID_DATA);

        List<Contact_info> contacts = new ArrayList<>();

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Cursor c = cr.query(uri, new String[]{"_id"}, null, null, null);
        while (c.moveToNext()) {
            int _id = c.getInt(0);
            Contact_info info = new Contact_info();
            uri = Uri.parse("content://com.android.contacts/raw_contacts/" + _id + "/data");
            Cursor cursor = cr.query(uri, new String[]{"data1", "mimetype"}, null, null, null);
            while (cursor.moveToNext()) {
                String data1 = cursor.getString(0);
                String mimetype = cursor.getString(1);
                if ("vnd.android.cursor.item/name".equals(mimetype)) {
                    info.setName(data1);
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    info.setNumber(data1);
                }


            }
            cursor.close();
            if (!TextUtils.isEmpty(info.getNumber())) {
                contacts.add(info);
            }
        }
        c.close();
        return contacts;
    }

    /**
     * 分页查询联系人
     *
     * @param cr
     * @param startid 开始的id
     * @param block   分页的大小
     * @return
     */
    public static List<Contact_info> getContactInfiByLimit(ContentResolver cr, int startid, int block, ProgressBar contact_pb) {
        int flag=0;
        List<Contact_info> infos=new ArrayList<Contact_info>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = cr.query(uri, projection, null, null, "_id  limit " + startid + "," + block);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (contact_pb!=null){
                    contact_pb.setProgress(flag);
                    flag++;
                }
                Contact_info info=new Contact_info();
                String contactName = cursor.getString(0);
                String phoneNumber = cursor.getString(1);
                info.setName(contactName);
                info.setNumber(phoneNumber);
                if (!TextUtils.isEmpty(info.getNumber())){
                infos.add(info);
                }
                info=null;
            }
            cursor.close();
        }
        return infos;
    }

    /**
     *
     * 获取联系人总个数
     * @param cr
     * @return
     */
    public static int getAllContactCounts(ContentResolver cr) {
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts", RAW_CONTACTS);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#", RAW_CONTACTS_ID);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#/data", RAW_CONTACTS_ID_DATA);

        int connt = 0;

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        //sort_key COLLATE LOCALIZED asc limit " + block + " offset " + startid  从startid+1开始查询block个
        Cursor c = cr.query(uri, new String[]{"_id"}, null, null, null);
        while (c.moveToNext()) {
            int _id = c.getInt(0);

            uri = Uri.parse("content://com.android.contacts/raw_contacts/" + _id + "/data");
            Cursor cursor = cr.query(uri, new String[]{"data1", "mimetype"}, null, null, null);
            while (cursor.moveToNext()) {
                String data1 = cursor.getString(0);
                String mimetype = cursor.getString(1);
                if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    if (!TextUtils.isEmpty(data1)){
                        connt++;
                    }
                }
            }
            cursor.close();
        }
        c.close();
        return connt;
    }

    /**
     * 查询是否是联系人
     * @param cr
     * @return
     */
    public static boolean queryContact(ContentResolver cr,String number) {
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts", RAW_CONTACTS);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#", RAW_CONTACTS_ID);
//        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#/data", RAW_CONTACTS_ID_DATA);

        boolean iscontact=false;

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        //sort_key COLLATE LOCALIZED asc limit " + block + " offset " + startid  从startid+1开始查询block个
        Cursor c = cr.query(uri, new String[]{"_id"}, null, null, null);
        while (c.moveToNext()) {
            int _id = c.getInt(0);

            uri = Uri.parse("content://com.android.contacts/raw_contacts/" + _id + "/data");
            Cursor cursor = cr.query(uri, new String[]{"data1", "mimetype"}, null, null, null);
            while (cursor.moveToNext()) {
                String data1 = cursor.getString(0);
                String mimetype = cursor.getString(1);
                if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    if (!TextUtils.isEmpty(data1)&&number.equals(data1)){
                        iscontact=true;
                    }
                }
            }
            cursor.close();
        }
        c.close();
        return iscontact;
    }



}

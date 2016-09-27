package com.example.yuxin.mobilesafer.domain;

/**
 * =============================================================================
 * Copyright (c) 2016 ${ORGANIZATION_NAME}. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.domain
 * Created by yuxin.
 * Created time 2016/8/16 0016 上午 8:55.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class SmsInfo {
    private String address;
    private String date;
    private String type;
    private String body;

    public SmsInfo() {
    }

    public SmsInfo( String address, String body, String date, String type) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SmsInfo{" +
                "address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

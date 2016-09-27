package com.example.yuxin.mobilesafer.domain;

import java.io.Serializable;

/**
 * Created by yuxin on 2016/7/23 0023.
 */
public class Contact_info implements Serializable {
    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Contact_info{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}

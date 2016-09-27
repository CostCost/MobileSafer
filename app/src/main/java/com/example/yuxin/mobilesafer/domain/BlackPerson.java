package com.example.yuxin.mobilesafer.domain;

/**
 * Created by yuxin on 2016/8/1 0001.
 */
public class BlackPerson {
    private String _id;
    private String name;
    private String number;
    private int type;

    public BlackPerson() {

    }


    public BlackPerson(String _id, String name, String number, int type) {
        this._id = _id;
        this.name = name;
        this.number = number;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "BlackPerson{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", type=" + type +
                '}';
    }
}

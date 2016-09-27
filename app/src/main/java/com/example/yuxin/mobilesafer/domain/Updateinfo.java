package com.example.yuxin.mobilesafer.domain;

/**
 * Created by yuxin on 2016/7/19 0019.
 */
public class Updateinfo {

    private String version;
    private String url;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Updateinfo{" +
                "description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

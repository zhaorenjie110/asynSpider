package com.hirebigdata.threadspider.pojo;

/**
 * Created by Administrator on 2015/1/20.
 */
public class Resource {
    int flag = 0;
    String data = "";
    String url_name = "";
    String user_data_id = "";
    Object obj = new Object();

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Resource(int flag, String data, String user_data_id, String url_name, Object obj) {
        this.flag = flag;
        this.data = data;
        this.user_data_id = user_data_id;
        this.url_name = url_name;
        this.obj = obj;
    }

    public Resource(int flag, String data, String user_data_id, String url_name) {
        this.flag = flag;
        this.data = data;
        this.user_data_id = user_data_id;
        this.url_name = url_name;
    }

    public Resource(int flag, String data, String user_data_id) {
        this.flag = flag;
        this.data = data;
        this.user_data_id = user_data_id;
    }

    public String getUrl_name() {
        return url_name;
    }

    public void setUrl_name(String url_name) {
        this.url_name = url_name;
    }

    public String getUser_data_id() {
        return user_data_id;
    }

    public void setUser_data_id(String user_data_id) {
        this.user_data_id = user_data_id;
    }

    public Resource() {
    }

    public Resource(int flag, String data) {
        this.flag = flag;
        this.data = data;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

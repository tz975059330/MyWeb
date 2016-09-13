package com.king.myweb;

import java.io.Serializable;

/**
 * Created by 16230 on 2016/9/7.
 */

public class Data implements Serializable{

    private String account;
    private String passwd;
    private String Cookie;
    private String name;

    public Data(String account, String passwd, String cookie, String name) {
        this.account = account;
        this.passwd = passwd;
        Cookie = cookie;
        this.name = name;
    }

    public String getCookie() {
        return Cookie;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }


}

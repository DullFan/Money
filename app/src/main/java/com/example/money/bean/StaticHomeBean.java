package com.example.money.bean;

public class StaticHomeBean {
    public static boolean flag = true;

    public static boolean isFlag() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        StaticHomeBean.flag = flag;
    }
}

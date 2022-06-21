package com.example.money.bean;

import java.util.List;

public class HomeBean {
    //日期
    private String date;
    //存放记录
    private List<HomeItemBean>mHomeItemBeans;

    public HomeBean(String date, List<HomeItemBean> homeItemBeans) {
        this.date = date;
        mHomeItemBeans = homeItemBeans;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<HomeItemBean> getHomeItemBeans() {
        return mHomeItemBeans;
    }

    public void setHomeItemBeans(List<HomeItemBean> homeItemBeans) {
        mHomeItemBeans = homeItemBeans;
    }
}

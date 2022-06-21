package com.example.money.bean;

public class MoneyPlusViewPagerBean {
    //名字
    private String name;
    //图片
    private int img;

    public MoneyPlusViewPagerBean(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}

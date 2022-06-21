package com.example.money.bean;

public class MoneyBean {
    private String data;
    private double money;

    public MoneyBean(String data, double money) {
        this.data = data;
        this.money = money;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}

package com.example.money.bean;

public class HomeItemBean {
    //id
    private int id;
    //金额
    private double money;
    //描述
    private String describe;
    //备注
    private String remarks;
    //支出还是收入.true为支出,false为收入
    private boolean isFlag;
    //是否退款.true为退款,false为未退款
    private boolean isRefund;
    //退款备注
    private String refundRemarks;
    //退款时间
    private String refundTime;
    //退款金额
    private String refundMoney;
    //消费时间
    private  String time;
    //记录时间
    private String RecordTime;

    public HomeItemBean(int id, double money, String describe, String remarks, boolean isFlag, boolean isRefund, String refundRemarks, String refundTime, String refundMoney, String time, String recordTime) {
        this.id = id;
        this.money = money;
        this.describe = describe;
        this.remarks = remarks;
        this.isFlag = isFlag;
        this.isRefund = isRefund;
        this.refundRemarks = refundRemarks;
        this.refundTime = refundTime;
        this.refundMoney = refundMoney;
        this.time = time;
        RecordTime = recordTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean refund) {
        isRefund = refund;
    }

    public String getRefundRemarks() {
        return refundRemarks;
    }

    public void setRefundRemarks(String refundRemarks) {
        this.refundRemarks = refundRemarks;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(String refundMoney) {
        this.refundMoney = refundMoney;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String recordTime) {
        RecordTime = recordTime;
    }
}

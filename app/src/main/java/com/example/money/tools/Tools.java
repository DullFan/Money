package com.example.money.tools;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {
    public static boolean isFlagNow = true;
    public static String getWeek(String pTime) {
        String week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            week += "星期六";
        }
        return week;
    }

    public static void SnackbarShow(View view, String content){
        Snackbar.make(view,content,Snackbar.LENGTH_LONG).show();
    }

    public static int getMonthOfDay(int year,int month){
        int day = 0;
        if(year%4==0&&year%100!=0||year%400==0){
            day = 29;
        }else{
            day = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
        }
        return 0;
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static Date stringToDate3(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static Date stringToDate2(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }
}

package com.example.money.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.money.R;

public class Loading extends ProgressDialog {
    public Loading(Context context) {
        super(context);
    }

    public Loading(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        setCancelable(true);
        //弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        setCanceledOnTouchOutside(false);
        //加载布局
        setContentView(R.layout.loading_layout);
        //设置对话框的宽高
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {//开启
        super.show();
    }

    @Override
    public void dismiss() {//关闭
        super.dismiss();
    }
}

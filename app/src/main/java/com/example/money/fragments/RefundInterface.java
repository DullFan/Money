package com.example.money.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.money.MainActivity;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.PostBean;
import com.example.money.databinding.FragmentRefundInterfaceBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.LiveDataBus;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RefundInterface extends Fragment {
    FragmentRefundInterfaceBinding mBinding;
    private HomeItemBean mSelHomeItemBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRefundInterfaceBinding.inflate(inflater, container, false);
        mSelHomeItemBean = LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).getValue();
        initBack();
        initEditText();
        return mBinding.getRoot();
    }

    private void initEditText() {
        mBinding.reundDescribe.setText(mSelHomeItemBean.getDescribe());
        mBinding.reundMoney.setText("-" + mSelHomeItemBean.getMoney());


        if (mSelHomeItemBean.getRemarks().equals("")) {
            mBinding.reundRemarks.setVisibility(View.GONE);
        } else {
            mBinding.reundRemarks.setVisibility(View.VISIBLE);
            mBinding.reundRemarks.setText(mSelHomeItemBean.getRemarks());
        }


        if (mSelHomeItemBean.isRefund()) {
            mBinding.reundRefund.setVisibility(View.VISIBLE);
            mBinding.reundRefund.setText("(已退款" + mSelHomeItemBean.getRefundMoney() + ")");
        } else {
            mBinding.reundRefund.setVisibility(View.GONE);
        }

        mBinding.refundMoney.setText(mSelHomeItemBean.getMoney() + "");
        if(mBinding.refundMoney.getText().toString().contains(".")){
            mBinding.refundMoney.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        }else{
            mBinding.refundMoney.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        }
        mBinding.refundMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    mBinding.refundMoney.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
                    int index = s.toString().indexOf(".");
                    String substring = s.toString().substring(index + 1, s.toString().length());
                    if (substring.length() > 2) {
                        String substring1 = s.toString().substring(0, index + 3);
                        mBinding.refundMoney.setText(substring1);
                        mBinding.refundMoney.setSelection(mBinding.refundMoney.getText().toString().length());
                    }
                }else{
                    mBinding.refundMoney.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.reundRemarks.setText(mSelHomeItemBean.getRemarks());
        //日期选择
        mBinding.refundTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                //获取年月日
                int year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                int day=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd=new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mBinding.refundTime.setText(year+"-"+(month+1)+"-"+day);
                    }
                },year,month,day);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                dpd.show();
            }
        });

        mBinding.reundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil httpUtil = new HttpUtil();
                String url = "/consumption/refund";
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        PostBean postBean = (PostBean) msg.obj;
                        if(postBean.getCode().equals("200")){
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }
                };
                Gson gson = new Gson();
                Map<String,Object>map = new HashMap<>();
                map.put("conDate",mSelHomeItemBean.getRecordTime());
                map.put("conRefund","1");
                if(Double.valueOf(mBinding.refundMoney.getText().toString())>20000){
                    Toast.makeText(getContext(), "暂不支持记录20k以上的数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put("conRefundmoney",mBinding.refundMoney.getText().toString());
                map.put("conRefundremarks",mBinding.reundTextinput.getText().toString());
                map.put("conRefundtime",mBinding.refundTime.getText().toString());
                String s = gson.toJson(map);
                httpUtil.okhttpPut(url,s,handler, PostBean.class);
            }
        });
    }

    private void initBack() {
        mBinding.reundBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigateUp();
            }
        });
    }
}
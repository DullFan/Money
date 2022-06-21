package com.example.money.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.bean.PostBean;
import com.example.money.databinding.FragmentForgetPasswordBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.Tools;
import com.example.money.ui.Loading;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends Fragment {
    FragmentForgetPasswordBinding mBinding;
    HttpUtil mHttpUtil;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForgetPasswordBinding.inflate(inflater,container,false);
        mHttpUtil = new HttpUtil();
        initBack();
        initForget();
        initLayout();
        return mBinding.getRoot();
    }

    private void initLayout() {
        //开启字数统计
        mBinding.forgetLayout2.setCounterEnabled(true);
        mBinding.forgetLayout3.setCounterEnabled(true);
        //最大字数
        mBinding.forgetLayout2.setCounterMaxLength(8);
        mBinding.forgetLayout3.setCounterMaxLength(10);
    }

    private void initForget() {
        mBinding.forgetUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 3 || s.toString().length() > 8) {
                    mBinding.forgetUsername.setError("用户名长度为3-8位");
                } else {
                    mBinding.forgetUsername.setError(null);//没有错误清空
                }
            }
        });
        mBinding.forgetPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 10
                        || s.toString().length() < 6) {
                    mBinding.forgetPwd.setError("密码长度为6-10位");//错误的提示信息
                } else {
                    mBinding.forgetPwd.setError(null);//没有错误清空
                }
            }
        });
        mBinding.forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mBinding.forgetUsername.getText().toString();
                String pwd = mBinding.forgetPwd.getText().toString();
                if (username.isEmpty() || pwd.isEmpty()) {
                    Tools.SnackbarShow(mBinding.getRoot(), "请输入信息");
                    return;
                }
                CharSequence error = mBinding.forgetUsername.getError();
                CharSequence error2 = mBinding.forgetPwd.getError();
                if (error != null || error2 != null) {
                    return;
                }
                Loading loading = new Loading(getContext(),R.style.CustomDialog);
                loading.show();
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        PostBean postBean = (PostBean) msg.obj;
                        if (postBean.getCode().equals("500")) {
                            loading.dismiss();
                            Tools.SnackbarShow(mBinding.getRoot(), postBean.getMsg());
                            return;
                        }else {
                            loading.dismiss();
                            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }
                };
                String url = "/user/setPwd";
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                map.put("userUsername", username);
                map.put("userPassword", pwd);
                String s = gson.toJson(map);
                mHttpUtil.okhttpPut(url, s, handler, PostBean.class);
            }
        });
    }

    private void initBack() {
        mBinding.forgetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigateUp();
            }
        });
    }
}
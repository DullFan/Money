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
import com.example.money.databinding.FragmentRegisterBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.Tools;
import com.example.money.ui.Loading;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment {
    FragmentRegisterBinding mBinding;
    HttpUtil mHttpUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        mHttpUtil = new HttpUtil();
        initBack();
        initRegister();
        return mBinding.getRoot();
    }



    private void initRegister() {
        mBinding.registerUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 3 || s.toString().length() > 8) {
                    mBinding.registerUsername.setError("用户名长度为3-8位");
                } else {
                    mBinding.registerUsername.setError(null);//没有错误清空
                }
            }
        });
        mBinding.registerPwd.addTextChangedListener(new TextWatcher() {
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
                    mBinding.registerPwd.setError("密码长度为6-10位");//错误的提示信息
                } else {
                    mBinding.registerPwd.setError(null);//没有错误清空
                }
            }
        });
        mBinding.registerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 2
                        || s.toString().length() > 6) {
                    mBinding.registerName.setError("名称长度为2-6位");//错误的提示信息
                } else {
                    mBinding.registerName.setError(null);//没有错误清空
                }
            }
        });
        mBinding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mBinding.registerUsername.getText().toString();
                String pwd = mBinding.registerPwd.getText().toString();
                String name = mBinding.registerName.getText().toString();
                if (username.isEmpty() || pwd.isEmpty() || name.isEmpty()) {
                    Tools.SnackbarShow(mBinding.getRoot(), "请输入信息");
                    return;
                }
                CharSequence error = mBinding.registerUsername.getError();
                CharSequence error2 = mBinding.registerPwd.getError();
                CharSequence error3 = mBinding.registerName.getError();
                if (error != null || error2 != null || error3 != null) {
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
                            Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }
                };
                String url = "/user/register";
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                map.put("userName", username);
                map.put("userPassword", pwd);
                map.put("name", name);
                String s = gson.toJson(map);
                mHttpUtil.httpPost(url, s, handler, PostBean.class);
            }
        });
    }

    private void initBack() {
        mBinding.registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigateUp();
            }
        });
    }
}
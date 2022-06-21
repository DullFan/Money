package com.example.money.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.money.R;
import com.example.money.bean.SignlnBean;
import com.example.money.databinding.FragmentSignInBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.Tools;
import com.example.money.ui.Loading;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends Fragment {
    FragmentSignInBinding mBinding;
    HttpUtil mHttpUtil;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSignInBinding.inflate(inflater,container,false);
        mHttpUtil = new HttpUtil();
        initBack();
        initSignin();
        return mBinding.getRoot();
    }

    private void initSignin() {
        String url = "/user/login";
        mBinding.signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mBinding.signinUsername.getText().toString();
                String password = mBinding.signinPwd.getText().toString();
                if(user.isEmpty()){
                    Tools.SnackbarShow(mBinding.getRoot(),"请输入用户名");
                    return;
                }
                if(password.isEmpty()){
                    Tools.SnackbarShow(mBinding.getRoot(),"请输入密码");
                }
                Loading login = new Loading(getContext(),R.style.CustomDialog);
                login.show();
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        SignlnBean signlnBean = (SignlnBean) msg.obj;
                        if(signlnBean.getCode().equals("500")){
                            login.dismiss();
                            Tools.SnackbarShow(mBinding.getRoot(),signlnBean.getMsg());
                        }else{
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name",signlnBean.getData().getName());
                            editor.putInt("id",signlnBean.getData().getId());
                            editor.putString("time",signlnBean.getData().getTime());
                            editor.putString("username",signlnBean.getData().getUsername());
                            editor.commit();
                            login.dismiss();
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }
                };
                Gson gson = new Gson();
                Map<String,Object>maps = new HashMap<>();
                maps.put("username", user);
                maps.put("password",password);
                String s = gson.toJson(maps);
                mHttpUtil.httpPost(url,s,handler,SignlnBean.class);
            }
        });
    }

    private void initBack() {
        mBinding.singBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });

        mBinding.signinZhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.register);
            }
        });

        mBinding.signinForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.forgetPassword);
            }
        });
    }
}
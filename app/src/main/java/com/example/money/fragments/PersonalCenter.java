package com.example.money.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.money.R;
import com.example.money.databinding.FragmentPersonalCenterBinding;
import com.example.money.tools.LiveDataBus;

public class PersonalCenter extends Fragment {
    FragmentPersonalCenterBinding mBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentPersonalCenterBinding.inflate(inflater,container,false);
        mBinding.personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name","无");
                editor.putInt("id",0);
                editor.putString("time","无");
                editor.putString("username","无");
                editor.commit();
                LiveDataBus.getInstance().with("back",String.class).setValue("111");
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });

        return mBinding.getRoot();
    }
}
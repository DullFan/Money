package com.example.money.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.databinding.FragmentByStagesBinding;
import com.example.money.databinding.FragmentSetUpBinding;

public class SetUp extends Fragment {
    FragmentSetUpBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSetUpBinding.inflate(inflater,container,false);
        ImageView back = mBinding.getRoot().findViewById(R.id.layout_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });
        return mBinding.getRoot();

    }
}
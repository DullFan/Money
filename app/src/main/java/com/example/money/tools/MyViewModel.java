package com.example.money.tools;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.money.bean.HomeBean;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {
    private MutableLiveData<List<HomeBean>> mHomeList;

    public LiveData<List<HomeBean>> getHomeList() {
        if (mHomeList == null) {
            Log.i("TAG", "getHomeList: ");
            mHomeList = new MutableLiveData<>();
            List<HomeBean>list = new ArrayList<>();
            mHomeList.setValue(list);
        }
        return mHomeList;
    }
}

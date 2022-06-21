package com.example.money.tools;


import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class LiveDataBus {
    private Map<String, MutableLiveData<Object>> bus;
    //设置单例模式
    public static LiveDataBus liveDataBus = new LiveDataBus();

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    public static LiveDataBus getInstance(){return liveDataBus;};

    public synchronized <T> MutableLiveData<T> with(String key,Class<T> type){
        if(!bus.containsKey(key)){
            bus.put(key,new MutableLiveData<Object>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }
}

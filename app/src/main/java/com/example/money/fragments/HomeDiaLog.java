package com.example.money.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.money.R;
import com.example.money.databinding.FragmentHomeDiaLogBinding;
import com.example.money.tools.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

public class HomeDiaLog extends Fragment {
    private MyDiaLogFragmentAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeDiaLogBinding mBind = FragmentHomeDiaLogBinding.inflate(inflater, container, false);
        List<String> data = new ArrayList<>();
        data.add("一月");
        data.add("二月");
        data.add("三月");
        data.add("四月");
        data.add("五月");
        data.add("六月");
        data.add("七月");
        data.add("八月");
        data.add("九月");
        data.add("十月");
        data.add("十一月");
        data.add("十二月");
        mAdapter = new MyDiaLogFragmentAdapter(data);
        mBind.dialogFragmentGridview.setAdapter(mAdapter);
        mBind.dialogFragmentGridview.setOnItemClickListener((parent, view, position, id) -> {
            mAdapter.setIndex(position);
            mAdapter.notifyDataSetInvalidated();
        });
        return mBind.getRoot();
    }

    class MyDiaLogFragmentAdapter extends BaseAdapter {
        List<String> mStringList;
        int index =  initIndex();

        private int initIndex() {
            Integer dialogMonthFlag = LiveDataBus.liveDataBus.with("DialogMonthFlag", int.class).getValue();
            if(dialogMonthFlag == 1){
                return LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue()-1;
            }else if (dialogMonthFlag == 2){
                return LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue()-1;
            }
            return 0;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public MyDiaLogFragmentAdapter(List<String> stringList) {
            mStringList = stringList;
        }

        @Override
        public int getCount() {
            return mStringList.size();
        }

        @Override
        public Object getItem(int position) {
            return mStringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHomder = new ViewHolder();
            if (convertView == null) {
                convertView = convertView.inflate(getContext(), R.layout.dialog_layout_home_gridview, null);
                viewHomder.mTextView = convertView.findViewById(R.id.dialog_layout_home_gridview_text);
                convertView.setTag(viewHomder);
            } else {
                viewHomder = (ViewHolder) convertView.getTag();
            }
            if (index == position) {
                viewHomder.mTextView.setTextColor(getResources().getColor(R.color.purple_500));
                LiveDataBus.getInstance().with("month", int.class).setValue(position + 1);
            } else {
                viewHomder.mTextView.setTextColor(getResources().getColor(R.color.black));
            }
            viewHomder.mTextView.setText(mStringList.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView mTextView;
        }
    }
}

package com.example.money.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.MoneyPlusViewPagerBean;
import com.example.money.databinding.FragmentMoneyPlusViewPagerBinding;
import com.example.money.tools.LiveDataBus;

import java.util.List;


public class MoneyPlusViewPager extends Fragment {
    FragmentMoneyPlusViewPagerBinding mBinding;
    List<MoneyPlusViewPagerBean> mList;
    private MyGridViewAdapter mViewAdapter;

    public MoneyPlusViewPager(List<MoneyPlusViewPagerBean> list) {
        mList = list;
    }

    public MoneyPlusViewPager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMoneyPlusViewPagerBinding.inflate(inflater, container, false);
        mViewAdapter = new MyGridViewAdapter(mList);

        mBinding.plusMoneyViewpagerGridview.setAdapter(mViewAdapter);
        mBinding.plusMoneyViewpagerGridview.setOnItemClickListener((parent, view, position, id) -> {
            mViewAdapter.setIndex(position);
            mViewAdapter.notifyDataSetChanged();
        });
        return mBinding.getRoot();
    }

    class MyGridViewAdapter extends BaseAdapter {
        private int index = 0;
        List<MoneyPlusViewPagerBean> mList;

        public MyGridViewAdapter(List<MoneyPlusViewPagerBean> list) {
            mList = list;
            index = initIndex();
        }

        private int initIndex() {
            if (LiveDataBus.liveDataBus.with("setOne", boolean.class).getValue()) {
                HomeItemBean selHomeItemBean = LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).getValue();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getName().equals(selHomeItemBean.getDescribe())) {
                        return i;
                    }
                }
            }
            return 0;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = convertView.inflate(getContext(), R.layout.layout_gridview, null);
                viewHolder.img = convertView.findViewById(R.id.layout_gridview_img);
                viewHolder.name = convertView.findViewById(R.id.layout_gridview_name);
                viewHolder.mCardView = convertView.findViewById(R.id.layout_gridview_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(mList.get(position).getName());
            if (index == position) {
                viewHolder.name.setTextColor(getResources().getColor(R.color.purple_500));
                viewHolder.img.setBackgroundColor(getResources().getColor(R.color.purple_500));
                viewHolder.img.setImageResource(R.drawable.gridimg1);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.select_item);
                convertView.setAnimation(animation);
                LiveDataBus.getInstance().with("classification", String.class).setValue(mList.get(position).getName());
            } else {
                viewHolder.name.setTextColor(getResources().getColor(R.color.textcolor));
                viewHolder.img.setBackgroundColor(Color.parseColor("#f1f1f1"));
                viewHolder.img.setImageResource(mList.get(position).getImg());
            }

            if (position == mList.size() - 1) {
                viewHolder.img.setClickable(true);
                viewHolder.name.setClickable(true);
                viewHolder.img.setImageResource(R.drawable.guan);
                viewHolder.name.setTextColor(getResources().getColor(R.color.purple_500));
                viewHolder.mCardView.setRadius((float) 0.0);
                viewHolder.img.setPadding(2, 2, 2, 2);
                viewHolder.img.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            ImageView img;
            CardView mCardView;
        }
    }
}
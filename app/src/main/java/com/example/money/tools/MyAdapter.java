package com.example.money.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MyAdapter<T> extends BaseAdapter {
    //数据源
    private List<T> list;
    //绑定布局ID
    private int layoutId;
    public MyAdapter(List<T> list, int layoutId) {
        this.list = list;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public T getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(position, convertView, parent, layoutId);
        //将数据传入抽象类中
        bindView(holder, getItem(position),holder.convertView,position);
        return holder.convertView;
    }
    //设置抽象类,
    public abstract void bindView(ViewHolder holder, T obj,View view,int position);
    public static class ViewHolder {
        private ViewGroup parent;
        private Map<Integer, View> map = new HashMap<>();
        private View convertView;
        public int pos;
        public ViewHolder(int position, ViewGroup parent, int layoutId) {
            this.pos = position;
            this.parent = parent;
            //绑定布局
            this.convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            this.convertView.setTag(this);
        }
        public static ViewHolder bind(int position, View convertView, ViewGroup parent, int layoutId) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
                holder.pos = position;
                holder.convertView = convertView;
            } else {
                holder = new ViewHolder(position, parent, layoutId);
            }
            return holder;
        }
        public <T> T getView(int id) {
            T t = (T) map.get(id);
            if (t == null) {
                t = (T) convertView.findViewById(id);
                map.put(id, (View) t);
            }
            return t;
        }
        //修改TextView
        public ViewHolder setText(int id, String str) {
            View view = getView(id);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setText(str);
            }
            return this;
        }
        //修改图片
        public ViewHolder setBackgroundRes(int id, int imgId) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ImageView textView = (ImageView) view;
                textView.setBackgroundResource(imgId);
            }
            return this;
        }
        //获取网络照片
        public ViewHolder setImgUrl(int img, String imgUrl) {
            View view = getView(img);
            if (view instanceof ImageView) {
                ImageView textView = (ImageView) view;
                Glide.with(parent.getContext()).load(imgUrl).into(textView);
            }
            return this;
        }
    }
}

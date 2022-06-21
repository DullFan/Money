package com.example.money.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.bean.ConsumptionBean;
import com.example.money.bean.HomeBean;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.PostBean;
import com.example.money.databinding.FragmentCalendarBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.LiveDataBus;
import com.example.money.tools.MyAdapter;
import com.example.money.tools.Tools;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Calendar extends Fragment {
    FragmentCalendarBinding mBinding;
    private Integer mMonth;
    private Integer mYear;
    private HttpUtil mHttpUtil;
    private String nowData;
    private List<HomeBean> mHomeBeanList;
    private MyRecyclerAdapter mAdapter;
    private MyRecyclerItemAdapter mAdapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentCalendarBinding.inflate(inflater, container, false);
        LiveDataBus.liveDataBus.with("DialogMonthFlag", int.class).setValue(2);
        mHomeBeanList = new ArrayList<>();
        mHttpUtil = new HttpUtil();
        //设置进来时年月
        mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
        mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
        if (mMonth < 10) {
            mBinding.calendarYear.setText(mYear + ".0" + mMonth);
        } else {
            mBinding.calendarYear.setText(mYear + "." + mMonth);
        }
        initData();
        ininTitle();
        initWeek();
        initRecycler();
        initFlat();
        return mBinding.getRoot();
    }

    private void initFlat() {
        mBinding.calendarFloatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
                String mName = sharedPreferences.getString("name", "无");
                if (mName.equals("无")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                LiveDataBus.liveDataBus.with("setOne", boolean.class).setValue(false);
                LiveDataBus.liveDataBus.with("calendarIsFlag", boolean.class).setValue(true);
                LiveDataBus.liveDataBus.with("calendarDate", String.class).setValue(nowData);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.moneyPlus);
            }
        });
    }

    private void initRecycler() {
        mBinding.calendarRecycler.setLayoutManager(new GridLayoutManager(getContext(), 7));
        mAdapter = new MyRecyclerAdapter(mHomeBeanList);
        mBinding.calendarRecycler.setAdapter(mAdapter);
    }


    private void initWeek() {
        List<String> list = new ArrayList<>();
        list.add("日");
        list.add("一");
        list.add("二");
        list.add("三");
        list.add("四");
        list.add("五");
        list.add("六");
        mBinding.calendarWeek.setAdapter(new MyAdapter<String>(list, R.layout.layout_textview) {
            @Override
            public void bindView(ViewHolder holder, String obj, View view, int position) {
                holder.setText(R.id.layout_text, list.get(position));
            }
        });
        mBinding.calendarWeek.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void ininTitle() {
        mBinding.calendarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });

        Drawable drawable = getResources().getDrawable(R.drawable.xiala);
        drawable.setBounds(0, 0, 20, 20);
        mBinding.calendarYear.setCompoundDrawablePadding(10);
        mBinding.calendarYear.setCompoundDrawables(null, null, drawable, null);
        mBinding.calendarYear.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getContext(), R.layout.dialog_home_layout, null);
            TabLayout tabLayout = view.findViewById(R.id.dialog_home_tablayout);
            ViewPager2 viewPager = view.findViewById(R.id.dialog_home_viewpager);
            builder.setView(view);
            //设置对话框中的内容
            initDialogView(tabLayout, viewPager);
            builder.setPositiveButton("确定", (dialog, which) -> {
                String year1 = LiveDataBus.getInstance().with("year2", String.class).getValue();
                if (year1.contains("年")) {
                    LiveDataBus.getInstance().with("moneyYear3", int.class).setValue(
                            Integer.valueOf(LiveDataBus.getInstance().with("year2", String.class).getValue().replace("年", ""))
                    );
                }
                //新
                LiveDataBus.getInstance().with("moneyMonth3", int.class).setValue(
                        LiveDataBus.getInstance().with("month", int.class).getValue()
                );
                Integer month = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
                Integer year = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
                if (month < 10) {
                    mBinding.calendarYear.setText(year + ".0" + month);
                } else {
                    mBinding.calendarYear.setText(year + "." + month);
                }
                initData();
            });
            builder.show();
        });
    }

    private void initData() {
        mHomeBeanList.clear();
        Integer integer = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
        Integer moneyMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
        String data = integer + "-" + moneyMonth;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = "/consumption/getmonth/" + id + "/" + data;
        Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                ConsumptionBean consumptionBean = (ConsumptionBean) msg.obj;
                if (consumptionBean.getMsg().equals("500")) {
                    Tools.SnackbarShow(mBinding.getRoot(), "获取失败");
                    return;
                }
                //排序
                List<ConsumptionBean.DataDTO> list = consumptionBean.getData();
                list.sort((o1, o2) -> {
                    Collator collator = Collator.getInstance(Locale.CHINA);
                    CollationKey key1 = collator.getCollationKey(o1.getCon_date());
                    CollationKey key2 = collator.getCollationKey(o2.getCon_dissipate());
                    return key1.compareTo(key2);
                });
                List<String> mStringList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    mStringList.add(list.get(i).getCon_dissipate());
                }
                List<String> strings = new ArrayList<>(new HashSet<>(mStringList));
                for (int i = 0; i < strings.size(); i++) {
                    List<HomeItemBean> homeItemBeans = new ArrayList<>();
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        if (strings.get(i).equals(list.get(i1).getCon_dissipate())) {
                            boolean flag1;
                            boolean flag2;

                            if (list.get(i1).getCon_refund().equals("1")) {
                                flag2 = true;
                            } else {
                                flag2 = false;
                            }
                            if (list.get(i1).getCon_consume().equals("1")) {
                                flag1 = true;
                            } else {
                                flag1 = false;
                            }
                            HomeItemBean homeItemBean = new HomeItemBean(
                                    list.get(i1).getCon_id(),
                                    Double.valueOf(list.get(i1).getCon_money()),
                                    list.get(i1).getCon_record(),
                                    list.get(i1).getCon_remarks(),
                                    flag1,
                                    flag2,
                                    list.get(i1).getCon_refundRemarks(),
                                    list.get(i1).getCon_refundTime(),
                                    list.get(i1).getCon_refundMoney(),
                                    list.get(i1).getCon_dissipate(),
                                    list.get(i1).getCon_date());
                            homeItemBeans.add(homeItemBean);
                        }
                    }
                    mHomeBeanList.add(new HomeBean(strings.get(i), homeItemBeans));
                }
                mHomeBeanList.sort((o1, o2) -> {
                    Collator collator = Collator.getInstance(Locale.CHINA);
                    CollationKey key1 = collator.getCollationKey(o1.getDate());
                    CollationKey key2 = collator.getCollationKey(o2.getDate());
                    return key2.compareTo(key1);
                });
                mAdapter.setMonthDay(Tools.getMonthOfDay(integer, moneyMonth));
                mAdapter.notifyDataSetChanged();
            }
        };
        mHttpUtil.httpGet(url, handler, ConsumptionBean.class);
    }

    private void initDialogView(TabLayout tabLayout, ViewPager2 viewPager) {
        viewPager.setUserInputEnabled(false);
        //内容
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        //标题
        List<String> stringList = new ArrayList<>();
        stringList.add("2021年");
        stringList.add("2020年");
        stringList.add("2019年");
        stringList.add("2018年");
        stringList.add("2017年");
        stringList.add("2016年");
        stringList.add("2015年");
        stringList.add("2014年");
        viewPager.setAdapter(new MyViewPagerAdapter(getActivity(), fragmentList));
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(stringList.get(position));
            }
        }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LiveDataBus.getInstance().with("year2", String.class).setValue(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        switch (LiveDataBus.getInstance().with("year2", String.class).getValue()) {
            case "2021年":
                tabLayout.getTabAt(0).select();
                viewPager.setCurrentItem(0, false);
                break;
            case "2020年":
                tabLayout.getTabAt(1).select();
                viewPager.setCurrentItem(1, false);
                break;
            case "2019年":
                tabLayout.getTabAt(2).select();
                viewPager.setCurrentItem(2, false);
                break;
            case "2018年":
                tabLayout.getTabAt(3).select();
                viewPager.setCurrentItem(3, false);
                break;
            case "2017年":
                tabLayout.getTabAt(4).select();
                viewPager.setCurrentItem(4, false);
                break;
            case "2016年":
                tabLayout.getTabAt(5).select();
                viewPager.setCurrentItem(5, false);
                break;
            case "2015年":
                tabLayout.getTabAt(6).select();
                viewPager.setCurrentItem(6, false);
                break;
            case "2014年":
                tabLayout.getTabAt(7).select();
                viewPager.setCurrentItem(7, false);
                break;
        }
    }

    class MyViewPagerAdapter extends FragmentStateAdapter {
        List<Fragment> mFragmentList;

        public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> mFragmentList) {
            super(fragmentActivity);
            this.mFragmentList = mFragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
        int monthDay;
        int kong;
        int index;
        List<HomeBean> mHomeBeanList;

        public MyRecyclerAdapter(List<HomeBean> homeBeanList) {
            mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
            mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
            String week = Tools.getWeek(mYear + "-" + mMonth + "-1");
            kong = initKong(week);
            Time time = new Time("GMT+8");
            time.setToNow();
            int day = time.monthDay;
            index = kong + day - 1;
            this.monthDay = Tools.getMonthOfDay(mYear, mMonth) + kong;
            mHomeBeanList = homeBeanList;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        private int initKong(String week) {
            switch (week) {
                case "星期天":
                    return 0;

                case "星期一":
                    return 1;

                case "星期二":
                    return 2;

                case "星期三":
                    return 3;

                case "星期四":
                    return 4;

                case "星期五":
                    return 5;

                case "星期六":
                    return 6;
            }
            return 0;
        }

        public void setMonthDay(int monthDay) {
            mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
            mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
            String week = Tools.getWeek(mYear + "-" + mMonth + "-1");
            kong = initKong(week);
            this.monthDay = monthDay;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.calendar_viewpager_recycler_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (position < kong) {
                holder.mConstraintLayout.setVisibility(View.GONE);
            } else {
                holder.mConstraintLayout.setVisibility(View.VISIBLE);
            }
            if (index == position) {
                GradientDrawable drawable = (GradientDrawable) holder.mConstraintLayout.getBackground();
                drawable.setColor(Color.parseColor("#8BD6F8"));
                holder.day.setTextColor(getResources().getColor(R.color.purple_500));
                holder.money1.setTextColor(getResources().getColor(R.color.purple_500));
                holder.money2.setTextColor(getResources().getColor(R.color.purple_500));
                holder.jie.setTextColor(getResources().getColor(R.color.purple_500));
                drawable.setAlpha(70);
                mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
                mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
                String data = mYear + "-" + mMonth + "-" + (position - kong + 1);
                nowData = mYear + "." + mMonth + "." + (position - kong + 1);
                mBinding.cancelRecyclerLayout.setPadding(0, 0, 0, 0);
                mBinding.calendarRecyclerData.setVisibility(View.GONE);
                mBinding.calendarRecyclerCollect.setVisibility(View.GONE);
                mBinding.calendarRecyclerBranch.setVisibility(View.GONE);
                mBinding.calendarRecyclerRecycler.setVisibility(View.GONE);
                for (int i = 0; i < mHomeBeanList.size(); i++) {
                    if (data.equals(mHomeBeanList.get(i).getDate())) {
                        mBinding.calendarRecyclerData.setVisibility(View.VISIBLE);
                        mBinding.calendarRecyclerCollect.setVisibility(View.VISIBLE);
                        mBinding.calendarRecyclerBranch.setVisibility(View.VISIBLE);
                        mBinding.calendarRecyclerRecycler.setVisibility(View.VISIBLE);
                        mBinding.cancelRecyclerLayout.setPadding(10, 10, 10, 5);
                        List<HomeItemBean> homeItemBeans = mHomeBeanList.get(i).getHomeItemBeans();
                        double num1 = 0;
                        double num2 = 0;
                        for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                            if (homeItemBeans.get(i1).isFlag()) {
                                //支出
                                if (!homeItemBeans.get(i1).isRefund()) {
                                    num1 += homeItemBeans.get(i1).getMoney();
                                }
                            } else {
                                //收入
                                num2 += homeItemBeans.get(i1).getMoney();
                            }
                        }
                        mBinding.calendarRecyclerData.setText(mHomeBeanList.get(i).getDate() + "  " + Tools.getWeek(mHomeBeanList.get(i).getDate()));
                        if (num1 != 0) {
                            mBinding.calendarRecyclerCollect.setText("支:" + num1);
                        }

                        if (num2 != 0) {
                            mBinding.calendarRecyclerBranch.setText("收:" + num2);
                        }
                        mBinding.calendarRecyclerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        mAdapter1 = new MyRecyclerItemAdapter(homeItemBeans);
                        mBinding.calendarRecyclerRecycler.setAdapter(mAdapter1);
                        break;
                    } else {
                        mBinding.cancelRecyclerLayout.setPadding(0, 0, 0, 0);
                        mBinding.calendarRecyclerData.setVisibility(View.GONE);
                        mBinding.calendarRecyclerCollect.setVisibility(View.GONE);
                        mBinding.calendarRecyclerBranch.setVisibility(View.GONE);
                        mBinding.calendarRecyclerRecycler.setVisibility(View.GONE);
                    }
                }
            } else {
                GradientDrawable drawable = (GradientDrawable) holder.mConstraintLayout.getBackground();
                drawable.setColor(getResources().getColor(R.color.white));
                holder.day.setTextColor(getResources().getColor(R.color.black));
                holder.money1.setTextColor(getResources().getColor(R.color.red));
                holder.money2.setTextColor(getResources().getColor(R.color.teal_200));
                holder.jie.setTextColor(getResources().getColor(R.color.textcolor));
                drawable.setAlpha(255);
            }


            initSel(holder, position);
            holder.day.setText((position - kong + 1) + "");
            holder.money1.setVisibility(View.GONE);
            holder.money2.setVisibility(View.GONE);
            holder.jie.setVisibility(View.VISIBLE);
            mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
            mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
            String data = mYear + "-" + mMonth + "-" + holder.day.getText().toString();
            for (int i = 0; i < mHomeBeanList.size(); i++) {
                if (data.equals(mHomeBeanList.get(i).getDate())) {
                    double num1 = 0;
                    double num2 = 0;
                    for (int i1 = 0; i1 < mHomeBeanList.get(i).getHomeItemBeans().size(); i1++) {
                        HomeItemBean homeItemBean = mHomeBeanList.get(i).getHomeItemBeans().get(i1);
                        if (homeItemBean.isFlag()) {//支出
                            num1 += homeItemBean.getMoney();
                            if (homeItemBean.isRefund()) {
                                num1 -= homeItemBean.getMoney();
                            }
                        } else {//收入
                            num2 += homeItemBean.getMoney();
                        }
                    }
                    holder.jie.setVisibility(View.GONE);
                    holder.money1.setText("-" + num1);
                    holder.money2.setText("+" + num2);
                    if (num1 != 0) {
                        holder.money1.setVisibility(View.VISIBLE);
                    }
                    if (num2 != 0) {
                        holder.money2.setVisibility(View.VISIBLE);
                    }
                }
            }


            if (mHomeBeanList.size() == 0) {
                holder.money1.setVisibility(View.GONE);
                holder.money2.setVisibility(View.GONE);
                holder.jie.setVisibility(View.VISIBLE);
            }

            holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setIndex(position);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        private void initSel(@NonNull ViewHolder holder, int position) {
            Time time = new Time("GMT+8");
            time.setToNow();
            int day = time.monthDay;
            int month = time.month + 1;
            int year = time.year;
            if (year == LiveDataBus.getInstance().with("moneyYear3", int.class).getValue()) {
                if (month == LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue()) {
                    if (day + kong - 1 == position) {
                        holder.day.setTextColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return monthDay;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView day;
            TextView jie;
            TextView money1;
            TextView money2;
            ConstraintLayout mConstraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.recycler_item_day);
                jie = itemView.findViewById(R.id.recycler_item_jieri);
                money1 = itemView.findViewById(R.id.recycler_item_money1);
                money2 = itemView.findViewById(R.id.recycler_item_money2);
                mConstraintLayout = itemView.findViewById(R.id.recycler_item_layout);
            }
        }
    }

    class MyRecyclerItemAdapter extends RecyclerView.Adapter<MyRecyclerItemAdapter.ViewHolder> {
        List<HomeItemBean> mHomeItemBeanList;

        public MyRecyclerItemAdapter(List<HomeItemBean> homeItemBeanList) {
            mHomeItemBeanList = homeItemBeanList;
        }

        @NonNull
        @Override
        public MyRecyclerItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.home_recycler_item, parent, false);
            return new MyRecyclerItemAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRecyclerItemAdapter.ViewHolder holder, int position) {
            if (mHomeItemBeanList.get(position).isFlag()) {//支出
                holder.describe.setText(mHomeItemBeanList.get(position).getDescribe());
                holder.money.setText("-" + mHomeItemBeanList.get(position).getMoney());
                holder.money.setTextColor(getResources().getColor(R.color.red));
                GradientDrawable drawable = (GradientDrawable) holder.ovel.getBackground();
                drawable.setColor(getResources().getColor(R.color.red));
            } else {//收入
                holder.describe.setText(mHomeItemBeanList.get(position).getDescribe());
                holder.money.setText("+" + mHomeItemBeanList.get(position).getMoney());
                holder.money.setTextColor(getResources().getColor(R.color.teal_200));
                GradientDrawable drawable = (GradientDrawable) holder.ovel.getBackground();
                drawable.setColor(getResources().getColor(R.color.teal_200));
            }
            //是否有备注
            if (!mHomeItemBeanList.get(position).getRemarks().equals("")) {
                holder.remarks.setVisibility(View.VISIBLE);
                holder.remarks.setText(mHomeItemBeanList.get(position).getRemarks());
            } else {
                holder.remarks.setVisibility(View.GONE);
            }

            //是否有退款
            if (mHomeItemBeanList.get(position).isRefund()) {
                holder.refund.setText("(已退款" + mHomeItemBeanList.get(position).getRefundMoney() + ")");
                holder.refund.setVisibility(View.VISIBLE);
            } else {
                holder.refund.setVisibility(View.GONE);
            }

            holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog builder = new AlertDialog.Builder(getContext()).create();
                    Window window = builder.getWindow();
                    window.setGravity(Gravity.BOTTOM);
                    window.setWindowAnimations(R.style.PopupAnimation);
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(layoutParams);
                    //设置最小宽度
                    window.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
                    //设置背景颜色,修改最底层的背景颜色(DecorView)
                    window.getDecorView().setBackgroundResource(R.drawable.money_item_dialog_shape);
                    View layout = View.inflate(getContext(), R.layout.money_item_dialog, null);
                    builder.setView(layout);
                    initView(layout, mHomeItemBeanList.get(position), builder);
                    builder.show();
                }

                private void initView(View layout, HomeItemBean homeItemBean, AlertDialog builder) {
                    TextView textView1 = layout.findViewById(R.id.money_item_money);
                    TextView textView2 = layout.findViewById(R.id.money_item_type);
                    TextView textView3 = layout.findViewById(R.id.money_item_time);
                    TextView textView4 = layout.findViewById(R.id.money_item_time2);
                    TextView textView5 = layout.findViewById(R.id.money_item_remarks);
                    ConstraintLayout constraintLayout = layout.findViewById(R.id.money_item_layout4);
                    if (homeItemBean.isFlag()) {
                        textView1.setText("-" + homeItemBean.getMoney() + "");
                        textView1.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        textView1.setText("+" + homeItemBean.getMoney() + "");
                        textView1.setTextColor(getResources().getColor(R.color.teal_200));
                    }
                    textView2.setText(homeItemBean.getDescribe() + "");
                    textView3.setText(homeItemBean.getTime());
                    String date = homeItemBean.getRecordTime().substring(0, homeItemBean.getRecordTime().length() - 3);
                    textView4.setText("记录于" + date);
                    if (homeItemBean.getRemarks().equals("")) {
                        constraintLayout.setVisibility(View.GONE);
                    } else {
                        textView5.setText(homeItemBean.getRemarks() + "");
                    }
                    //删除
                    TextView del = layout.findViewById(R.id.money_item_del);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("删除");
                            builder.setMessage("确定要删除这条记录吗");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String url = "/consumption/delitem/" + homeItemBean.getId();
                                    Handler handler = new Handler() {
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {
                                            initData();
                                        }
                                    };
                                    mHttpUtil.okhttpDelete(url, "", handler, PostBean.class);
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.show();
                        }
                    });
                    //退款
                    TextView tui = layout.findViewById(R.id.money_item_tui);
                    if (!homeItemBean.isFlag()) {
                        tui.setVisibility(View.GONE);
                    } else {
                        tui.setVisibility(View.VISIBLE);
                    }
                    ConstraintLayout layout1 = layout.findViewById(R.id.money_item_layout5);
                    ConstraintLayout layout2 = layout.findViewById(R.id.money_item_layout6);
                    if (homeItemBean.isRefund()) {
                        layout1.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.VISIBLE);
                        TextView textViewname = layout.findViewById(R.id.money_item_tuiname);
                        TextView textViewname2 = layout.findViewById(R.id.money_item_tuitime);
                        TextView textViewname3 = layout.findViewById(R.id.money_item_tuimoney);
                        TextView textViewname4 = layout.findViewById(R.id.money_item_tuibeizhu);
                        textViewname.setText("退款·" + homeItemBean.getDescribe());
                        textViewname2.setText(homeItemBean.getRefundTime());
                        textViewname3.setText(homeItemBean.getRefundMoney());
                        if (homeItemBean.getRefundRemarks().isEmpty()) {
                            textView4.setVisibility(View.GONE);
                        } else {
                            textViewname4.setText(homeItemBean.getRefundRemarks());
                            textView4.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.GONE);
                    }
                    tui.setOnClickListener(v -> {
                        builder.dismiss();
                        LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).setValue(homeItemBean);
                        NavController navController = Navigation.findNavController(mBinding.getRoot());
                        navController.navigate(R.id.refundInterface);
                    });
                    //修改
                    TextView set = layout.findViewById(R.id.money_item_set);
                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            LiveDataBus.liveDataBus.with("setOne", boolean.class).setValue(true);
                            LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).setValue(homeItemBean);
                            NavController navController = Navigation.findNavController(mBinding.getRoot());
                            navController.navigate(R.id.moneyPlus);
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mHomeItemBeanList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ovel;
            TextView describe;
            TextView money;
            TextView remarks;
            TextView refund;
            ConstraintLayout mConstraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mConstraintLayout = itemView.findViewById(R.id.home_recycler_item_layout);
                ovel = itemView.findViewById(R.id.home_recycler_item_ovel);
                describe = itemView.findViewById(R.id.home_recycler_item_describe);
                money = itemView.findViewById(R.id.home_recycler_item_money);
                remarks = itemView.findViewById(R.id.home_recycler_item_remarks);
                refund = itemView.findViewById(R.id.home_recycler_item_refund);
            }
        }
    }
}
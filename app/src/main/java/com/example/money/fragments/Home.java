package com.example.money.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.money.R;
import com.example.money.bean.ConsumptionBean;
import com.example.money.bean.HomeBean;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.PostBean;
import com.example.money.databinding.FragmentHomeBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.LiveDataBus;
import com.example.money.tools.MyViewModel;
import com.example.money.tools.Tools;
import com.example.money.ui.Loading;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class Home extends Fragment {
    private FragmentHomeBinding mBinding;

    private LinearLayoutManager mLayout;
    private MyRecyclerAdapter mAdapter;
    private boolean mIsShow;
    private String mName;
    private HttpUtil mHttpUtil;
    private MyViewModel mMyViewModel;
    private Loading mLoading;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mMyViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        mLoading = new Loading(getContext(),R.style.CustomDialog);
        LiveDataBus.liveDataBus.with("DialogMonthFlag",int.class).setValue(1);
        mLoading.show();
        mHttpUtil = new HttpUtil();
        mBinding.homeDrawerlayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float percent = 1 - slideOffset / 10;
                mBinding.homeAsdasda.setScaleX(percent);
                mBinding.homeAsdasda.setScaleY(percent);
                int i = (int) (slideOffset * 20 * getResources().getDisplayMetrics().density + 0.5f);
                mBinding.homeAsdasda.setRadius(i);
            }
        });


        //将所有控件设置为不可见
        initGson();
        //登录注册
        initLogin();
        //设置默认年月
        initYearMonth();
        //设置侧滑点击事件
        initDrawerLayout();
        initRecycler();
        //刷新
        initSwite();
        initData();
        initFloatingActionButton();
        //设置年月
        initYear();
        final int sdk = Build.VERSION.SDK_INT;
        return mBinding.getRoot();
    }

    private void initGson() {
        mBinding.homeDrawerlayout.setVisibility(View.GONE);
    }

    private void initData() {
        Integer integer = LiveDataBus.getInstance().with("moneyYear2", int.class).getValue();
        Integer moneyMonth = LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue();
        String data = integer + "-" + moneyMonth;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = "/consumption/getmonth/" + id + "/" + data;
        Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                mMyViewModel.getHomeList().getValue().clear();
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
                    List<HomeBean> value = mMyViewModel.getHomeList().getValue();
                    value.add(new HomeBean(strings.get(i), homeItemBeans));
                }
                mMyViewModel.getHomeList().getValue().sort((o1, o2) -> {
                    Date key1 = Tools.stringToDate(o1.getDate());
                    Date key2 = Tools.stringToDate(o2.getDate());
                    return key2.compareTo(key1);
                });

                for (int i = 0; i < mMyViewModel.getHomeList().getValue().size(); i++) {
                    mMyViewModel.getHomeList().getValue().get(i).getHomeItemBeans().sort(
                            (o1, o2) -> {
                        Date key1 = Tools.stringToDate2(o1.getRecordTime());
                        Date key2 = Tools.stringToDate2(o2.getRecordTime());
                        return key2.compareTo(key1);
                    });
                }

                if (mMyViewModel.getHomeList().getValue().size() != 0) {
                    initSlide();
                    mBinding.homeNoData.setVisibility(View.GONE);
                    mBinding.homeImg.setImageResource(R.drawable.img1);
                    mBinding.homeLayout2.setVisibility(View.VISIBLE);
                } else {
                    mBinding.homeNoData.setVisibility(View.VISIBLE);
                    mBinding.homeImg.setImageResource(0);
                    mBinding.homeLayout2.setVisibility(View.GONE);
                    mBinding.homeLayout.setBackgroundColor(getResources().getColor(R.color.purple_500));
                }
                //设置月收入
                initShouru();
                mLoading.dismiss();
                mBinding.homeDrawerlayout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        };
        mHttpUtil.httpGet(url, handler, ConsumptionBean.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initRecycler() {
        mLayout = new LinearLayoutManager(getContext());
        mBinding.homeRecycler.setLayoutManager(mLayout);
        if (mMyViewModel.getHomeList().getValue().size() != 0) {
            initSlide();
            mBinding.homeNoData.setVisibility(View.GONE);
            mBinding.homeImg.setImageResource(R.drawable.img1);
            mBinding.homeLayout2.setVisibility(View.VISIBLE);
        } else {
            mBinding.homeNoData.setVisibility(View.VISIBLE);
            mBinding.homeImg.setImageResource(0);
            mBinding.homeLayout2.setVisibility(View.GONE);
            mBinding.homeLayout.setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
        mAdapter = new MyRecyclerAdapter(mMyViewModel.getHomeList().getValue());
        mBinding.homeRecycler.setAdapter(mAdapter);
    }

    private void initSing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("请先登录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavController navController = Navigation.findNavController(mBinding.homeFloatingbutton);
                navController.navigate(R.id.signIn);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void initLogin() {
        LiveDataBus.liveDataBus.with("selectdate", String.class).setValue("无");
        View view = mBinding.homeNavigation.getHeaderView(0);
        ImageView imageView = view.findViewById(R.id.header_img);
        TextView textView = view.findViewById(R.id.header_name);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
        mName = sharedPreferences.getString("name", "无");
        if (mName.equals("无")) {
            textView.setText("点击登录");
            imageView.setImageResource(R.drawable.nohead);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.signIn);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.signIn);
                }
            });
        } else {
            textView.setText(mName);
            imageView.setImageResource(R.drawable.img1);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.personalCenter);
                }
            });
        }

        mBinding.homeCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveDataBus.getInstance().with("moneyYear3", int.class).setValue(
                        LiveDataBus.getInstance().with("moneyYear2", int.class).getValue()
                );
                LiveDataBus.getInstance().with("moneyMonth3", int.class).setValue(
                        LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue()
                );
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.calendar);
            }
        });

        mBinding.homeQuanquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveDataBus.getInstance().with("moneyYear3", int.class).setValue(
                        LiveDataBus.getInstance().with("moneyYear2", int.class).getValue()
                );
                LiveDataBus.getInstance().with("moneyMonth3", int.class).setValue(
                        LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue()
                );
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.monthlyReport);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSlide() {
        mIsShow = false;
        int addNum = 160;
        int Max = 255 + addNum;
        NestedScrollView scrollView = mBinding.homeNested;
        ConstraintLayout constraintLayout = mBinding.homeLayout;
        constraintLayout.setBackgroundColor(0);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= Max) {
                    constraintLayout.setBackgroundColor(getResources().getColor(R.color.purple_500));
                }
                if (scrollY <= Max) {
                    constraintLayout.setBackgroundColor(0);
                }
                if (scrollY - oldScrollY > 0 && mIsShow) {
                    mIsShow = false;
                    Animation mHiddenAction = AnimationUtils.loadAnimation(getContext(), R.anim.scale_out);
                    //上滑
                    mBinding.homeFloatingbutton.setVisibility(View.GONE);
                    mBinding.homeFloatingbutton.setAnimation(mHiddenAction);

                } else if (scrollY - oldScrollY < 0 && !mIsShow) {
                    Animation mShowAction = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
                    mIsShow = true;
                    //下滑
                    mBinding.homeFloatingbutton.setVisibility(View.VISIBLE);
                    mBinding.homeFloatingbutton.setAnimation(mShowAction);
                }
            }
        });
    }

    private void initShouru() {
        double num1 = 0;
        double num2 = 0;
        for (int i = 0; i < mMyViewModel.getHomeList().getValue().size(); i++) {
            List<HomeItemBean> homeItemBeans = mMyViewModel.getHomeList().getValue().get(i).getHomeItemBeans();
            for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                if (homeItemBeans.get(i1).isFlag()) {//支出
                    num1 += homeItemBeans.get(i1).getMoney();
                    if (homeItemBeans.get(i1).isRefund()) {
                        num1 -= homeItemBeans.get(i1).getMoney();
                    }
                } else {//收入
                    num2 += homeItemBeans.get(i1).getMoney();
                }
            }
        }
        num1 = Double.valueOf(String.format("%.2f", num1));
        num2 = Double.valueOf(String.format("%.2f", num2));
        LiveDataBus.liveDataBus.with("moneynum1", double.class).setValue(num1);
        LiveDataBus.liveDataBus.with("moneynum2", double.class).setValue(num2);
        double num3 = num2 + (-num1);
        LiveDataBus.liveDataBus.with("moneynum3", double.class).setValue(num3);
        LiveDataBus.liveDataBus.with("moneynum1", double.class).observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                Double moneynum1 = LiveDataBus.liveDataBus.with("moneynum1", double.class).getValue();
                Double moneynum2 = LiveDataBus.liveDataBus.with("moneynum2", double.class).getValue();
                Double moneynum3 = LiveDataBus.liveDataBus.with("moneynum3", double.class).getValue();
                mBinding.moneyMoney1.setText(moneynum1 + "");
                mBinding.moneyMoney2.setText(moneynum2 + "");
                mBinding.moneyMoney3.setText(moneynum3 + "");
            }
        });
    }

    private void initYearMonth() {
        if (Tools.isFlagNow) {
            Tools.isFlagNow = false;
            LiveDataBus.liveDataBus.with("isFlagNow", boolean.class).setValue(false);
            Log.i("TAG", "initYearMonth: 初始化");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //初始化
            LiveDataBus.getInstance().with("year", String.class).setValue(year + "年");
            LiveDataBus.getInstance().with("year2", String.class).setValue(year + "年");
            LiveDataBus.getInstance().with("moneyYear", int.class).setValue(year);
            LiveDataBus.getInstance().with("moneyMonth", int.class).setValue(month);
            LiveDataBus.getInstance().with("moneyDay", int.class).setValue(day);
            LiveDataBus.getInstance().with("moneyYear2", int.class).setValue(year);
            LiveDataBus.getInstance().with("moneyMonth2", int.class).setValue(month);
        }
        Integer moneyYear2 = LiveDataBus.getInstance().with("moneyYear2", int.class).getValue();
        Integer moneyMonth2 = LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue();
        if (moneyMonth2 < 10) {
            mBinding.homeYear.setText(moneyYear2 + ".0" + moneyMonth2);
        } else {
            mBinding.homeYear.setText(moneyYear2 + "." + moneyMonth2);
        }
        //默认金额
        Double moneynum1 = LiveDataBus.liveDataBus.with("moneynum1", double.class).getValue();
        Double moneynum2 = LiveDataBus.liveDataBus.with("moneynum2", double.class).getValue();
        Double moneynum3 = LiveDataBus.liveDataBus.with("moneynum3", double.class).getValue();
        mBinding.moneyMoney1.setText(moneynum1 + "");
        mBinding.moneyMoney2.setText(moneynum2 + "");
        mBinding.moneyMoney3.setText(moneynum3 + "");
    }

    private void initYear() {
        //设置图片
        Drawable drawable = getResources().getDrawable(R.drawable.xiala);
        drawable.setBounds(0, 0, 20, 20);
        mBinding.homeYear.setCompoundDrawablePadding(10);
        mBinding.homeYear.setCompoundDrawables(null, null, drawable, null);
        mBinding.homeYear.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getContext(), R.layout.dialog_home_layout, null);
            TabLayout tabLayout = view.findViewById(R.id.dialog_home_tablayout);
            ViewPager2 viewPager = view.findViewById(R.id.dialog_home_viewpager);
            builder.setView(view);
            //设置对话框中的内容
            initDialogView(tabLayout, viewPager);
            builder.setPositiveButton("确定", (dialog, which) -> {
                String year1 = LiveDataBus.getInstance().with("year", String.class).getValue();
                if (year1.contains("年")) {
                    LiveDataBus.getInstance().with("moneyYear2", int.class).setValue(
                            Integer.valueOf(LiveDataBus.getInstance().with("year", String.class).getValue().replace("年", ""))
                    );
                }
                //新
                LiveDataBus.getInstance().with("moneyMonth2", int.class).setValue(
                        LiveDataBus.getInstance().with("month", int.class).getValue()
                );
                Integer month = LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue();
                Integer year = LiveDataBus.getInstance().with("moneyYear2", int.class).getValue();
                if (month < 10) {
                    mBinding.homeYear.setText(year + ".0" + month);
                } else {
                    mBinding.homeYear.setText(year + "." + month);
                }
                initData();
            });
            builder.show();
        });
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
                LiveDataBus.getInstance().with("year", String.class).setValue(tab.getText().toString());
                LiveDataBus.getInstance().with("year2", String.class).setValue(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        switch (LiveDataBus.getInstance().with("year", String.class).getValue()) {
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


    private void initFloatingActionButton() {
        mBinding.homeFloatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveDataBus.liveDataBus.with("setOne",boolean.class).setValue(false);
                LiveDataBus.liveDataBus.with("calendarIsFlag",boolean.class).setValue(false);

                if (mName.equals("无")) {
                    initSing();
                    return;
                }
                NavController navController = Navigation.findNavController(mBinding.homeFloatingbutton);
                navController.navigate(R.id.moneyPlus);
            }
        });
    }

    private void initSwite() {
//        mBinding.homeSwipe.setColorSchemeResources(android.R.color.holo_blue_light,
//                android.R.color.holo_red_light, android.R.color.holo_orange_light
//                , android.R.color.holo_green_light);
//        mBinding.homeSwipe.setProgressViewOffset(false, 100, 300);
//
//        mBinding.homeSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBinding.homeSwipe.setRefreshing(false);
//                    }
//                }, 2000);
//            }
//        });
    }

    private void initDrawerLayout() {
        mBinding.homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.homeDrawerlayout.openDrawer(GravityCompat.START);
            }
        });
        mBinding.homeDrawerlayout.setDrawerElevation(100);
        //开启
        mBinding.homeNavigation.setNavigationItemSelectedListener(item -> {
            if (mName.equals("无")) {
                initSing();
                return true;
            }
            NavController navController = Navigation.findNavController(mBinding.homeNavigation);
            switch (item.getItemId()) {
                case R.id.statistics:
                    LiveDataBus.getInstance().with("moneyYear3", int.class).setValue(
                            LiveDataBus.getInstance().with("moneyYear2", int.class).getValue()
                    );
                    LiveDataBus.getInstance().with("moneyMonth3", int.class).setValue(
                            LiveDataBus.getInstance().with("moneyMonth2", int.class).getValue()
                    );
                    NavController controller = Navigation.findNavController(mBinding.getRoot());
                    controller.navigate(R.id.monthlyReport);
                    break;

                case R.id.setUp:
                    navController.navigate(R.id.setUp);
                    break;
            }
            return true;
        });
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
        List<HomeBean> mHomeBeanList;

        public MyRecyclerAdapter(List<HomeBean> homeBeanList) {
            mHomeBeanList = homeBeanList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view2 = LayoutInflater.from(getContext())
                    .inflate(R.layout.home_recycler_layout, parent, false);
            return new ViewHolder(view2);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            int index = mHomeBeanList.get(position).getDate().indexOf(".");
            holder.data.setText(mHomeBeanList.get(position).getDate().substring(index + 1) + "  " + Tools.getWeek(mHomeBeanList.get(position).getDate()));
            double num1 = 0;
            double num2 = 0;
            List<HomeItemBean> homeItemBeans = mHomeBeanList.get(position).getHomeItemBeans();
            for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                if (homeItemBeans.get(i1).isFlag()) {//支出
                    num1 += homeItemBeans.get(i1).getMoney();
                } else {//收入
                    num2 += homeItemBeans.get(i1).getMoney();
                }
                //是否为退款
                if(homeItemBeans.get(i1).isRefund()){
                    num1 -= Double.valueOf(homeItemBeans.get(i1).getRefundMoney());
                }
            }
            holder.branch.setText("收:" + num2);
            holder.collect.setText("支:" + num1);
            if (num1 == 0) {
                holder.collect.setVisibility(View.GONE);
            }
            if (num2 == 0) {
                holder.branch.setVisibility(View.GONE);
            }
            holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            holder.mRecyclerView.setAdapter(new MyRecyclerItemAdapter(mHomeBeanList.get(position).getHomeItemBeans()));


            holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    LiveDataBus.liveDataBus.with("setOne",boolean.class).setValue(false);
                    LiveDataBus.liveDataBus.with("calendarIsFlag",boolean.class).setValue(false);
                    LiveDataBus.liveDataBus.with("selectdate", String.class).setValue(mHomeBeanList.get(position).getDate());
                    navController.navigate(R.id.moneyPlus);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mHomeBeanList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView data;
            TextView collect;
            TextView branch;
            RecyclerView mRecyclerView;
            ConstraintLayout mConstraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                data = itemView.findViewById(R.id.home_recycler_data);
                collect = itemView.findViewById(R.id.home_recycler_collect);
                branch = itemView.findViewById(R.id.home_recycler_branch);
                mRecyclerView = itemView.findViewById(R.id.home_recycler_recycler);
                mConstraintLayout = itemView.findViewById(R.id.home_recycler_layout);
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.home_recycler_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                                            PostBean postBean = (PostBean) msg.obj;
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
                    if(homeItemBean.isRefund()){
                        layout1.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.VISIBLE);
                        TextView textViewname = layout.findViewById(R.id.money_item_tuiname);
                        TextView textViewname2 = layout.findViewById(R.id.money_item_tuitime);
                        TextView textViewname3 = layout.findViewById(R.id.money_item_tuimoney);
                        TextView textViewname4 = layout.findViewById(R.id.money_item_tuibeizhu);
                        textViewname.setText("退款·"+homeItemBean.getDescribe());
                        textViewname2.setText(homeItemBean.getRefundTime());
                        textViewname3.setText(homeItemBean.getRefundMoney());
                        if(homeItemBean.getRefundRemarks().isEmpty()){
                            textView4.setVisibility(View.GONE);
                        }else{
                            textViewname4.setText(homeItemBean.getRefundRemarks());
                            textView4.setVisibility(View.VISIBLE);
                        }
                    }else{
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.GONE);
                    }
                    tui.setOnClickListener(v -> {
                        builder.dismiss();
                        LiveDataBus.liveDataBus.with("SelHomeItemBean",HomeItemBean.class).setValue(homeItemBean);
                        NavController navController = Navigation.findNavController(mBinding.getRoot());
                        navController.navigate(R.id.refundInterface);
                    });
                    //修改
                    TextView set = layout.findViewById(R.id.money_item_set);
                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            LiveDataBus.liveDataBus.with("setOne",boolean.class).setValue(true);
                            LiveDataBus.liveDataBus.with("calendarIsFlag",boolean.class).setValue(false);
                            LiveDataBus.liveDataBus.with("SelHomeItemBean",HomeItemBean.class).setValue(homeItemBean);
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

    static class MyViewPagerAdapter extends FragmentStateAdapter {
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

    @Override
    public void onResume() {
        super.onResume();
        //判断用户是否退出
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
        String string = sharedPreferences.getString("name", "无");
        if (string.equals("无")) {
            initData();
        }
    }
}
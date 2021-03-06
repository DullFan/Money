package com.example.money.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.money.R;
import com.example.money.bean.ConsumptionBean;
import com.example.money.bean.HomeBean;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.MoneyBean;
import com.example.money.bean.ReportBean;
import com.example.money.databinding.FragmentMonthlyReportBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.LiveDataBus;
import com.example.money.tools.MyAdapter;
import com.example.money.tools.Tools;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class MonthlyReport extends Fragment {
    private FragmentMonthlyReportBinding mBinding;
    private Integer mMonth;
    private Integer mYear;
    private HttpUtil mHttpUtil;
    private List<HomeBean> mHomeBeanList;
    private List<MoneyBean> mMoneyBeans1;
    private List<MoneyBean> mMoneyBeans2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMonthlyReportBinding.inflate(inflater, container, false);
        mHomeBeanList = new ArrayList<>();
        LiveDataBus.liveDataBus.with("DialogMonthFlag", int.class).setValue(2);
        mHttpUtil = new HttpUtil();
        //?????????????????????
        mMonth = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
        mYear = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
        if (mMonth < 10) {
            mBinding.reportYear.setText(mYear + ".0" + mMonth);
        } else {
            mBinding.reportYear.setText(mYear + "." + mMonth);
        }
        initTitle();
        initData();
        return mBinding.getRoot();
    }

    private void initTitle() {
        mBinding.reportBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.xiala);
        drawable.setBounds(0, 0, 20, 20);
        mBinding.reportYear.setCompoundDrawablePadding(10);
        mBinding.reportYear.setCompoundDrawables(null, null, drawable, null);
        mBinding.reportYear.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getContext(), R.layout.dialog_home_layout, null);
            TabLayout tabLayout = view.findViewById(R.id.dialog_home_tablayout);
            ViewPager2 viewPager = view.findViewById(R.id.dialog_home_viewpager);
            builder.setView(view);
            //???????????????????????????
            initDialogView(tabLayout, viewPager);
            builder.setPositiveButton("??????", (dialog, which) -> {
                String year1 = LiveDataBus.getInstance().with("year2", String.class).getValue();
                if (year1.contains("???")) {
                    LiveDataBus.getInstance().with("moneyYear3", int.class).setValue(
                            Integer.valueOf(LiveDataBus.getInstance().with("year2", String.class).getValue().replace("???", ""))
                    );
                }
                //???
                LiveDataBus.getInstance().with("moneyMonth3", int.class).setValue(
                        LiveDataBus.getInstance().with("month", int.class).getValue()
                );
                Integer month = LiveDataBus.getInstance().with("moneyMonth3", int.class).getValue();
                Integer year = LiveDataBus.getInstance().with("moneyYear3", int.class).getValue();
                if (month < 10) {
                    mBinding.reportYear.setText(year + ".0" + month);
                } else {
                    mBinding.reportYear.setText(year + "." + month);
                }
                initData();
            });
            builder.show();
        });
    }

    private void initDialogView(TabLayout tabLayout, ViewPager2 viewPager) {
        viewPager.setUserInputEnabled(false);
        //??????
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        fragmentList.add(new HomeDiaLog());
        //??????
        List<String> stringList = new ArrayList<>();
        stringList.add("2021???");
        stringList.add("2020???");
        stringList.add("2019???");
        stringList.add("2018???");
        stringList.add("2017???");
        stringList.add("2016???");
        stringList.add("2015???");
        stringList.add("2014???");
        viewPager.setAdapter(new Home.MyViewPagerAdapter(getActivity(), fragmentList));
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
            case "2021???":
                tabLayout.getTabAt(0).select();
                viewPager.setCurrentItem(0, false);
                break;
            case "2020???":
                tabLayout.getTabAt(1).select();
                viewPager.setCurrentItem(1, false);
                break;
            case "2019???":
                tabLayout.getTabAt(2).select();
                viewPager.setCurrentItem(2, false);
                break;
            case "2018???":
                tabLayout.getTabAt(3).select();
                viewPager.setCurrentItem(3, false);
                break;
            case "2017???":
                tabLayout.getTabAt(4).select();
                viewPager.setCurrentItem(4, false);
                break;
            case "2016???":
                tabLayout.getTabAt(5).select();
                viewPager.setCurrentItem(5, false);
                break;
            case "2015???":
                tabLayout.getTabAt(6).select();
                viewPager.setCurrentItem(6, false);
                break;
            case "2014???":
                tabLayout.getTabAt(7).select();
                viewPager.setCurrentItem(7, false);
                break;
        }
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
                    Tools.SnackbarShow(mBinding.getRoot(), "????????????");
                    return;
                }
                //??????
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

                if (mHomeBeanList.size() == 0) {
                    mBinding.reportNodata.setVisibility(View.VISIBLE);
                    mBinding.reportLayout3.setVisibility(View.GONE);
                    mBinding.reportLayout4.setVisibility(View.GONE);
                    mBinding.reportLayout5.setVisibility(View.GONE);
                    mBinding.reportMoney1.setText("0");
                    mBinding.reportMoney2.setText("0");
                    mBinding.reportMoney3.setText("0");
                    mBinding.reportMoney4.setText("0");
                } else {
                    mBinding.reportNodata.setVisibility(View.GONE);
                    mBinding.reportLayout3.setVisibility(View.VISIBLE);
                    mBinding.reportLayout4.setVisibility(View.VISIBLE);
                    mBinding.reportLayout5.setVisibility(View.VISIBLE);
                    //???????????????
                    initMoney();
                    initLayout1();
                    //???????????????
                    initMoney2();
                    //????????????
                    initLayout3();
                    //????????????
                    initLayout4();
                }
            }
        };
        mHttpUtil.httpGet(url, handler, ConsumptionBean.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initLayout4() {
        List<ReportBean>reportBeans = new ArrayList<>();
        for (int i = 0; i < mHomeBeanList.size(); i++) {
            double num1 = 0;
            double num2 = 0;
            List<HomeItemBean> homeItemBeans = mHomeBeanList.get(i).getHomeItemBeans();
            for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                if(homeItemBeans.get(i1).isFlag()){
                    if(!homeItemBeans.get(i1).isRefund()){
                        num1 += homeItemBeans.get(i1).getMoney();
                    }
                }else{
                    num2 +=homeItemBeans.get(i1).getMoney();
                }
            }
            DecimalFormat df = new DecimalFormat("#.00");
            reportBeans.add(new ReportBean(mHomeBeanList.get(i).getDate(), df.format(num1),
                    df.format(num2),df.format(num2+(-num1))));
        }
        reportBeans.sort(new Comparator<ReportBean>() {
            @Override
            public int compare(ReportBean o1, ReportBean o2) {
                Date key1 = Tools.stringToDate(o1.getDate());
                Date key2 = Tools.stringToDate(o2.getDate());
                return key2.compareTo(key1);
            }
        });
        for (int i = 0; i < reportBeans.size(); i++) {
            reportBeans.get(i).setDate(reportBeans.get(i).getDate().substring(5));
        }

        mBinding.reportListview.setAdapter(new MyAdapter<ReportBean>(reportBeans,R.layout.layout_report_listview) {
            @Override
            public void bindView(ViewHolder holder, ReportBean obj, View view, int position) {
                if(obj.getNum2().equals(".00")){
                    holder.setText(R.id.report_list_text2,"0.00");
                }else{
                    holder.setText(R.id.report_list_text2,obj.getNum2()+"");
                }
                if(obj.getNum1().equals(".00")){
                    holder.setText(R.id.report_list_text3,"0.00");
                }else{
                    holder.setText(R.id.report_list_text3,obj.getNum1()+"");
                }
                holder.setText(R.id.report_list_text1,obj.getDate());
                TextView textView = view.findViewById(R.id.report_list_text4);
                if(Double.valueOf(obj.getNum3()) > 0){
                    textView.setTextColor(getResources().getColor(R.color.teal_200));
                }else{
                    textView.setTextColor(getResources().getColor(R.color.red));
                }
                holder.setText(R.id.report_list_text4,obj.getNum3()+"");
            }
        });
    }

    private void initLayout3() {
        double num1 = 0;
        double num2 = 0;
        for (int i = 0; i < mHomeBeanList.size(); i++) {
            List<HomeItemBean> homeItemBeans = mHomeBeanList.get(i).getHomeItemBeans();
            for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                if(homeItemBeans.get(i1).isFlag()){
                    if(!homeItemBeans.get(i1).isRefund()){
                        num1 += homeItemBeans.get(i1).getMoney();
                    }
                }else{
                    num2 +=homeItemBeans.get(i1).getMoney();
                }
            }
        }

        mBinding.reportMoney1.setText(num1+"");
        mBinding.reportMoney2.setText(num2+"");
        mBinding.reportMoney3.setText(num2+(-num1)+"");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String format = decimalFormat.format(num1 / (day));
        String format1 = decimalFormat.format(num2 / (day));
        mBinding.reportMoney4.setText(format);
        mBinding.reportText10.setText("????????????:"+format+" ??????:"+format1);
    }

    private void initMoney2() {
        List<MoneyBean> moneyBeanList = new ArrayList<>();
        List<MoneyBean> moneyBeanList2 = new ArrayList<>();
        List<String> stringList1 = new ArrayList<>();
        List<String> stringList2 = new ArrayList<>();
        for (int i = 0; i < mHomeBeanList.size(); i++) {
            List<HomeItemBean> list = mHomeBeanList.get(i).getHomeItemBeans();
            for (int i1 = 0; i1 < list.size(); i1++) {
                if (list.get(i1).isFlag()) {
                    if (!list.get(i1).isRefund()) {
                        stringList1.add(list.get(i1).getDescribe());
                        //??????
                        moneyBeanList.add(new MoneyBean(list.get(i1).getDescribe(), list.get(i1).getMoney()));
                    }
                } else {
                    stringList2.add(list.get(i1).getDescribe());
                    //??????
                    moneyBeanList2.add(new MoneyBean(list.get(i1).getDescribe(), list.get(i1).getMoney()));
                }
            }
        }
        stringList1 = new ArrayList<>(new HashSet<>(stringList1));
        stringList2 = new ArrayList<>(new HashSet<>(stringList2));
        List<MoneyBean> moneyBeans1 = new ArrayList<>();
        List<MoneyBean> moneyBeans2 = new ArrayList<>();
        for (int i = 0; i < stringList1.size(); i++) {
            double num = 0;
            for (int i1 = 0; i1 < moneyBeanList.size(); i1++) {
                if (stringList1.get(i).equals(moneyBeanList.get(i1).getData())) {
                    num += moneyBeanList.get(i1).getMoney();
                }
            }
            moneyBeans1.add(new MoneyBean(stringList1.get(i), num));
        }

        for (int i = 0; i < stringList2.size(); i++) {
            double num = 0;
            for (int i1 = 0; i1 < moneyBeanList2.size(); i1++) {
                if (stringList2.get(i).equals(moneyBeanList2.get(i1).getData())) {
                    num += moneyBeanList2.get(i1).getMoney();
                }
            }
            moneyBeans2.add(new MoneyBean(stringList2.get(i), num));
        }
        initLayout2(moneyBeans1, moneyBeans2);
    }

    private void initLayout2(List<MoneyBean> moneyBeans1, List<MoneyBean> moneyBeans2) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.teal_200));
        colors.add(getResources().getColor(R.color.purple_200));
        colors.add(getResources().getColor(R.color.purple_500));
        colors.add(getResources().getColor(R.color.teal_700));
        colors.add(getResources().getColor(R.color.black));
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.textcolor));
        colors.add(getResources().getColor(R.color.color1));
        colors.add(getResources().getColor(R.color.color2));
        if (moneyBeans1.size() == 0) {
            mBinding.reportText8.setVisibility(View.VISIBLE);
            mBinding.reportPiechart.setVisibility(View.GONE);
        } else {
            Description description = new Description();
            description.setText("");
            mBinding.reportPiechart.setDescription(description);
            mBinding.reportText8.setVisibility(View.GONE);
            mBinding.reportPiechart.setVisibility(View.VISIBLE);

            Legend l = mBinding.reportPiechart.getLegend();
            l.setEnabled(false);
            mBinding.reportPiechart.setUsePercentValues(true);
            mBinding.reportPiechart.setRotationEnabled(true);
            mBinding.reportPiechart.setCenterText("????????????");
            List<PieEntry> list = new ArrayList<>();
            for (int i = 0; i < moneyBeans1.size(); i++) {
                list.add(new PieEntry((int) moneyBeans1.get(i).getMoney(), moneyBeans1.get(i).getData()));
            }
            PieDataSet pieDataSet = new PieDataSet(list, "");
            //??????????????????????????????
            pieDataSet.setValueLineColor(Color.WHITE);
            //??????????????????????????????
            pieDataSet.setColors(colors);
            PieData pieData = new PieData(pieDataSet);
            pieData.setValueFormatter(new PercentFormatter(mBinding.reportPiechart));
            pieData.setValueTextSize(11f);//????????????
            pieData.setValueTextColor(Color.WHITE);//???????????????
            mBinding.reportPiechart.setData(pieData);
            mBinding.reportPiechart.invalidate();
        }

        if (moneyBeans2.size() == 0) {
            mBinding.reportText7.setVisibility(View.VISIBLE);
            mBinding.reportPiechart2.setVisibility(View.GONE);
        } else {
            mBinding.reportText7.setVisibility(View.GONE);
            mBinding.reportPiechart2.setVisibility(View.VISIBLE);
            mBinding.reportPiechart2.setRotationEnabled(false);
            Description description = new Description();
            description.setText("");
            mBinding.reportPiechart2.setDescription(description);
            Legend l2 = mBinding.reportPiechart2.getLegend();
            l2.setEnabled(false);
            mBinding.reportPiechart2.setUsePercentValues(true);
            mBinding.reportPiechart2.setRotationEnabled(true);
            mBinding.reportPiechart2.setCenterText("????????????");
            List<PieEntry> list2 = new ArrayList<>();
            for (int i = 0; i < moneyBeans2.size(); i++) {
                list2.add(new PieEntry((int) moneyBeans2.get(i).getMoney(), moneyBeans2.get(i).getData()));
            }
            PieDataSet pieDataSet2 = new PieDataSet(list2, "");
            //??????????????????????????????
            pieDataSet2.setValueLineColor(Color.WHITE);
            //??????????????????????????????
            pieDataSet2.setColors(colors);
            PieData pieData2 = new PieData(pieDataSet2);
            pieData2.setValueFormatter(new PercentFormatter(mBinding.reportPiechart2));
            pieData2.setValueTextSize(11f);//????????????
            pieData2.setValueTextColor(Color.WHITE);//???????????????
            mBinding.reportPiechart2.setData(pieData2);
            mBinding.reportPiechart2.invalidate();
        }
        mBinding.reportPiechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mBinding.reportPiechart.setCenterText(moneyBeans1.get((int) h.getX()).getData()
                        + " " + moneyBeans1.get((int) h.getX()).getMoney());
            }

            @Override
            public void onNothingSelected() {
                mBinding.reportPiechart.setCenterText("????????????");
            }
        });
        mBinding.reportPiechart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mBinding.reportPiechart2.setCenterText(moneyBeans2.get((int) h.getX()).getData()
                        + " " + moneyBeans2.get((int) h.getX()).getMoney());
            }

            @Override
            public void onNothingSelected() {
                mBinding.reportPiechart2.setCenterText("????????????");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initMoney() {
        mMoneyBeans1 = new ArrayList<>();
        mMoneyBeans2 = new ArrayList<>();
        for (int i = 0; i < mHomeBeanList.size(); i++) {
            int num1 = 0;
            int num2 = 0;
            List<HomeItemBean> homeItemBeans = mHomeBeanList.get(i).getHomeItemBeans();
            for (int i1 = 0; i1 < homeItemBeans.size(); i1++) {
                if (homeItemBeans.get(i1).isFlag()) {
                    //??????
                    if (!homeItemBeans.get(i1).isRefund()) {
                        num1 += homeItemBeans.get(i1).getMoney();
                    }
                } else {
                    //??????
                    num2 += homeItemBeans.get(i1).getMoney();
                }
            }
            mMoneyBeans1.add(new MoneyBean(mHomeBeanList.get(i).getDate(), num1));
            mMoneyBeans2.add(new MoneyBean(mHomeBeanList.get(i).getDate(), num2));
        }

        mMoneyBeans1.sort(new Comparator<MoneyBean>() {
            @Override
            public int compare(MoneyBean o1, MoneyBean o2) {
                Date key1 = Tools.stringToDate(o1.getData());
                Date key2 = Tools.stringToDate(o2.getData());
                return key1.compareTo(key2);
            }
        });

        mMoneyBeans2.sort(new Comparator<MoneyBean>() {
            @Override
            public int compare(MoneyBean o1, MoneyBean o2) {
                Date key1 = Tools.stringToDate(o1.getData());
                Date key2 = Tools.stringToDate(o2.getData());
                return key1.compareTo(key2);
            }
        });

        for (int i = 0; i < mMoneyBeans1.size(); i++) {
            String date = mMoneyBeans1.get(i).getData().substring(5).replace("-", ".");
            mMoneyBeans1.get(i).setData(date);
        }
        for (int i = 0; i < mMoneyBeans2.size(); i++) {
            String date = mMoneyBeans2.get(i).getData().substring(5).replace("-", ".");
            mMoneyBeans2.get(i).setData(date);
        }

        mMoneyBeans1.add(0, new MoneyBean("", 0f));
        mMoneyBeans2.add(0, new MoneyBean("", 0f));
    }

    private void initLayout1() {
        mBinding.reportLinechart.setScaleEnabled(false);// ??????????????????
        mBinding.reportLinechart.setDoubleTapToZoomEnabled(false);//?????????????????????
        //???????????????
        mBinding.reportLinechart.setDescription(null);
        //??????????????????X???
        XAxis xAxis = mBinding.reportLinechart.getXAxis();
        //?????????????????????Y???  ????????????
        YAxis axisLeft = mBinding.reportLinechart.getAxisLeft();
        YAxis axisRight = mBinding.reportLinechart.getAxisRight();
        axisRight.setEnabled(false);
        //??????X?????????????????????
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //??????X????????????
        xAxis.setAxisLineWidth(1);
        xAxis.setAxisLineColor(Color.BLACK);
        //??????0????????????
        xAxis.setAxisMinimum(1);
        //??????X???????????????
        xAxis.setDrawAxisLine(true);
        //x?????????????????????
        xAxis.setDrawGridLines(false);
        //??????X?????????
        xAxis.setEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(mMoneyBeans1.size(), false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if ((int) value < mMoneyBeans1.size()) {
                    return mMoneyBeans1.get((int) value).getData();
                } else {
                    return "";
                }
            }
        });

        //y???0??????
        axisLeft.setAxisMinimum(0);
        //???????????????
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisLineColor(Color.BLACK);
        //??????Y?????????
        axisLeft.setDrawAxisLine(true);
        axisLeft.setAxisLineWidth(1);
        axisLeft.setEnabled(true);
        axisLeft.setDrawLabels(true);
        //X?????????0-6
        List<Entry> mListEnryMin = new ArrayList<>();

        for (int i = 0; i < mMoneyBeans1.size(); i++) {
            //??????x,y????????????
            mListEnryMin.add(new Entry(i, (float) mMoneyBeans1.get(i).getMoney()));
        }

        List<Entry> mListEnryMin2 = new ArrayList<>();

        for (int i = 0; i < mMoneyBeans2.size(); i++) {
            //??????x,y????????????
            mListEnryMin2.add(new Entry(i, (float) mMoneyBeans2.get(i).getMoney()));
        }

        LineDataSet barDataSet = new LineDataSet(mListEnryMin, "??????");
        LineDataSet barDataSet2 = new LineDataSet(mListEnryMin2, "??????");
        barDataSet.setColors(Color.parseColor("#ff0000"));
        barDataSet2.setColors(getResources().getColor(R.color.teal_200));

        barDataSet.setCircleColor(Color.parseColor("#ff0000"));
        barDataSet2.setCircleColor(getResources().getColor(R.color.teal_200));
        //???????????????????????????????????????
        barDataSet.setValueTextSize(12);
        LineData lineData = new LineData(barDataSet, barDataSet2);
        lineData.setDrawValues(true);
        mBinding.reportLinechart.setData(lineData);
        mBinding.reportLinechart.invalidate();
        //????????????
        mBinding.reportLinechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}
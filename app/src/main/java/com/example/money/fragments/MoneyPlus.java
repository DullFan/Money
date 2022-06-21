package com.example.money.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.money.R;
import com.example.money.bean.HomeItemBean;
import com.example.money.bean.MoneyPlusViewPagerBean;
import com.example.money.bean.PostBean;
import com.example.money.databinding.FragmentMoneyPlusBinding;
import com.example.money.tools.HttpUtil;
import com.example.money.tools.LiveDataBus;
import com.example.money.tools.MyViewModel;
import com.example.money.tools.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoneyPlus extends Fragment {
    FragmentMoneyPlusBinding mBinding;
    private MyGridViewAdapter mAdapter;
    private List<String> mList;
    private MyViewModel mModel;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMoneyPlusBinding.inflate(inflater, container, false);
        mModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        initTabLayout();
        if (LiveDataBus.liveDataBus.with("setOne", boolean.class).getValue()) {
            HomeItemBean selHomeItemBean = LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).getValue();
            mBinding.layoutTitle.setVisibility(View.VISIBLE);
            mBinding.moneyTablayout.setVisibility(View.INVISIBLE);
            mBinding.moneyViewpager.setUserInputEnabled(false);
            if (selHomeItemBean.isFlag()) {
                //支出
                mBinding.moneyViewpager.setCurrentItem(0, false);
            } else {
                mBinding.moneyViewpager.setCurrentItem(1, false);
            }
            mBinding.moneyText.setText(selHomeItemBean.getMoney() + "");
        }
        initBack();
        initInput();
        initGridView();
        //点击事件
        initGridViewItem();
        //设置选择日期
        initYear();
        return mBinding.getRoot();
    }

    private void initYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String selectdate = LiveDataBus.liveDataBus.with("selectdate", String.class).getValue().replace("-", ".");
        if (!selectdate.equals("无")) {
            date = selectdate;
            Integer moneyYear = LiveDataBus.getInstance().with("moneyYear", int.class).getValue();
            if (date.contains(moneyYear + "")) {
                String replace = date.replace(moneyYear + ".", "");
                String replace2 = (month + 1) + "." + day;
                if (replace2.equals(replace)) {
                    mBinding.moneyDay.setText("今天");
                } else {
                    mBinding.moneyDay.setText(replace);
                }
            } else {
                mBinding.moneyDay.setText(date);
            }
        } else {
            date = year + "." +
                    (month + 1) + "." +
                    day;
        }

        if(LiveDataBus.liveDataBus.with("calendarIsFlag",boolean.class).getValue()){
            date = LiveDataBus.liveDataBus.with("calendarDate",String.class).getValue();
            mBinding.moneyDay.setText(date);

        }

        if (LiveDataBus.liveDataBus.with("setOne", boolean.class).getValue()) {
            HomeItemBean selHomeItemBean = LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).getValue();
            date = selHomeItemBean.getTime().replace("-", ".");
            mBinding.moneyDay.setText(selHomeItemBean.getTime());
        }
        mBinding.moneyDay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.MyDatePickerDialogTheme, (view, year1, month1, dayOfMonth) -> {
                    date = year1 + "." + (month1 + 1) + "." + dayOfMonth;
                    String date2 = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    if (year1 == year
                            && (month1 + 1) == (month + 1) &&
                            dayOfMonth == day) {
                        mBinding.moneyDay.setText("今天");
                    } else if (year1 == LiveDataBus.getInstance().with("moneyYear", int.class).getValue()) {
                        String s = (month1 + 1) + "-" + dayOfMonth;
                        mBinding.moneyDay.setText(s);
                    } else {
                        mBinding.moneyDay.setText(date2);
                    }
                }, year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (3650 * 70 * 90 * 24));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 207391164681L);
                dialog.show();
            }
        });
    }

    private void initGridViewItem() {
        //单击事件
        mBinding.moneyGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = mBinding.moneyText;
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 4:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 13:
                        if (textView.getText().toString().equals("0.0")) {
                            textView.setText("");
                        }
                        if (textView.getText().toString().length() >= 15) {
                            Snackbar.make(view, "输入数字太大了", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (textView.getText().toString().contains(".")) {
                            int index = textView.getText().toString().indexOf(".");
                            String substring = textView.getText().toString().substring(index + 1);
                            if (substring.length() == 2 && !substring.contains("+")) {
                                return;
                            }
                            if (substring.length() == 2 && !substring.contains("-")) {
                                return;
                            }
                        }

                        textView.setText(textView.getText().toString() + mList.get(position));

                        //加法
                        if (textView.getText().toString().contains("+")) {
                            int jiaIndex = textView.getText().toString().indexOf("+");
                            String jiaString2 = textView.getText().toString().substring(jiaIndex + 1);
                            if (jiaString2.contains(".")) {
                                int dianIndex2 = jiaString2.indexOf(".");
                                String jiaSubString2 = jiaString2.substring(dianIndex2 + 1);
                                if (jiaSubString2.length() == 3) {
                                    String substring = textView.getText().toString().substring(0, textView.getText().toString().length() - 1);
                                    textView.setText(substring);
                                    return;
                                }
                            }
                        }

                        //减法
                        if (textView.getText().toString().contains("-")) {
                            int jianIndex = textView.getText().toString().indexOf("-");
                            String jiaString2 = textView.getText().toString().substring(jianIndex + 1);
                            if (jiaString2.contains(".")) {
                                int dianIndex2 = jiaString2.indexOf(".");
                                String jianSubString2 = jiaString2.substring(dianIndex2 + 1);
                                if (jianSubString2.length() == 3) {
                                    String substring = textView.getText().toString()
                                            .substring(0, textView.getText().toString().length() - 1);
                                    textView.setText(substring);
                                    return;
                                }
                            }
                        }
                        break;
                    case 3:
                        String s = textView.getText().toString();
                        if (s.equals("0.0")) {
                            return;
                        }
                        String s1 = s.substring(0, s.length() - 1);
                        textView.setText(s1);
                        if (textView.getText().toString().isEmpty()) {
                            textView.setText("0.0");
                        }
                        break;
                    case 7:
                        String string = textView.getText().toString();
                        if (string.equals("0.0")) {
                            return;
                        }
                        if (string.contains("+")) {
                            return;
                        }
                        if (string.contains("-")) {
                            int index = string.indexOf("-");
                            if (string.contains(".")) {
                                double aDouble1 = Double.valueOf(string.substring(0, index));
                                if (string.substring(index + 1).isEmpty()) {
                                    return;
                                }
                                if (string.substring(index + 1).equals(".")) {
                                    return;
                                }
                                double aDouble2 = Double.valueOf(string.substring(index + 1));
                                if (aDouble2 > aDouble1) {
                                    textView.setText("0.0");
                                    return;
                                }
                                textView.setText((aDouble1 - aDouble2) + "-");
                            } else {
                                int aDouble1 = Integer.parseInt(string.substring(0, index));
                                if (string.substring(index + 1).isEmpty()) {
                                    return;
                                }
                                if (string.substring(index + 1).equals(".")) {
                                    return;
                                }
                                int aDouble2 = Integer.parseInt(string.substring(index + 1));
                                if (aDouble2 > aDouble1) {
                                    textView.setText("0.0");
                                    return;
                                }
                                textView.setText((aDouble1 - aDouble2) + "-");
                            }
                        } else {
                            textView.setText(string + "-");
                        }
                        break;
                    case 11:
                        String string2 = textView.getText().toString();
                        if (string2.equals("0.0")) {
                            return;
                        }
                        if (string2.contains("-")) {
                            return;
                        }
                        if (string2.contains("+")) {
                            int index = string2.indexOf("+");
                            if (string2.contains(".")) {
                                double aDouble1 = Double.valueOf(string2.substring(0, index));
                                if (string2.substring(index + 1).isEmpty()) {
                                    return;
                                }
                                if (string2.substring(index + 1).equals(".")) {
                                    return;
                                }
                                double aDouble2 = Double.valueOf(string2.substring(index + 1));
                                textView.setText((aDouble1 + aDouble2) + "+");
                            } else {
                                int aDouble1 = Integer.parseInt(string2.substring(0, index));
                                if (string2.substring(index + 1).isEmpty()) {
                                    return;
                                }
                                if (string2.substring(index + 1).equals(".")) {
                                    return;
                                }
                                int aDouble2 = Integer.parseInt(string2.substring(index + 1));
                                textView.setText((aDouble1 + aDouble2) + "+");
                            }
                        } else {
                            textView.setText(string2 + "+");
                        }
                        break;
                    case 12:
                        if (textView.getText().toString().equals("0.0") || textView.getText().toString().equals("0.")) {
                            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.money_layout_text);
                            textView.startAnimation(animation);
                            return;
                        }
                        initItem(view, 1);
                        if (LiveDataBus.getInstance().with("asdasd", boolean.class).getValue()) {
                            return;
                        }
                        textView.setText("0.0");
                        break;
                    case 14:
                        String str = textView.getText().toString();
                        if (str.equals("0.0")) {
                            textView.setText("0.");
                            return;
                        }
                        if (str.contains("+")) {
                            int index = str.indexOf("+");
                            String substring = str.substring(index + 1);
                            if (substring.contains(".")) {
                                return;
                            }
                            textView.setText(str + ".");
                        }

                        if (str.contains("-")) {
                            int index = str.indexOf("-");
                            String substring = str.substring(index + 1);
                            if (substring.contains(".")) {
                                return;
                            }
                            textView.setText(str + ".");
                        }

                        if (str.contains(".")) {
                            return;
                        }

                        textView.setText(str + ".");
                        break;
                }
            }
        });
        //长按事件
        mBinding.moneyGridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    mBinding.moneyText.setText("0.0");
                }
                return false;
            }
        });
    }

    private void initGridView() {
        mList = new ArrayList<>();
        mList.add("1");
        mList.add("2");
        mList.add("3");
        mList.add("");
        mList.add("4");
        mList.add("5");
        mList.add("6");
        mList.add("-");
        mList.add("7");
        mList.add("8");
        mList.add("9");
        mList.add("+");
        mList.add("再记");
        mList.add("0");
        mList.add("·");
        mList.add("保存");
        mAdapter = new MyGridViewAdapter(mList);
        mBinding.moneyGridview.setAdapter(mAdapter);
    }

    private void initInput() {
        LiveDataBus.getInstance().with("tabLayoutSelect", String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("支出")) {
                    mAdapter.setIndex(0);
                } else {
                    mAdapter.setIndex(1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initBack() {
        mBinding.layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });
    }

    private void initTabLayout() {
        List<String> mStringList = new ArrayList<>();
        mStringList.add("支出");
        mStringList.add("收入");
        List<Fragment> list = new ArrayList<>();
        List<MoneyPlusViewPagerBean> beanList1 = new ArrayList<>();
        List<MoneyPlusViewPagerBean> beanList2 = new ArrayList<>();
        //设置数据
        initData(beanList1, beanList2);
        list.add(new MoneyPlusViewPager(beanList1));
        list.add(new MoneyPlusViewPager(beanList2));
        mBinding.moneyViewpager.setAdapter(new MyFragmentAdapter(getActivity(), list));
        LiveDataBus.getInstance().with("tabLayoutSelect", String.class).setValue(mStringList.get(0));
        new TabLayoutMediator(mBinding.moneyTablayout, mBinding.moneyViewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mStringList.get(position));
            }
        }).attach();
        mBinding.moneyTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LiveDataBus.getInstance().with("tabLayoutSelect", String.class).setValue(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initData(List<MoneyPlusViewPagerBean> beanList1, List<MoneyPlusViewPagerBean> beanList2) {
        beanList1.add(new MoneyPlusViewPagerBean("三餐", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("零食", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("衣服", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("交通", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("旅游", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("孩子", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("宠物", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("通讯", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("烟酒", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("学习", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("日用", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("住房", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("美妆", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("医疗", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("礼金", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("娱乐", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("请客", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("数码", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("运动", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("办公", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("其他", R.drawable.gridimg2));
        beanList1.add(new MoneyPlusViewPagerBean("管理", R.drawable.gridimg2));



        beanList2.add(new MoneyPlusViewPagerBean("工资", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("兼职", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("礼金", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("股票", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("基金", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("中奖", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("营业", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("其他", R.drawable.gridimg2));
        beanList2.add(new MoneyPlusViewPagerBean("管理", R.drawable.gridimg2));
    }

    class MyFragmentAdapter extends FragmentStateAdapter {
        List<Fragment> mFragmentList;

        public MyFragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> mFragmentList) {
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

    class MyGridViewAdapter extends BaseAdapter {
        List<String> mStringList;
        int index = 0;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public MyGridViewAdapter(List<String> stringList) {
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
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.layout_money_gridview, null);
                viewHolder.img = convertView.findViewById(R.id.money_gridview_img);
                viewHolder.name = convertView.findViewById(R.id.money_gridview_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 3) {
                viewHolder.img.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.GONE);
            } else {
                viewHolder.img.setVisibility(View.GONE);
                viewHolder.name.setVisibility(View.VISIBLE);
            }

            viewHolder.name.setText(mStringList.get(position));
            if (mStringList.get(position).equals("再记")) {
                viewHolder.name.setTextSize(13);
            } else {
                viewHolder.name.setTextSize(20);
            }
            if (position == mStringList.size() - 1) {
                if (index == 0) {
                    convertView.setClickable(true);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView textView = mBinding.moneyText;
                            if (textView.getText().toString().equals("0.0") || textView.getText().toString().equals("0.")) {
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.money_layout_text);
                                textView.startAnimation(animation);
                                return;
                            }
                            initItem(v, 0);
                        }
                    });
                    convertView.setBackgroundResource(R.drawable.money_layout_item);
                    mBinding.moneyText.setTextColor(getResources().getColor(R.color.red));
                } else {
                    convertView.setClickable(true);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView textView = mBinding.moneyText;
                            if (textView.getText().toString().equals("0.0") || textView.getText().toString().equals("0.")) {
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.money_layout_text);
                                textView.startAnimation(animation);
                                return;
                            }
                            initItem(v, 0);
                        }
                    });
                    convertView.setBackgroundResource(R.drawable.money_layout_item2);
                    mBinding.moneyText.setTextColor(getResources().getColor(R.color.teal_200));
                }
                viewHolder.name.setTextSize(13);
                viewHolder.name.setTextColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            ImageView img;
        }
    }

    private void initItem(View v, int back) {
        HttpUtil httpUtil = new HttpUtil();
        TextView textView = mBinding.moneyText;
        double money;
        if (textView.getText().toString().contains("+")) {
            LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(false);
            int index = textView.getText().toString().indexOf("+");
            double aDouble1 = Double.valueOf(textView.getText().toString().substring(0, index));
            if (textView.getText().toString().substring(index + 1).isEmpty()) {
                money = aDouble1;
            } else {
                double aDouble2 = Double.valueOf(textView.getText().toString().substring(index + 1));
                money = aDouble1 + aDouble2;
            }
        } else if (textView.getText().toString().contains("-")) {
            LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(false);
            int index = textView.getText().toString().indexOf("-");
            double aDouble1 = Double.valueOf(textView.getText().toString().substring(0, index));
            if (textView.getText().toString().substring(index + 1).isEmpty()) {
                money = aDouble1;
            } else {
                double aDouble2 = Double.valueOf(textView.getText().toString().substring(index + 1));
                if (aDouble2 > aDouble1) {
                    textView.setText("0.0");
                    LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(true);
                    return;
                }
                LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(false);
                money = aDouble1 - aDouble2;
            }
        } else {
            money = Double.valueOf(textView.getText().toString());
            LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(false);
        }
        if (money == 0) {
            LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(true);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.money_layout_text);
            textView.startAnimation(animation);
            return;
        }
        if (money > 20000) {
            Snackbar.make(mBinding.getRoot(), "目前不支持记录20k以上的消费记录", Snackbar.LENGTH_LONG).show();
            LiveDataBus.getInstance().with("asdasd", boolean.class).setValue(true);
            return;
        }
        String describe = LiveDataBus.getInstance().with("classification", String.class).getValue();
        String remarks = mBinding.moneyEdittext.getText().toString();
        String flag;
        String str = LiveDataBus.getInstance().with("tabLayoutSelect", String.class).getValue();
        if (str.equals("支出")) {
            flag = "1";
        } else {
            flag = "0";
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                PostBean postBean = (PostBean) msg.obj;
                if (postBean.getMsg().equals("500")) {
                    if (LiveDataBus.liveDataBus.with("setOne", boolean.class).getValue()) {
                        Tools.SnackbarShow(mBinding.getRoot(), "修改失败");
                    }else{
                        Tools.SnackbarShow(mBinding.getRoot(), "添加失败");
                    }
                } else {
                    if (back == 0) {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigateUp();
                    } else {
                        Tools.SnackbarShow(mBinding.getRoot(), "保存成功");
                    }
                }
            }
        };
        if (LiveDataBus.liveDataBus.with("setOne", boolean.class).getValue()) {
            //修改金额
            String utl = "/consumption/setmoney";
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            String data = date.replace(".", "-");
            HomeItemBean selHomeItemBean = LiveDataBus.liveDataBus.with("SelHomeItemBean", HomeItemBean.class).getValue();
            map.put("conDate", selHomeItemBean.getRecordTime());
            map.put("conDissipate", data);
            map.put("conMoney", money);
            map.put("conRecord", describe);
            map.put("conRemarks", remarks);
            String s = gson.toJson(map);
            httpUtil.okhttpPut(utl, s, handler, PostBean.class);
        }else{
            String utl = "/consumption/addOne";
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logindata", getContext().MODE_PRIVATE);
            int id = sharedPreferences.getInt("id", 0);
            map.put("conUserid", id);
            String data = date.replace(".", "-");
            map.put("conDissipate", data);
            map.put("conMoney", money);
            map.put("conConsume", flag);
            map.put("conRecord", describe);
            map.put("conRemarks", remarks);
            String s = gson.toJson(map);
            httpUtil.httpPost(utl, s, handler, PostBean.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //消除焦点
        mBinding.moneyEdittext.clearFocus();
    }
}
package com.city.trash.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.bean.LoginBean;
import com.city.trash.common.ActivityCollecter;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerRuleListComponent;
import com.city.trash.di.module.RuleListModule;
import com.city.trash.presenter.RuleListPresenter;
import com.city.trash.presenter.contract.RuleListContract;
import com.city.trash.ui.adapter.DrawerListAdapter;
import com.city.trash.ui.adapter.DrawerListContent;
import com.city.trash.ui.adapter.MyFragmentPagerAdapter;
import com.city.trash.ui.fragment.BaseFragment;
import com.city.trash.ui.fragment.HomeFragment;

import java.util.List;

/**
 *
 */
public class MainActivity extends BaseActivity<RuleListPresenter> implements RuleListContract.FeeRuleView {
    public static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private String[] mOptionTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View headerView;
    private View mNetWorkTips;
    public LoginBean mUserInfo;

    private int pressKey;
    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;

    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;
    private RadioButton rb_message;
    private RadioButton rb_better;
    private RadioButton rb_setting;
    private ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    //退出时的时间
    private long mExitTime;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerRuleListComponent.builder().appComponent(appComponent).ruleListModule(new RuleListModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        initview();
        selectItem(0);
    }

    //网络检测
    @Override
    protected void handleNetWorkTips(boolean has) {
        if (has) {
            mNetWorkTips.setVisibility(View.GONE);
        } else {
            mNetWorkTips.setVisibility(View.VISIBLE);
        }
    }

    private void initview() {
        mOptionTitles = getResources().getStringArray(R.array.options_array);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        //官方导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header, mDrawerList, false);
        mDrawerList.addHeaderView(headerView);
        TextView mTxt_username = headerView.findViewById(R.id.txt_username);
        mTxt_username.setText(ACache.get(AppApplication.getApplication()).getAsString("username"));

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_list_item, DrawerListContent.ITEMS));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mNetWorkTips = findViewById(R.id.network_view);
        mNetWorkTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_channel.setSelected(true);

        mPresenter.feeRule();
    }

    //租赁
    public void isReceiving(View view) {
        vpager.setCurrentItem(PAGE_TWO);
    }

    //退还
    public void isGrounding(View view) {
        vpager.setCurrentItem(PAGE_THREE);
    }

    //设置
    public void isSetting(View view) {
        vpager.setCurrentItem(PAGE_FOUR);
    }

    //退出
    public void isBacking(View view){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    //报废登记
    public void isPicking(View view){
        startActivity(new Intent(MainActivity.this, PickActivity.class));
    }

    //扫码查询
    public void isFinding(View view){
        startActivity(new Intent(MainActivity.this,FindActivity.class));
    }

    @Override
    public void feeRuleResult(BaseBean<List<FeeRule>> baseBean) {
        if (baseBean==null){
            ToastUtil.toast("获取押金失败");
            finish();
        }
        if (baseBean.getCode()==0 && baseBean.getData()!=null){
                ACache.get(AppApplication.getApplication()).put("feeRule", baseBean);
        }else {
            ToastUtil.toast(baseBean.getMessage());
            finish();
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("获取押金失败");
    }

    /**
     * The click listener for ListView in the navigation drawer
     * 点击左侧抽屉的item
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            if (position == 0)//headView click
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                if (!NetUtils.isConnected(MainActivity.this)) {
                    ToastUtil.toast(R.string.error_network_unreachable);
                    return;
                }
                selectItem(position);
            }
        }
    }

    public void selectItem(int position) {
        // update the no_items content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = HomeFragment.newInstance();
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
        }
        if (fragment == null) {
            return;
        }
        replaceFragment(position, fragment);
    }

    public void replaceFragment(int position, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            //addToBackStack()对应的是popBackStack()
            //popBackStack(String name, int flag)：name为addToBackStack(String name)的参数，
            // 通过name能找到回退栈的特定元素，flag可以为0或者FragmentManager.POP_BACK_STACK_INCLUSIVE，
            // 0表示只弹出该元素以上的所有元素，POP_BACK_STACK_INCLUSIVE表示弹出包含该元素及以上的所有元素。
            // 这里说的弹出所有元素包含回退这些事务
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.vpager, fragment, TAG_CONTENT_FRAGMENT).commit();
        } else {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.vpager, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
        }

        mDrawerList.setItemChecked(position, true);//高亮选中项
        setTitle(mOptionTitles[position]);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && position == 0) {
            actionBar.setHomeAsUpIndicator(R.mipmap.button_daohang);
        } else {
            actionBar.setHomeAsUpIndicator(null);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void bindViews() {
        rg_tab_bar = findViewById(R.id.rg_tab_bar);
        rb_channel = findViewById(R.id.rb_channel);
        rb_message = findViewById(R.id.rb_message);
        rb_better = findViewById(R.id.rb_better);
        rb_setting = findViewById(R.id.rb_setting);
        rg_tab_bar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_channel:
                        vpager.setCurrentItem(PAGE_ONE);
                        setTitle(mOptionTitles[0]);
                        break;
                    case R.id.rb_message:
                        vpager.setCurrentItem(PAGE_TWO);
                        setTitle(mOptionTitles[1]);
                        break;
                    case R.id.rb_better:
                        vpager.setCurrentItem(PAGE_THREE);
                        setTitle(mOptionTitles[2]);
                        break;
                    case R.id.rb_setting:
                        vpager.setCurrentItem(PAGE_FOUR);
                        setTitle(mOptionTitles[5]);
                        break;
                }
            }
        });

        vpager = findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //state的状态有三种，0表示什么都没做，1正在滑动，2滑动完毕
                if (state == 2) {
                    switch (vpager.getCurrentItem()) {
                        case PAGE_ONE:
                            reselected();
                            setTitle(mOptionTitles[0]);
                            rb_channel.setSelected(true);
                            break;
                        case PAGE_TWO:
                            reselected();
                            setTitle(mOptionTitles[1]);
                            rb_message.setSelected(true);
                            break;
                        case PAGE_THREE:
                            reselected();
                            setTitle(mOptionTitles[2]);
                            rb_better.setSelected(true);
                            break;
                        case PAGE_FOUR:
                            reselected();
                            setTitle(mOptionTitles[5]);
                            rb_setting.setSelected(true);
                            break;
                    }
                }
            }
        });
    }

    private void reselected() {
        rb_setting.setSelected(false);
        rb_message.setSelected(false);
        rb_better.setSelected(false);
        rb_channel.setSelected(false);
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(makeFragmentName(vpager.getId(), vpager.getCurrentItem()));
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ((BaseFragment) fragment).myOnKeyDwon();
            }
        }else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(makeFragmentName(vpager.getId(), vpager.getCurrentItem()));
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ((BaseFragment) fragment).myOnKeyUp();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            ActivityCollecter.finishAll();
        }
    }

   /* @Override
    public void onBackPressed() {
        mDrawerList.setItemChecked(0, true);
        setTitle(mOptionTitles[0]);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.button_daohang);
        }
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    //点击左侧按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                if (fragment != null)
                {
                    if (fragment instanceof HomeFragment)
                    {
                        if (mDrawerLayout.isDrawerVisible(mDrawerList))
                        {
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else
                        {
                            mDrawerLayout.openDrawer(mDrawerList);
                        }
                    } else
                    {
                        if(isSoftShowing())
                        {
                            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        //this.onBackPressed();
                    }
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        // 获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom != 0;
    }
}




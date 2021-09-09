package com.city.trash.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.common.ScreenUtils;
import com.city.trash.common.SystemUtil;
import com.city.trash.common.download.LoadingService;
import com.city.trash.common.download.Utils;
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
import com.city.trash.ui.fragment.*;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.rscja.deviceapi.RFIDWithUHFUART;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends BaseActivity<RuleListPresenter> implements RuleListContract.FeeRuleView, HomeFragment.CallBackValue {
    public static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private String[] mOptionTitles;
    private int[] mBitMaps;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mleftLin;
    private TextView mVersionCode;
    private View headerView;
    private View mNetWorkTips;
    private ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    //退出时的时间
    private long mExitTime;
    private BaseBean<FeeRule> baseBean;

    private HomeFragment myFragment1 = null;
    private LeaseFragment myFragment2 = null;
    private ReturnFragment myFragment3 = null;
    private SettingFragment myFragment4 = null;
    private List<Fragment> fragments;
    private List<String> titleList;
    private List<Integer> picList;
    private TabLayout tablayout;
    private boolean isLoading;
    private MyReceive myReceive;
    private String path;
    public static RFIDWithUHFUART mReader; //RFID扫描

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
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.loading_over");
        filter.addAction("android.intent.action.loading");
        registerReceiver(myReceive, filter);//注册广播

        mPresenter.feeRule(1);

        initview();
        selectItem(0);
        initUHF();
    }

    /**
     * �豸�ϵ��첽��
     *
     * @author liuruifeng
     */
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();

            if (!result) {
                ToastUtil.toast("初始化失败");
            } else {
                ToastUtil.toast("初始化成功");
                String key1 = ACache.get(AppApplication.getApplication()).getAsString("key1");
                if (TextUtils.isEmpty(key1)) {
                    key1 = "10";
                }
                mReader.setPower(Integer.valueOf(key1));
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    //初始化RFID扫描
    public void initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            ToastUtil.toast(ex.getMessage());
            return;
        }
        if (mReader != null) {
            new InitTask().execute();
        }
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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mleftLin = findViewById(R.id.left_lin);
        mVersionCode = findViewById(R.id.tv_versionCode);

        //官方导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header, mDrawerList, false);
        mDrawerList.addHeaderView(headerView);
        TextView mTxt_username = headerView.findViewById(R.id.txt_username);
        mTxt_username.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_list_item, DrawerListContent.ITEMS));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        try {
            mVersionCode.setText("当前版本号：" + Utils.getVersionName(this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //网络检测
        mNetWorkTips = findViewById(R.id.network_view);
        mNetWorkTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });

        myFragment1 = new HomeFragment();
        myFragment2 = new LeaseFragment();
        myFragment3 = new ReturnFragment();
        myFragment4 = new SettingFragment();
        fragments = new ArrayList<>();
        fragments.add(myFragment1);
        fragments.add(myFragment2);
        fragments.add(myFragment3);
        fragments.add(myFragment4);
        mOptionTitles = getResources().getStringArray(R.array.options_array);
        titleList = new ArrayList<>();
        titleList.add(mOptionTitles[0]);
        titleList.add(mOptionTitles[1]);
        titleList.add(mOptionTitles[2]);
        titleList.add(mOptionTitles[5]);
        mBitMaps = new int[]{R.mipmap.home, R.mipmap.zulin,
                R.mipmap.back, R.mipmap.set};
        picList = new ArrayList<>();
        picList.add(mBitMaps[0]);
        picList.add(mBitMaps[1]);
        picList.add(mBitMaps[2]);
        picList.add(mBitMaps[3]);

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, titleList);

        tablayout = findViewById(R.id.tablayout);
        vpager = findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
                if (fragment == myFragment2) {
                    setTitle(mOptionTitles[1]);
                } else if (fragment == myFragment3) {
                    setTitle(mOptionTitles[2]);
                } else if (fragment == myFragment4) {
                    setTitle(mOptionTitles[5]);
                } else {
                    setTitle(mOptionTitles[0]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void SendMessageValue(String strValue) {
        if (strValue.equals("租赁")) {
            vpager.setCurrentItem(1);
        } else if (strValue.equals("退还")) {
            if (fragments.size() == 4) {
                vpager.setCurrentItem(2);
            } else {
                vpager.setCurrentItem(1);
            }
        } else if (strValue.equals("设置")) {
            if (fragments.size() == 4) {
                vpager.setCurrentItem(3);
            } else if (fragments.size() == 3) {
                vpager.setCurrentItem(2);
            } else {
                vpager.setCurrentItem(1);
            }
        } else if (strValue.equals("退出")) {
            finish();
        } else if (strValue.equals("报废登记")) {
            startActivity(new Intent(MainActivity.this, PickActivity.class));
        } else if (strValue.equals("扫码查询")) {
            startActivity(new Intent(MainActivity.this, FindActivity.class));
        } else if (strValue.equals("绑定二维码")){
            startActivity(new Intent(MainActivity.this, BindActivity.class));
        }
    }

    @Override
    public void feeRuleResult(BaseBean<FeeRule> baseBean1) {
        if (baseBean1 == null) {
            ToastUtil.toast("获取押金失败");
            finish();
        }
        if (baseBean1.getCode() == 0 && baseBean1.getData() != null) {
            ACache.get(AppApplication.getApplication()).put("feeRule", baseBean1);

            baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
            if (baseBean == null) {
                return;
            }
            List<String> list = new ArrayList<>();
            for (int i = 0; i < baseBean.getData().getPadMenus().size(); i++) {
                String id = baseBean.getData().getPadMenus().get(i).getId();
                list.add(id);
            }
            if (!list.contains("1")) {
                fragments.remove(myFragment2);
                titleList.remove(mOptionTitles[1]);
                picList.remove(1);
            }
            if (!list.contains("2")) {
                fragments.remove(myFragment3);
                titleList.remove(mOptionTitles[2]);
                picList.remove(2);
            }
            mAdapter.notifyDataSetChanged();

            tablayout.setupWithViewPager(vpager);
            //获取当前tab数量
            int tabCount = tablayout.getTabCount();
            //遍历循环tab数量
            for (int i = 0; i < tabCount; i++) {
                //获取每个tab
                TabLayout.Tab tab = tablayout.getTabAt(i);
                View view = View.inflate(this, R.layout.tab_view, null);
                ImageView iv = view.findViewById(R.id.iv);
                TextView tv = view.findViewById(R.id.tv);
                tv.setText(titleList.get(i));
                iv.setImageResource(picList.get(i));
                //给tab设置view
                tab.setCustomView(view);
            }
            if (baseBean.getData().getAutoUpdateInfo() != null && baseBean.getData().getAutoUpdateInfo().getVersion() != null) {
                getVersionInfoFromServer();
            }

        } else {
            ToastUtil.toast(baseBean1.getMessage());
            finish();
        }
    }

    /**
     * The click listener for ListView in the navigation drawer
     * 点击左侧抽屉的item
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(mleftLin);
            if (!NetUtils.isConnected(MainActivity.this)) {
                ToastUtil.toast(R.string.error_network_unreachable);
                return;
            }
            if (position == 0)//headView click
            {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            } else {
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
                finish();
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
        mDrawerLayout.closeDrawer(mleftLin);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
        if (SystemUtil.getSystemVersion().equals("6.0")){
            if (keyCode == 139 || keyCode == 280) {
                if (event.getRepeatCount() == 0) {
                    ((BaseFragment) fragment).myOnKeyDwon();
                }
            }else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                exit();
                return true;
            }
        }else {
            if (keyCode == 139 || keyCode == 293) {
                if (event.getRepeatCount() == 0) {
                    ((BaseFragment) fragment).myOnKeyDwon();
                }
            }else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                exit();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
        if (SystemUtil.getSystemVersion().equals("6.0")){
            if (keyCode == 139 || keyCode == 280) {
                if (event.getRepeatCount() == 0) {
                    ((BaseFragment) fragment).myOnKeyUp();
                }
            }
        }else {
            if (keyCode == 139 || keyCode == 293) {
                if (event.getRepeatCount() == 0) {
                    ((BaseFragment) fragment).myOnKeyUp();
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReader != null) {
            mReader.free();
        }
        if (PrintService.pl != null && PrintService.pl.getState() == PrinterClass.STATE_CONNECTED) {
            //断开打印连接
            PrintService.pl.disconnect();
        }
    }

    //点击左侧按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                if (fragment != null) {
                    if (fragment instanceof HomeFragment) {
                        if (mDrawerLayout.isDrawerVisible(mleftLin)) {
                            mDrawerLayout.closeDrawer(mleftLin);
                        } else {
                            mDrawerLayout.openDrawer(mleftLin);
                        }
                    } else {
                        if (isSoftShowing()) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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

    /**
     * 从服务器获取版本最新的版本信息
     */
    private void getVersionInfoFromServer() {
        if (ScreenUtils.avaiableMedia()) {
            path = getExternalCacheDir() + "/123.apk";   //SD卡存在
        } else {
            path = getCacheDir() + "/123.apk";           //SD卡不存在，用内部路径
        }

        //从服务器获取信息
        ACache.get(this).put("url", baseBean.getData().getAutoUpdateInfo().getFilePath());
        ACache.get(this).put("path", path);
        //比较版本信息
        try {
            int result = Utils.compareVersion(Utils.getVersionName(this), baseBean.getData().getAutoUpdateInfo().getVersion());
            if (result == -1) {//不是最新版本
                showDialog();
            } else {
                //Toast.makeText(MainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跟新版本的信息的Dialog
     */
    private void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView version, content;
        Button left, right;
        View view = inflater.inflate(R.layout.version_update, null, false);
        version = (TextView) view.findViewById(R.id.version);
        content = (TextView) view.findViewById(R.id.content);
        left = (Button) view.findViewById(R.id.left);
        right = (Button) view.findViewById(R.id.right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            content.setText(Html.fromHtml(baseBean.getData().getAutoUpdateInfo().getUpdateInfo(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            content.setText(baseBean.getData().getAutoUpdateInfo().getUpdateInfo());
        }
        content.setMovementMethod(LinkMovementMethod.getInstance());
        version.setText("版本号：" + baseBean.getData().getAutoUpdateInfo().getVersion());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                downloadNewVersionFromServer();

            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(false);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        //dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        lp.width = wm.getDefaultDisplay().getWidth() / 10 * 9;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * 启动服务后台下载
     */
    private void downloadNewVersionFromServer() {
        if (new File(path).exists()) {
            new File(path).delete();
        }
        Toast.makeText(MainActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
        LoadingService.startUploadImg(this);
    }

    /**
     * 定义广播接收者 接受下载状态
     */
    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.loading_over".equals(action)) {
                isLoading = false;
            } else if ("android.intent.action.loading".equals(action)) {
                isLoading = true;
            }
        }
    }
}




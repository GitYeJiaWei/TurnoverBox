package com.city.trash.ui.activity;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.city.trash.AppApplication;
import com.city.trash.bean.BaseEpc;
import com.city.trash.common.ActivityCollecter;
import com.city.trash.common.ScreenUtils;
import com.city.trash.common.util.NetUtils;
import com.city.trash.di.component.AppComponent;
import com.city.trash.presenter.BasePresenter;
import com.city.trash.ui.BaseView;
import com.city.trash.ui.receiver.NetworkChangeEvent;

import com.rscja.deviceapi.entity.UHFTAGInfo;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.city.trash.ui.activity.MainActivity.mReader;


public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView
{

    private Unbinder mUnbinder;

    protected AppApplication mApplication;

    private Toast mToast = null;
    private ProgressDialog waitDialog = null;
    protected boolean mCheckNetWork = true; //默认检查网络状态

    protected Boolean IsFlushList = true; // 是否刷列表
    protected Object beep_Lock = new Object();
    //蜂鸣器
    protected ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);


    @Inject
    public T mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(setLayout());

        mUnbinder = ButterKnife.bind(this);
        this.mApplication = (AppApplication) getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
        ActivityCollecter.addActivity(this);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IsFlushList = true;
        initSound();
        //在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查
        hasNetWork(NetUtils.isConnected(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder!=null&&mUnbinder != Unbinder.EMPTY)
        {

            mUnbinder.unbind();
        }
        if (waitDialog != null)
        {
            waitDialog = null;
        }

        EventBus.getDefault().unregister(this);
        ActivityCollecter.removeActivity(this);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            BaseEpc baseEpc = (BaseEpc) msg.obj;
            if(baseEpc!=null)
            {
                handleUi(baseEpc);
            }
        }
    };

    //处理ui
    public void handleUi(BaseEpc baseEpc)
    {
        synchronized (beep_Lock)
        {
            beep_Lock.notify();
        }
    }

    //配置读写器参数
    protected  void initSound()
    {
        // 蜂鸣器发声
        AppApplication.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                while (IsFlushList) {
                    synchronized (beep_Lock) {
                        try {
                            beep_Lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    if (IsFlushList) {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                    }
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        IsFlushList = false;
        synchronized (beep_Lock)
        {
            beep_Lock.notifyAll();
        }
        super.onPause();
    }

    //隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                ScreenUtils.hideKeyboard(ev, view, BaseActivity.this);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean loopFlag = false;

    class TagThread extends Thread {

        private int mBetween = 80;

        public TagThread(int iBetween) {
            mBetween = iBetween;
        }

        public void run() {
            String strTid;
            String strResult;
            UHFTAGInfo res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res.getTid();
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data", "EPC:" + res.getEPC() + "|" + strResult);
                    Message msg = handler.obtainMessage();
                    //msg.obj = strResult + "EPC:" + res.getEPC() + "@" + res.getRssi();

                    BaseEpc baseEpc = new BaseEpc();
                    baseEpc._EPC = res.getEPC();
                    baseEpc._TID = strResult;
                    baseEpc.rssi = res.getRssi();
                    msg.obj = baseEpc;

                    handler.sendMessage(msg);
                }
                try {
                    sleep(mBetween);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init();


    @Override
    public void showLoading()
    {
        if (waitDialog == null)
        {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage("加载中...");
        waitDialog.show();
    }

    @Override
    public void showError(String msg)
    {
        if (waitDialog != null)
        {
            waitDialog.setMessage(msg);
            waitDialog.show();
        }
    }

    @Override
    public void dismissLoading()
    {
        if (waitDialog != null && waitDialog.isShowing())
        {
            waitDialog.dismiss();
        }
    }

    //检查网络
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event)
    {
        hasNetWork(event.isConnected);
    }

    private void hasNetWork(boolean has)
    {
        if (isCheckNetWork())
        {
            handleNetWorkTips(has);
        }
    }

    protected void handleNetWorkTips(boolean has)
    {
    }
    public void setCheckNetWork(boolean checkNetWork)
    {
        mCheckNetWork = checkNetWork;
    }

    public boolean isCheckNetWork()
    {
        return mCheckNetWork;
    }

}

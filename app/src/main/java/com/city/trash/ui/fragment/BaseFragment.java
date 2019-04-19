package com.city.trash.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.city.trash.AppApplication;
import com.city.trash.common.CustomProgressDialog;
import com.city.trash.di.component.AppComponent;
import com.city.trash.presenter.BasePresenter;
import com.city.trash.ui.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment<T extends BasePresenter> extends BackPressedFragment implements BaseView
{

    private Unbinder mUnbinder;

    private AppApplication mApplication;

    private View mRootView;

    protected AppCompatActivity mActivity;

    protected CustomProgressDialog progressDialog;

    private ProgressDialog waitDialog = null;


    @Inject
    public T mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        mRootView = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        //mActivity = (AppCompatActivity) this.getActivity();
        //不建议使用getActivity(),用onAttach()强转
        init(mRootView);

        return mRootView;
    }

    public void myOnKeyDwon()
    {

    }

    public void myOnKeyUp()
    {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.mApplication = (AppApplication) getActivity().getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder != Unbinder.EMPTY)
        {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showLoading()
    {
        if (waitDialog == null)
        {
            waitDialog = new ProgressDialog(getContext());
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

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init(View view);

    public abstract void setBarCode(String barCode);


    @Override
    public void onBackPressed()
    {
        mActivity.onBackPressed();
    }

}

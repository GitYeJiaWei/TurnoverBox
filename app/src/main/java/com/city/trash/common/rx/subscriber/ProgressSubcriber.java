package com.city.trash.common.rx.subscriber;

import android.content.Context;
import android.util.Log;

import com.city.trash.common.exception.BaseException;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.ui.BaseView;

import io.reactivex.disposables.Disposable;

public  abstract  class ProgressSubcriber<T> extends ErrorHandlerSubscriber<T>  {

    private BaseView mView;

    public ProgressSubcriber(Context context, BaseView view) {
        super(context);
        this.mView = view;
    }

    public boolean isShowProgress(){
        return true;
    }


    @Override
    public void onSubscribe(Disposable d) {
        if(isShowProgress()){
            mView.showLoading();
        }
    }

    @Override
    public void onComplete() {
        //当所有onNext()完成后触发
        mView.dismissLoading();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        /*BaseException baseException = mErrorHandler.handleError(e);
        mView.showError(baseException.getDisplayMessage());*/

        ToastUtil.toast(e.getMessage());
        mView.showError(e.getMessage());
        mView.dismissLoading();

    }

}

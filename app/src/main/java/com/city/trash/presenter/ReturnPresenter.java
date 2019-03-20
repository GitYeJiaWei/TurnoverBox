package com.city.trash.presenter;

import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.ReturnBean;
import com.city.trash.common.rx.subscriber.ProgressSubcriber;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.presenter.contract.ReturnContract;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ReturnPresenter extends BasePresenter<ReturnContract.IReturnModel, ReturnContract.ReturnView> {
    @Inject
    public ReturnPresenter(ReturnContract.IReturnModel iReturnModel, ReturnContract.ReturnView returnView) {
        super(iReturnModel, returnView);
    }

    public void Return(String listEpcJson) {
        if (!NetUtils.isConnected(mContext)) {
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.Return(listEpcJson)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<List<ReturnBean>>>(mContext, mView) {
                    @Override
                    public void onNext(BaseBean<List<ReturnBean>> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.returnResult(baseBean);
                    }
                });
    }
}

package com.city.trash.presenter;

import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FindBean;
import com.city.trash.common.rx.subscriber.ProgressSubcriber;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.presenter.contract.FindContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FindPresenter extends BasePresenter<FindContract.IFindModel,FindContract.FindView> {
    @Inject
    public FindPresenter(FindContract.IFindModel iFindModel, FindContract.FindView findView) {
        super(iFindModel, findView);
    }

    public void find(String epc){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.find(epc)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<FindBean>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<FindBean> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.findResult(baseBean);
                    }
                });
    }
}

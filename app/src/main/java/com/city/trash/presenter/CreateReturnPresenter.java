package com.city.trash.presenter;

import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.common.rx.subscriber.ProgressSubcriber;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.presenter.contract.CreateReturnContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateReturnPresenter extends BasePresenter<CreateReturnContract.ICreateReturnModel,CreateReturnContract.CreateReturnView> {

    @Inject
    public CreateReturnPresenter(CreateReturnContract.ICreateReturnModel iCreateReturnModel, CreateReturnContract.CreateReturnView createReturnView) {
        super(iCreateReturnModel, createReturnView);
    }

    public void CreateReturn(String customerId,String damageFee,String listEpcJson){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.createReturn(customerId,damageFee,listEpcJson)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<String>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<String> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.createReturnResult(baseBean);
                    }
                });
    }
}

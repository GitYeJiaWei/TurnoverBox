package com.city.trash.presenter;

import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.common.rx.subscriber.ProgressSubcriber;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.presenter.contract.CreateDamageContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateDamagePresenter extends BasePresenter<CreateDamageContract.ICreateDamageModel,CreateDamageContract.CreateDamageView> {
    @Inject
    public CreateDamagePresenter(CreateDamageContract.ICreateDamageModel iCreateDamageModel, CreateDamageContract.CreateDamageView createDamageView) {
        super(iCreateDamageModel, createDamageView);
    }

    public void createDamage(String ListEpcJson){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.createDamage(ListEpcJson)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<String>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<String> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.createDamageResult(baseBean);
                    }
                });
    }
}

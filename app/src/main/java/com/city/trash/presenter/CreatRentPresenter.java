package com.city.trash.presenter;

import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.common.rx.subscriber.ProgressSubcriber;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.presenter.contract.CreateRentContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreatRentPresenter extends BasePresenter<CreateRentContract.ICreateRentModel,CreateRentContract.CreateRentView> {

    @Inject
    public CreatRentPresenter(CreateRentContract.ICreateRentModel iCreateRentModel, CreateRentContract.CreateRentView createRentView) {
        super(iCreateRentModel, createRentView);
    }

    public void creatrent(String ListEpcJson,String CustomerId,double ReplenishmentAmount){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.createRent(ListEpcJson,CustomerId,ReplenishmentAmount)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<Object>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<Object> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.createRentResult(baseBean);
                    }
                });
    }
}

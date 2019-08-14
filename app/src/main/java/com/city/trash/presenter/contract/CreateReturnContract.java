package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.ui.BaseView;

import io.reactivex.Observable;

public interface CreateReturnContract {
    //Model的接口,数据请求
    interface ICreateReturnModel{
        Observable<BaseBean<Object>> createReturn(String customerId,String damageFee,String listEpcJson);
    }

    //View的接口，表明View要做的事情
    interface CreateReturnView extends BaseView {
        void createReturnResult(BaseBean<Object> baseBean);
    }
}

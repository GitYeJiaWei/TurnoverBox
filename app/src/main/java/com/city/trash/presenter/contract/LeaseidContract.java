package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LeaseBean;
import com.city.trash.ui.BaseView;

import io.reactivex.Observable;

public interface LeaseidContract {
    //Model的接口,数据请求
    interface ILeaseidModel{
        Observable<BaseBean<LeaseBean>> leaseid(String cardCode);
    }

    //View的接口，表明View要做的事情
    interface LeaseidView extends BaseView {
        void leaseidResult(BaseBean<LeaseBean> baseBean);
    }
}

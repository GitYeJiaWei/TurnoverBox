package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FindBean;
import com.city.trash.ui.BaseView;

import io.reactivex.Observable;

public interface FindContract {
    //Model的接口,数据请求
    interface IFindModel{
        Observable<BaseBean<FindBean>> find(String epc);
    }

    //View的接口，表明View要做的事情
    interface FindView extends BaseView {
        void findResult(BaseBean<FindBean> baseBean);
    }
}

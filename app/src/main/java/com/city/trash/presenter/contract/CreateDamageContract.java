package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.ui.BaseView;

import io.reactivex.Observable;

public interface CreateDamageContract {
    //Model的接口,数据请求
    interface ICreateDamageModel{
        Observable<BaseBean<String>> createDamage(String ListEpcJson);
    }

    //View的接口，表明View要做的事情
    interface CreateDamageView extends BaseView {
        void createDamageResult(BaseBean<String> baseBean);
    }
}

package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.ReturnBean;
import com.city.trash.ui.BaseView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public interface ReturnContract {
    //Model的接口,数据请求
    interface IReturnModel{
        Observable<BaseBean<List<ReturnBean>>> Return(String listEpcJson);
    }

    //View的接口，表明View要做的事情
    interface ReturnView extends BaseView {
        void returnResult(BaseBean<List<ReturnBean>> baseBean);
    }
}

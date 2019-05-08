package com.city.trash.presenter.contract;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.ui.BaseView;

import java.util.List;

import io.reactivex.Observable;

public interface RuleListContract {
    //Model的接口,数据请求
    interface IFeeRuleModel{
        Observable<BaseBean<FeeRule>> feeRule(int deviceType);
    }

    //View的接口，表明View要做的事情
    interface FeeRuleView extends BaseView {
        void feeRuleResult(BaseBean<FeeRule> baseBean);
    }
}

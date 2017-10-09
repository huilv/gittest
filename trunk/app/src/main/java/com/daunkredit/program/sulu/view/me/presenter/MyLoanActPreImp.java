package com.daunkredit.program.sulu.view.me.presenter;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.view.me.MyLoanActView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 17:49
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MyLoanActPreImp extends BaseActivityPresenterImpl implements MyLoanActPresenter {
    @Override
    public void initLoanData() {
        final MyLoanActView view = (MyLoanActView) mView;
        String token = TokenManager.getInstance().getToken();
        showLoading(mView.getStringById(R.string.loading_loading));
        mUserApi.getLoanAppAll(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<HistoryLoanAppInfoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.d(e);
                        dismissLoading();
                    }

                    @Override
                    public void onNext(List<HistoryLoanAppInfoBean> historyLoanAppInfoBean) {
                        dismissLoading();
                        if (historyLoanAppInfoBean == null || historyLoanAppInfoBean.size() == 0) {
                            XLeoToast.showMessage(R.string.text_my_loan_not_loan);
                            return;
                        }
                        if (isAttached()) {
                            view.initData(historyLoanAppInfoBean);
                        }
                    }
                });

    }
}

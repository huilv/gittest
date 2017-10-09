package com.daunkredit.program.sulu.view.fragment.presenter;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 15:55
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanInProFraPreImp extends BaseFragmentPresenterImpl implements LoanInProFraPresenter {

    @Override
    public void cancelLoan(final LoanAppBeanFather latestLoanAppBean) {

        mView.getBaseActivity().showLoading(null);
        mUserApi.cancelLoan(latestLoanAppBean.getLoanAppId(), TokenManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                        XLeoToast.showMessage(R.string.show_loan_cancel_fail);
                        Logger.d("Cancel loan app failed");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        XLeoToast.showMessage(R.string.show_loan_cancel_success);
                        Logger.d("Cancel loan app succeed");
                        RxBus.get().post(new EventCollection.CancelLoan());
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                    }
                });


    }
}

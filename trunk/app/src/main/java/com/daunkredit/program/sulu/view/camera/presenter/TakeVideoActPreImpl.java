package com.daunkredit.program.sulu.view.camera.presenter;

import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 13:09
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TakeVideoActPreImpl extends BaseActivityPresenterImpl implements TakeVideoActPresenter {
    @Override
    public void cancelLoan(LatestLoanAppBean bean) {
        mView.getBaseActivity().showLoading(null);
        mUserApi.cancelLoan(bean.getLoanAppId(), TokenManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Toast.makeText(TakeVideoActivity.this, "Cancel Loan app failed.", Toast.LENGTH_SHORT).show();
                        Logger.d("Cancel loan app failed");
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                        mView.getBaseActivity().finish();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        //Toast.makeText(TakeVideoActivity.this, "Cancel Loan app succeed.", Toast.LENGTH_SHORT).show();
                        Logger.d("Cancel loan app succeed");
                        RxBus.get().post(new EventCollection.CancelLoan());
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                        mView.getBaseActivity().finish();

                    }
                });
    }
}

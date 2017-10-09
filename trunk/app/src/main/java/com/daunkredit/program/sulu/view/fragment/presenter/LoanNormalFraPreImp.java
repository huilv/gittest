package com.daunkredit.program.sulu.view.fragment.presenter;

import android.content.Intent;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.QualityErrorBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.view.MainActivity;
import com.daunkredit.program.sulu.view.camera.TakeVideoActivity;
import com.daunkredit.program.sulu.view.fragment.LoanNormalView;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.google.gson.Gson;
import com.hwangjr.rxbus.RxBus;
import com.sulu.kotlin.data.LoanRange;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:02
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanNormalFraPreImp extends BaseFragmentPresenterImpl implements LoanNormalFraPresenter {

    final UserApi api = ServiceGenerator.createService(UserApi.class);
    @Override
    public void getCurrentLoan() {
        TokenManager instance = TokenManager.getInstance();
        if (!instance.hasLogin()) {
            TokenManager.isReSetLoanStatus = true;
            Intent intent = new Intent(mView.getBaseActivity(), LoginActivity.class);
            mView.getBaseActivity().startActivity(intent);
        } else {
            LatestLoanAppBean o = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
            if (o != null && FieldParams.LoanStatus.SUBMITTED.equals(o.getStatus())) {
                mView.getBaseActivity().changeTo(TakeVideoActivity.class);
            } else {
//                XLeoToast.showMessage(R.string.toast_please_finish_info);

                api.isQualification(TokenManager.getInstance().getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<QualityErrorBean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Gson gson = new Gson();
                                if (e instanceof HttpException) {
                                    HttpException httpException = (HttpException) e;
                                    String errorBody = null;
                                    try {
                                        errorBody = httpException.response().errorBody().string();
                                        QualityErrorBean qualityErrorBean = gson.fromJson(errorBody, QualityErrorBean.class);
                                        XLeoToast.showMessage("" + qualityErrorBean.getMessage());
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    XLeoToast.showMessage(R.string.show_netwok_wrong);
                                }
                            }

                            @Override
                            public void onNext(QualityErrorBean qualityErrorBean) {
                                MainActivity.isSubmitVisible = true;
                                RxBus.get().post(TokenManager.checkoutMessage(FieldParams.TO_LOAN_DATA));
                            }
                        });
            }
        }
    }

    @Override
    public void requestDataSpan() {
        final LoanNormalView view = (LoanNormalView) mView;
        mView.getBaseActivity().showLoading(null);
        mUserApi.getLoanRange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                         new Subscriber<LoanRange>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.getBaseActivity().dismissLoading();
                                                XLeoToast.showMessage(R.string.show_netwok_wrong);
                                            }

                                            @Override
                                            public void onNext(LoanRange loanRange) {
                                                if (loanRange == null) {
                                                    onError(new IllegalArgumentException());
                                                    return;
                                                }
                                                mView.getBaseActivity().dismissLoading();
                                                //instance.putMessage(FieldParams.LOAN_RANGE,loanRange);
                                                view.onSpanObtain(loanRange);
                                            }
    });
    }
}

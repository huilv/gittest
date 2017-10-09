package com.daunkredit.program.sulu.view.fragment.presenter;

import android.content.Intent;

import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.QualityErrorBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.view.camera.TakeVideoActivity;
import com.daunkredit.program.sulu.view.fragment.LoaningFraView;
import com.daunkredit.program.sulu.view.fragment.LoaningFragment;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 15:40
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoaningFraPreImp extends BaseFragmentPresenterImpl implements LoaningFraPresenter {
    private final UserApi api = ServiceGenerator.createService(UserApi.class);
    @Override
    public void applyLoan(LoaningFragment.ApplyLoanInfo applyLoanInfo) {
        final LoaningFraView view = (LoaningFraView) mView;
        mView.getBaseActivity().showLoading(null);
        api.applyLoanApp(applyLoanInfo.getLoanType(),
                applyLoanInfo.getAmount() + "",
                applyLoanInfo.getPeroid() + "",
                applyLoanInfo.getPeriodUnit(),
                applyLoanInfo.getBankCode(),
                applyLoanInfo.getCardNo().trim(),
                applyLoanInfo.getApplyFor(),
                applyLoanInfo.getApplyChannel(),
                applyLoanInfo.getApplyPlatform(),
                applyLoanInfo.getCouponId(),
                TokenManager.getInstance().getToken()
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Gson gson = new Gson();
                        if(e instanceof HttpException){
                            HttpException httpException = (HttpException) e;
                            String errorBody = null;
                            try {
                                errorBody = httpException.response().errorBody().string();
                                QualityErrorBean qualityErrorBean = gson.fromJson(errorBody,QualityErrorBean.class);
                                XLeoToast.showMessage(qualityErrorBean.getMessage());
                                Logger.d("e:" + errorBody);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }else{
                            XLeoToast.showMessage(e.toString());
                            Logger.d("e:" + e);
                        }
                        dismissLoading();
                        if (!isAttached()) {
                            return;
                        }
                        view.resetButton();

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Logger.d(getClass().getName()+" start video recording");
                        dismissLoading();
                        if (!isAttached()) {
                            return;
                        }
                        Intent intent = new Intent(mView.getBaseActivity(), TakeVideoActivity.class);
                        mView.getBaseActivity().startActivity(intent);
                    }
                });
    }
}

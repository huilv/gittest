package com.daunkredit.program.sulu.view.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.enums.LoanStatus;
import com.daunkredit.program.sulu.view.MainActivity;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.utils.AssertsCopyer;

import java.io.File;
import java.io.IOException;

import rx.Subscriber;

/**
 * @作者:My
 * @创建日期: 2017/6/21 11:05
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SplashActPresenterImpl extends BaseActivityPresenterImpl implements SplashActPresenter {
    private Long              mStartTime;
    private Handler           mHandler;
    private LatestLoanAppBean mLatestLoanAppBean;
    private boolean           canFinish;
    private boolean isVisible = true;

    private void jumpToMainActivity(Long currentTime) {
        Long delayTime = (currentTime - mStartTime >= 3000L) ? 0 : (mStartTime + 3000 - currentTime);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAttached()) {
                    Intent intent = new Intent(mView.getBaseActivity(), MainActivity.class);
                    mView.getBaseActivity().startActivity(intent);
                }
                if (canFinish) {
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                } else {
                    //                    setVisible(false);
                    isVisible = false;
                }

            }
        }, delayTime);
    }
    // public static String requestCode;

    /**
     * Get latest loan
     *
     * @return
     */
    public void getLatestLoan() {
        if (!TokenManager.getInstance().hasLogin()) {
            canFinish = true;
            jumpToMainActivity(SystemClock.currentThreadTimeMillis());
        } else {
            TokenManager.DataUpdateManager.requestLoanLatestData(1000, new Subscriber<LatestLoanAppBean>() {
                //                @Override
                //                public void onCompleted() {
                //
                //                }
                //
                //                @Override
                //                public void onError(Throwable e) {
                //
                //                }
                //
                //                @Override
                //                public void onNext(LatestLoanAppBean latestLoanAppBean) {
                //
                //                }
                //            });
                //            mUserApi.getLatestLoanApp(TokenManager.getInstance().getToken(), "splash")
                //                    .subscribeOn(Schedulers.io())
                //                    .observeOn(AndroidSchedulers.mainThread())
                //                    .subscribe(new Subscriber<LatestLoanAppBean>() {
                @Override
                public void onCompleted() {


                }

                @Override
                public void onError(Throwable e) {
                    //                        loanStatus = MainActivity.LoanStatus.UNLOAN;
                    TokenManager.getInstance().storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                    //                        Toast.makeText(SplashActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    Logger.d("Get latest Loan app... Error" + e);
                    canFinish = true;
                    if (isVisible) {
                        jumpToMainActivity(SystemClock.currentThreadTimeMillis());
                    } else {
                        mHandler.removeCallbacksAndMessages(null);
                        finish();
                    }
                }

                @Override
                public void onNext(LatestLoanAppBean latestLoanBean) {
                    //latestLoanAppBean = latestLoanBean;
                    mLatestLoanAppBean = latestLoanBean;
                    TokenManager tokenManager = TokenManager.getInstance();
                    tokenManager.storeMessage(FieldParams.LATESTBEAN, mLatestLoanAppBean);
                    if (mLatestLoanAppBean.getStatus() == null) {
                        Logger.d("loanStatus is null");
                        //                            loanStatus = LoanStatus.UNLOAN;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANED);
                    } else if (mLatestLoanAppBean.getStatus().equals("OVERDUE")) {
                        //                            loanStatus = LoanStatus.EXPIRED;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.EXPIRED);
                    } else if (mLatestLoanAppBean.getStatus().equals("CURRENT")) {
                        //                            loanStatus = LoanStatus.LOANED;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANED);
                    } else if (
                            mLatestLoanAppBean.getStatus().equals("IN_REVIEW") ||
                                    mLatestLoanAppBean.getStatus().equals("READY_TO_ISSUE") ||
                                    mLatestLoanAppBean.getStatus().equals("APPROVED") ||
                                    mLatestLoanAppBean.getStatus().equals("ISSUING")) {
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANINPROCESS);
                        //                            loanStatus = LoanStatus.LOANINPROCESS;

                    } else if (mLatestLoanAppBean.getStatus().equals("NOLOANAPP") ||
                            mLatestLoanAppBean.getStatus().equals("CANCELED") ||
                            mLatestLoanAppBean.getStatus().equals("WITHDRAWN") ||
                            mLatestLoanAppBean.getStatus().equals("REJECTED") ||
                            mLatestLoanAppBean.getStatus().equals("PAID_OFF") ||
                            mLatestLoanAppBean.getStatus().equals("CLOSED")) {
                        //                            loanStatus = LoanStatus.UNLOAN;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                    } else if (mLatestLoanAppBean.getStatus().equals("SUBMITTED")) {
                        //                            loanStatus = LoanStatus.SUBMITTED;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.SUBMITTED);
                    } else {
                        Logger.d("unKnown status");
                        //                            loanStatus = LoanStatus.UNLOAN;
                        tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                    }

                    Logger.d("loan app Status:  " + mLatestLoanAppBean.getStatus());
                    //                        Logger.d("loanStatus:  "+loanStatus);
                    //                        loanStatus = LoanStatus.EXPIRED;
                    canFinish = true;
                    if (isVisible) {
                        jumpToMainActivity(SystemClock.currentThreadTimeMillis());
                    } else {
                        mHandler.removeCallbacksAndMessages(null);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void requestDataAndJump() {
        mStartTime = SystemClock.currentThreadTimeMillis();
        mHandler = new Handler();
        jumpToMainActivity(mStartTime);
        mLatestLoanAppBean = new LatestLoanAppBean();
        getLatestLoan();
    }

    @Override
    public void checkAndCopyAsserts() {
        if (isAttached()) {
            Context baseActivity = mView.getBaseActivity().getApplicationContext();
            AssetManager assets = baseActivity.getAssets();
            try {
                String[] list = assets.list("needcopy");
                if (list == null || list.length <= 0) {
                    LoggerWrapper.d("no asserts");
                    return;
                }
                for (String s : list) {
                    if (TextUtils.isEmpty(s)) {
                        continue;
                    }
                    if (!AssertsCopyer.INSTANCE.checkFile("needcopy" + File.separator + s, baseActivity)) {
                        AssertsCopyer.INSTANCE.copyfile("needcopy" + File.separator + s, assets, baseActivity,null);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

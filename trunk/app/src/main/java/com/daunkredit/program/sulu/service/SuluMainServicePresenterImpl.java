package com.daunkredit.program.sulu.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.VersionBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;

import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.alibaba.mobileim.channel.IMChannel.startTime;

/**
 * @作者:My
 * @创建日期: 2017/7/17 14:35
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

class SuluMainServicePresenterImpl implements SuluMainServicePresenter {
    private SuluMainServiceInterface mService;
    private UserApi mUserApi = ServiceGenerator.createService(UserApi.class);
    private Runnable mTask;
    private Handler  mTimer;
    private long taskDelay         = 60 * 1000;
    private long versionCheckDelay = 24 * 3600 * 1000;
    public long mTime;

    private LatestLoanAppBean mLatestBean;

    public SuluMainServicePresenterImpl(SuluMainServiceInterface suluMainService) {
        mService = suluMainService;
    }

    @Override
    public void initBinder(SuluMainService.SuluMainBinder binder) {
        binder.setHandler(mTimer);
        binder.setTask(mTask);
    }

    @Override
    public void startBasicTask() {
        Looper newLooper = Looper.myLooper();
        mTimer = new Handler(newLooper);
        mTask = new Runnable() {
            @Override
            public void run() {
                dataObtain();
                mTimer.postDelayed(this, taskDelay);
            }
        };
        mTimer.post(mTask);
        //        timer.post(new Runnable() {
        //            @Override
        //            public void run() {
        //                checkVersion();
        //                timer.postDelayed(this, versionCheckDelay);
        //            }
        //        });
    }

    @Override
    public void onUnbind() {
        mTimer.removeCallbacksAndMessages(null);
        mTimer = null;
        mService = null;
    }

    private void checkVersion() {
        mUserApi.getVersionInfo().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<VersionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.d(e);
                        XLeoToast.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(final VersionBean versionBean) {
                        if (mService != null && mService.isNeedUpdate(versionBean.getLatestVersionCode())) {
                            Intent intent = new Intent(FieldParams.BroadcastAction.BROADCASTACTION_UPDATE);
                            intent.putExtra(FieldParams.UPDATE_VERSON_BEAN, versionBean);
                            mService.sendBroadCoast(intent);
                        }
                    }
                });
    }

    private void dataObtain() {
        String token = TokenManager.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            return;
        }
        LoggerWrapper.d("obtainData");
        //        mUserApi.getLatestLoanApp(token, "main_service")
        //                .subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(
        TokenManager.DataUpdateManager.requestLoanLatestData(taskDelay,
                new Subscriber<LatestLoanAppBean>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(LatestLoanAppBean latestLoanAppBean) {
                        LoggerWrapper.d("onnext");
                        if (startTime > mTime) {
                            mTime = startTime;
                            mLatestBean = latestLoanAppBean;
                            if (mService != null && isDataNeedUpdate()) {
                                TokenManager instance = TokenManager.getInstance();
                                instance.storeMessage(FieldParams.LATESTBEAN, latestLoanAppBean);
                                Intent intent = new Intent();
                                intent.setAction(FieldParams.BroadcastAction.BROADCASTACTION);
                                mService.sendBroadCoast(intent);
                            }

                        }
                    }
                });
    }

    private boolean isDataNeedUpdate() {
        Object message = TokenManager.getInstance().getMessage(FieldParams.LATESTBEAN);
        if (message != null && message instanceof LatestLoanAppBean) {
            LatestLoanAppBean temp = (LatestLoanAppBean) message;
            if (TextUtils.equals(temp.getStatus(), mLatestBean.getStatus()) && TextUtils.equals(temp.getBankCode(), mLatestBean.getBankCode())
                    && TextUtils.equals(temp.getCardNo(), mLatestBean.getCardNo()) && TextUtils.equals(temp.getLoanAppId(), mLatestBean.getLoanAppId())
                    && temp.getAmount() == mLatestBean.getAmount() && temp.getRemainAmount() == mLatestBean.getRemainAmount()
                    && temp.getPaidAmount() == mLatestBean.getPaidAmount() && temp.getTotalAmount() == mLatestBean.getTotalAmount()) {
                return false;
            }
        }
        return true;
    }

}

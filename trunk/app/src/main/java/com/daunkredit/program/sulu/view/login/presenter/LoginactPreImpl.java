package com.daunkredit.program.sulu.view.login.presenter;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.LoginRequestBean;
import com.daunkredit.program.sulu.bean.LoginTokenResponse;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.enums.LoanStatus;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.login.LoginActView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.alibaba.mobileim.YWChannel.getResources;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoginactPreImpl extends BaseActivityPresenterImpl implements LoginActPresenter {
    final UserApi api = ServiceGenerator.createService(UserApi.class);
    private int loginCount;

    @Override
    public void obtainSmsCode(Button buttonObtain, EditText editTextPhone) {
        UserEventQueue.add(new ClickEvent(buttonObtain.toString(), ActionType.CLICK, buttonObtain.getText().toString()));
        String phoneStr = editTextPhone.getText().toString();
        LoginActView view = (LoginActView) mView;
        if (view.checkLegalState(editTextPhone)) {
            new TimeCount(60000, 1000, buttonObtain).start();
            api.sendSms(phoneStr)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {

                            //  Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            Logger.d("exception: " + e);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {

                            // Toast.makeText(LoginActivity.this, "" + responseBody.toString(), Toast.LENGTH_SHORT).show();
                            Logger.d("type: " + responseBody.contentType());
                            Logger.d("len: " + responseBody.contentLength());

                        }
                    });
        }
    }

    @Override
    public void doLogin(final LoginRequestBean loginRequestBean) {
        /**
         * Login
         */
        final LoginActView view = (LoginActView) mView;
        api.login(loginRequestBean.smsCode,
                loginRequestBean.captchaSid,
                loginRequestBean.captcha,
                loginRequestBean.mobile,
                loginRequestBean.inviteCode)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (!isAttached()) {
                            return;
                        }
                        view.getBaseActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoading(null);
                            }
                        });

                    }
                })
                .doOnNext(new Action1<LoginTokenResponse>() {
                    @Override
                    public void call(LoginTokenResponse loginTokenResponse) {
                        if (TokenManager.isReSetLoanStatus) {
                            api.getLatestLoanApp(loginTokenResponse.getToken(), "tag")
                                    .subscribe(new Action1<LatestLoanAppBean>() {
                                        @Override
                                        public void call(LatestLoanAppBean lLoanAppBean) {
                                            TokenManager tokenManager = TokenManager.getInstance();
                                            tokenManager.storeMessage(FieldParams.LATESTBEAN, lLoanAppBean);

                                            if (lLoanAppBean.getStatus() == null) {
                                                Logger.d("loanStatus is null");
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                                            } else if ("OVERDUE".equals(lLoanAppBean.getStatus())) {
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.EXPIRED);
                                            } else if ("CURRENT".equals(lLoanAppBean.getStatus())) {
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANED);
                                            } else if ("SUBMITTED".equals(lLoanAppBean.getStatus())) {
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.SUBMITTED);
                                            } else if ("IN_REVIEW".equals(lLoanAppBean.getStatus()) ||
                                                    "READY_TO_ISSUE".equals(lLoanAppBean.getStatus()) ||
                                                    "APPROVED".equals(lLoanAppBean.getStatus()) ||
                                                    "ISSUING".equals(lLoanAppBean.getStatus())) {
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANINPROCESS);
                                            } else if (lLoanAppBean.getStatus().equals("NOLOANAPP") ||
                                                    lLoanAppBean.getStatus().equals("CANCELED") ||
                                                    lLoanAppBean.getStatus().equals("REJECTED") ||
                                                    lLoanAppBean.getStatus().equals("PAID_OFF") ||
                                                    lLoanAppBean.getStatus().equals("CLOSED")) {
                                                tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                                            }

                                            RxBus.get().post(new EventCollection.ReSetLoanStatusEvent());
                                            Logger.d("loan app Status:  " + lLoanAppBean.getStatus());
                                        }
                                    });
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginTokenResponse>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        loginCount++;
                        if (loginCount >= 2 && isAttached()) {
                           view.onLoginError();
                        }
                        dismissLoading();
                        Logger.d("Error: " + e.getMessage());
                        XLeoToast.showMessage(R.string.show_input_error);

                    }

                    @Override
                    public void onNext(LoginTokenResponse loginTokenResponse) {
                        loginCount = 0;
                        Logger.d("token " + loginTokenResponse.getToken());
                        // Logger.d("refresh_token "+loginTokenResponse.getRefreshToken());
                        TokenManager.getInstance().setToken(loginTokenResponse.getToken(), 0);
                        TokenManager.getInstance().setMobile(loginRequestBean.mobile.toString());
                        TokenManager.removeMessageFromFile(FieldParams.USERNAME);
                        dismissLoading();
                        RxBus.get().post(new EventCollection.ReSetLoanStatusEvent());
                        if (isAttached()) {
                            mView.getBaseActivity().setResult(0, mView.getBaseActivity().getIntent());
                        }
                        //bind account for ali push
                        PushServiceFactory.getCloudPushService().bindAccount(TokenManager.getInstance().getMobile(), new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                LoggerWrapper.d("bindService success");
                            }

                            @Override
                            public void onFailed(String s, String s1) {
                                LoggerWrapper.d("bindService failed");

                            }
                        });
                        finish();
                    }
                });

    }



    /**
     * set time count
     */
    class TimeCount extends CountDownTimer {

        private final Button buttonObtain;

        public TimeCount(long millisInFuture, long countDownInterval, Button button) {
            super(millisInFuture, countDownInterval);
            buttonObtain = button;
        }


        @Override
        public void onFinish() {
            buttonObtain.setText(getResources().getText(R.string.button_obtain_code));
            buttonObtain.setSelected(false);
            buttonObtain.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            buttonObtain.setSelected(true);
            buttonObtain.setClickable(false);
            buttonObtain.setText(millisUntilFinished / 1000 + "s");
        }
    }
}

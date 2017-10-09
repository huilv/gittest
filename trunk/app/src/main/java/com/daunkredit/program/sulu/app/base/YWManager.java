package com.daunkredit.program.sulu.app.base;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMCore;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.App;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.bean.YWUser;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.orhanobut.logger.Logger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.alibaba.mobileim.YWIMKit;

/**
 * @作者:My
 * @创建日期: 2017/6/21 9:59
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class YWManager {

    private static YWIMCore ywimCore;

    public interface OnLoginCompleteListener {
        void onLoginComplete(YWIMCore mIMKit);

        void onLoginError(int errCode, String description);
    }

    public static void initYW(final BaseActivity activity, final OnLoginCompleteListener l) {
        String token = TokenManager.getInstance().getToken();
        if (TextUtils.isEmpty(token) || TokenManager.isExpired) {
            ToastManager.showToast(activity.getString(R.string.show_not_login_yet));
            activity.startActivity(new Intent(activity, LoginActivity.class));
            return;
        }
        activity.showLoading(activity.getString(R.string.show_open_chat_window));
        obtainAccount(token,activity, l);
    }

    private static void obtainAccount(String token, final BaseActivity activity, final OnLoginCompleteListener l) {
        String userid = TokenManager.getYWAccountID();
        if (userid != null) {
            String password = TokenManager.checkoutMessage(FieldParams.YW_ACCOUNT_PASSWORD, String.class);
            if (password != null) {
                loginYWCore(userid, password, activity, l);
                return;
            }
        }
        UserApi mUserApi = ServiceGenerator.createService(UserApi.class);
        mUserApi.getChatUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<YWUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        activity.dismissLoading();
                        ToastManager.showToast(activity.getString(R.string.show_get_user_account_fail));
                    }

                    @Override
                    public void onNext(YWUser responseBody) {
                        String userid = responseBody.getUserid();
                        String password = responseBody.getPassword();
                        TokenManager.storeYWAccount(userid,password);
                        if (l == null) {
//                            loginYW(userid, password, activity);
                        } else {
                            loginYWCore(userid, password, activity, l);
                        }
                    }
                });
    }

    public static void loginYWCore(String userid, String password, final BaseActivity activity, final OnLoginCompleteListener l) {
        ywimCore = YWAPI.createIMCore(userid, App.ALI_IM_APP_KEY);
        IYWLoginService loginService1 = ywimCore.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userid, password);
        loginService1.login(loginParam, new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
                Logger.d(arg0.length);

                for (Object obj : arg0
                        ) {
                    Logger.d(obj.toString());
                }

                TokenManager.putMessage(FieldParams.HAS_YW_LOGIN,true);
                activity.dismissLoading();
                l.onLoginComplete(ywimCore);

            }

            @Override
            public void onProgress(int arg0) {
                // TODO Auto-generated method stub

                Logger.d("progress " + arg0);
            }

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
                activity.dismissLoading();
                //                ToastManager.showToast(getString(R.string.show_login_al_failed));
                Logger.d("errCode " + errCode + " desc " + description);
                if (l != null) {
                    l.onLoginError(errCode, description);
                }
            }
        });
    }

    public static void logout() {
        if (ywimCore == null) {
            return;
        }
        IYWLoginService loginService = ywimCore.getLoginService();
        loginService.logout(new IWxCallback() {
                                @Override
                                public void onSuccess(Object... objects) {
                                    TokenManager.putMessage(FieldParams.HAS_YW_LOGIN,false);
                                }

                                @Override
                                public void onError(int i, String s) {
                                }

                                @Override
                                public void onProgress(int i) {

                                }
                            }
        );
    }


}

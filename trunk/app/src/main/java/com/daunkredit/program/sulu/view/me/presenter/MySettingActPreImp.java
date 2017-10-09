package com.daunkredit.program.sulu.view.me.presenter;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.YWManager;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 17:59
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MySettingActPreImp extends BaseActivityPresenterImpl implements MySettingActPresenter {
    @Override
    public void logout() {
        String token = TokenManager.getInstance().getToken();

        mUserApi.logout(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLeoToast.showMessage(R.string.show_log_out_failed);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        XLeoToast.showMessage(R.string.show_log_out_success);
                        TokenManager.getInstance().clear();
                        TokenManager.removeMessageFromFile(FieldParams.USERNAME);
                        RxBus.get().post(new EventCollection.LogoutEvent());
                        //unbind account for ali push
                        PushServiceFactory.getCloudPushService().unbindAccount(new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                LoggerWrapper.d("unbind success");
                            }

                            @Override
                            public void onFailed(String s, String s1) {
                                LoggerWrapper.d("unbind failed");
                            }
                        });
                        YWManager.logout();
                        finish();
                    }
                });
    }


}

package com.daunkredit.program.sulu.view.fragment.presenter;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.YWManager;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.view.fragment.MeFraView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;
import com.sulu.kotlin.data.ActivityInfoBean;
import com.sulu.kotlin.data.MeInfoBean;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:59
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MeFraPreImp extends BaseFragmentPresenterImpl implements MeFraPresenter {
    @Override
    public void initUserAccountInfo() {
        final MeFraView view = (MeFraView) mView;
        if (TokenManager.getInstance().hasLogin()) {
            showLoading(null);
            mUserApi.getMeInfo(TokenManager.getInstance().getToken())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<MeInfoBean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissLoading();
                            XLeoToast.showMessage(R.string.show_netwok_wrong);
                            view.initUserInfo(new MeInfoBean());
                        }

                        @Override
                        public void onNext(MeInfoBean meInfoBean) {
                            dismissLoading();
                            if (isAttached()) {
                                TokenManager.storeYWAccount(meInfoBean.getChatAccount().getUserid(),meInfoBean.getChatAccount().getPassword());
                                view.initUserInfo(meInfoBean);
                            }
                        }
                    });
        } else {
            showLoading(null);
            mUserApi.getActivityList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ArrayList<ActivityInfoBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isAttached()) {
                                XLeoToast.showMessage(R.string.show_netwok_wrong);
                                dismissLoading();
                            }
                        }

                        @Override
                        public void onNext(ArrayList<ActivityInfoBean> activityInfoBeen) {
                            if (isAttached()) {
                                dismissLoading();
                                MeInfoBean bean = new MeInfoBean();
                                bean.setBanner(activityInfoBeen);
                                view.initUserInfo(bean);
                            }
                        }
                    });
        }
    }

    @Override
    public void logout() {
        final MeFraView view = (MeFraView) mView;
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
                        view.initUserInfo(new MeInfoBean());
                    }
                });
    }

    @Override
    public void login() {
        mView.toLogin();
    }
}

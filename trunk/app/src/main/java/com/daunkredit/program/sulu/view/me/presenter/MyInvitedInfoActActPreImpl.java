package com.daunkredit.program.sulu.view.me.presenter;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.view.me.MyInvitedInfoActView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.sulu.kotlin.data.InviteResult;
import com.sulu.kotlin.data.InviteeBean;
import com.sulu.kotlin.data.InviteePersonBean;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 17:42
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MyInvitedInfoActActPreImpl extends BaseActivityPresenterImpl implements MyInvitedInfoActPresenter {
    @Override
    public void obtainCode() {

        showLoading(null);
        Observable<ResponseBody> inviteCode = mUserApi.getInviteCode(TokenManager.getInstance().getToken());
        Observable<ArrayList<InviteePersonBean>> invitedList = mUserApi.getInvitedList(TokenManager.getInstance().getToken());
        Observable<InviteeBean> inviteInfo = mUserApi.getInviteInfo(TokenManager.getInstance().getToken());
        Observable<InviteResult> objectObservable = Observable.combineLatest(invitedList, inviteInfo, inviteCode, new Func3<ArrayList<InviteePersonBean>, InviteeBean, ResponseBody, InviteResult>() {
            @Override
            public InviteResult call(ArrayList<InviteePersonBean> inviteePersonBeen, InviteeBean inviteeBean, ResponseBody responseBody) {
                String code = "";
                try {
                    code = responseBody.string();
                }catch (Exception e){
                    XLeoToast.showMessage(R.string.show_netwok_wrong);
                }
                return new InviteResult(inviteePersonBeen,inviteeBean,code);
            }
        });

        objectObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<InviteResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoading();
                        XLeoToast.showMessage(R.string.show_netwok_wrong);
                    }

                    @Override
                    public void onNext(InviteResult inviteResult) {
                        if (isAttached()) {
                            dismissLoading();
                            ((MyInvitedInfoActView) mView).onInviteResultObtain(inviteResult);
                        }
                    }
                });
    }
}

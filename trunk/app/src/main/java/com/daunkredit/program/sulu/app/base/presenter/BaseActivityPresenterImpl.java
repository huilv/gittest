package com.daunkredit.program.sulu.app.base.presenter;

import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.app.base.BaseActivityView;
import com.daunkredit.program.sulu.app.base.YWManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.PermissionManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 9:46
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class BaseActivityPresenterImpl implements BaseActivityPresenter {

    protected UserApi mUserApi = ServiceGenerator.createService(UserApi.class);
    protected BaseActivityView mView;
    protected boolean isAttached;

    @Override
    public void initYW() {
        if (mView instanceof BaseActivity) {
            YWManager.initYW(mView.getBaseActivity(),null);
        }
    }

    @Override
    public   void attach(BaseActivityView tBaseActivity) {
        mView = tBaseActivity;
        isAttached = true;
    }


    @Override
    public void disAttach() {
        mView = null;
        mUserApi = null;
        isAttached = false;
    }

    @Override
    public boolean isAttached() {
        return isAttached;
    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {

            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @Override
    public void uploadXTrace() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PermissionManager().sendAll(mView.getBaseActivity());
            }
        }).start();
    }

    @Override
    public void showLoading(String string) {
        if (isAttached()) {
            mView.getBaseActivity().showLoading(string);
        }
    }

    @Override
    public void dismissLoading() {
        if (isAttached()) {
            mView.getBaseActivity().dismissLoading();
        }
    }

    @Override
    public void finish() {
        if (isAttached()) {
            mView.getBaseActivity().finish();
        }
    }
}

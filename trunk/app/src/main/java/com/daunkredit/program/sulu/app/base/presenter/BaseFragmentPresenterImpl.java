package com.daunkredit.program.sulu.app.base.presenter;

import com.daunkredit.program.sulu.app.base.BaseFragmentView;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.PermissionManager;

/**
 * @作者:My
 * @创建日期: 2017/6/21 10:08
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class BaseFragmentPresenterImpl implements BaseFragmentPresenter {
    protected BaseFragmentView mView;
    protected UserApi mUserApi = ServiceGenerator.createService(UserApi.class);
    private boolean isAttached;

    @Override
    public void attach(BaseFragmentView v) {
        mView = v;
        isAttached = true;
    }

    @Override
    public void detach() {
        mView = null;
        mUserApi = null;
        isAttached = false;
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
    public boolean isAttached() {
        return isAttached;
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

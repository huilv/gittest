package com.daunkredit.program.sulu.view.presenter;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.MsgInboxBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.view.MsgBoxActView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 18:31
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MsgBoxActPreImp extends BaseActivityPresenterImpl implements MsgBoxActPresenter {
    public void initData() {
        final MsgBoxActView view = (MsgBoxActView) mView;

        mUserApi.getMsgInbox(TokenManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MsgInboxBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XLeoToast.showMessage(R.string.show_no_message);
                    }

                    @Override
                    public void onNext(List<MsgInboxBean> msgInboxBeen) {
                        if (msgInboxBeen == null && msgInboxBeen.size() == 0) {
                            XLeoToast.showMessage(R.string.show_no_message);
                            return;
                        }
                        if (isAttached()) {
                            view.initMsgDatas(msgInboxBeen);
                        }

                    }
                });
    }

    @Override
    public void setMsgChecked(final MsgInboxBean msgChecked) {
        final MsgBoxActView view = (MsgBoxActView) mView;
        mUserApi.sendReadMsg(msgChecked.getMsgId() + "",TokenManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        msgChecked.setRead(true);
                        if (isAttached()) {
                            view.updateList();
                        }
                    }
                });
    }
}

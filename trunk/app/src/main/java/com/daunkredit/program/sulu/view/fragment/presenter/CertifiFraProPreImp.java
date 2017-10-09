package com.daunkredit.program.sulu.view.fragment.presenter;

import android.text.TextUtils;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.ProgressBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.view.fragment.CertifiFraProView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 15:30
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class CertifiFraProPreImp extends BaseFragmentPresenterImpl implements CertifiFraProPresenter {
    public void downloadState() {
        String token = TokenManager.getInstance().getToken();
        final CertifiFraProView view = (CertifiFraProView) mView;
        if (TextUtils.isEmpty(token)) {
            view.resetProgress();
            return;
        }
        mView.getBaseActivity().showLoading(mView.getBaseActivity().getString(R.string.loading_loading));
        UserApi service = ServiceGenerator.createService(UserApi.class);
        service.progress(token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProgressBean>() {
                    @Override
                    public void onCompleted() {
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastManager.showToast(mView.getBaseActivity().getString(R.string.show_netwok_wrong));
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                    }

                    @Override
                    public void onNext(ProgressBean progressBean) {
                        if (!isAttached()) {
                            return;
                        }
                        view.onProgressBeaObtain(progressBean);
                        view.updateProgress();
                        view.updateSubmitState();
                        mView.getBaseActivity().dismissLoading();
                    }
                });

    }
}

package com.daunkredit.program.sulu.view.camera.presenter;

import android.util.Pair;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.FileUploadType;
import com.daunkredit.program.sulu.common.network.FileUploadUtil;
import com.daunkredit.program.sulu.view.camera.TakeVideoFraView;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 13:23
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TakeVideoFraPreImp extends BaseFragmentPresenterImpl implements TakeVideoFraPresenter {
    @Override
    public void submitVideo(final String videoPath) {
        mUserApi.getLatestLoanApp(TokenManager.getInstance().getToken(), "tag")
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoading(mView.getBaseActivity().getString(R.string.show_uploading));
                            }
                        });

                    }
                })
                .flatMap(new Func1<LatestLoanAppBean, Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>>>() {
                    @Override
                    public Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>> call(LatestLoanAppBean latestLoanAppBean) {
                        Logger.d("latest LoanAppId: " + latestLoanAppBean.getLoanAppId());
                        return uploadVideoObservable(latestLoanAppBean.getLoanAppId(), videoPath);
                    }
                })
                //                .doOnNext(new Action1<Pair<Call<ResponseBody>, Response<ResponseBody>>>() {
                //                    @Override
                //                    public void call(Pair<Call<ResponseBody>, Response<ResponseBody>> callResponsePair) {
                //                        api.getLatestLoanApp(TokenManager.getInstance().getToken(),"video")
                //                                .subscribe(new Action1<LatestLoanAppBean>() {
                //                                    @Override
                //                                    public void call(LatestLoanAppBean latestLoanAppBean) {
                //                                       MainActivity.latestLoanAppBean = latestLoanAppBean;
                //                                    }
                //                                });
                //                    }
                //                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Call<ResponseBody>, Response<ResponseBody>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("error: " + e.getMessage());
                            dismissLoading();
//                        XLeoToast.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(Pair<Call<ResponseBody>, Response<ResponseBody>> callResponsePair) {

                        ToastManager.showToast(mView.getBaseActivity().getString(R.string.show_upload_video_success) + callResponsePair.second.message());
                        uploadXTrace();
                        if (!isAttached()) {
                            return;
                        }
                        dismissLoading();
                        RxBus.get().post(new EventCollection.LoanSuccess());
                        if (isAttached()) {
                            ((TakeVideoFraView)mView).onVideoSubmitted();
                        }
                    }
                });
    }

    Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>> uploadVideoObservable(String loanAppId, String videoPath) {
        return FileUploadUtil.uploadVideoFile(
                FileUploadType.CONTRACT_VIDEO,
                videoPath,
                TokenManager.getInstance().getToken(),
                loanAppId);


    }
}

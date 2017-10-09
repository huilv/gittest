package com.daunkredit.program.sulu.view.presenter;

import android.Manifest;
import android.text.TextUtils;

import com.appsflyer.AppsFlyerLib;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.VersionBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.update.UpdateManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.enums.LoanStatus;
import com.daunkredit.program.sulu.harvester.HarvestInfoManager;
import com.daunkredit.program.sulu.harvester.PermissionManager;
import com.daunkredit.program.sulu.view.MainActView;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.Duration;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.orhanobut.logger.Logger;

import org.afinal.simplecache.ACache;

import rx.Subscriber;

import static com.daunkredit.program.sulu.enums.LoanStatus.UNLOAN;

/**
 * @作者:My
 * @创建日期: 2017/6/21 18:10
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MainActPreImp extends BaseActivityPresenterImpl implements MainActPresenter {

    private static final int TABLOAN   = 0;
    private static final int TABCERT   = 1;
    private static final int TABME     = 2;
    private static final int TABONLINE = 3;

    private static final int LOANING = 4;
    @Override
    public void dealResult(String loanStatus) {
        if (!isAttached()) {
            return;
        }
        MainActView view = (MainActView) mView;
        TokenManager tokenManager = TokenManager.getInstance();
        if (loanStatus == null) {
            Logger.d("loanStatus is null");
            tokenManager.storeMessage(FieldParams.LOANSTATUS, UNLOAN);
        } else if ("OVERDUE".equals(loanStatus)) {
            tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.EXPIRED);
        } else if ("CURRENT".equals(loanStatus)) {
            LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
            String loanAppId = latestLoanAppBean.getLoanAppId();
            String asString = ACache.get(mView.getBaseActivity()).getAsString(FieldParams.LOANRESULT);
            if (loanAppId != null && !TextUtils.equals(loanAppId, asString)) {
                view.showTipsDialog("CURRENT");
                ACache.get(mView.getBaseActivity()).put(FieldParams.LOANRESULT, loanAppId);
            }
            tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANED);
        } else if ("SUBMITTED".equals(loanStatus)) {
            tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.SUBMITTED);
        } else if ("IN_REVIEW".equals(loanStatus) ||
                "READY_TO_ISSUE".equals(loanStatus) ||
                "APPROVED".equals(loanStatus) ||
                "ISSUING".equals(loanStatus)) {
            tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANINPROCESS);
        } else if (loanStatus.equals("NOLOANAPP") ||
                loanStatus.equals("CANCELED") ||
                loanStatus.equals("PAID_OFF")
                ) {
            tokenManager.storeMessage(FieldParams.LOANSTATUS, UNLOAN);
        } else if ("REJECTED".equals(loanStatus)) {
            LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
            String loanAppId = latestLoanAppBean.getLoanAppId();
            String asString = ACache.get(mView.getBaseActivity()).getAsString(FieldParams.LOANRESULT);
            if (loanAppId != null && !TextUtils.equals(loanAppId, asString)) {
                view.showTipsDialog("REJECTED");
                ACache.get(mView.getBaseActivity()).put(FieldParams.LOANRESULT, loanAppId);
            }
            tokenManager.storeMessage(FieldParams.LOANSTATUS, UNLOAN);
        } else if (loanStatus.equals("CLOSED")) {
            LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
            String loanAppId = latestLoanAppBean.getLoanAppId();
            String asString = ACache.get(mView.getBaseActivity()).getAsString(FieldParams.LOANRESULT);
            if (loanAppId != null && !TextUtils.equals(loanAppId, asString)) {
                view.showTipsDialog("CLOSED");
                ACache.get(mView.getBaseActivity()).put(FieldParams.LOANRESULT, loanAppId);
            }
            tokenManager.storeMessage(FieldParams.LOANSTATUS, UNLOAN);
        } else {
            tokenManager.storeMessage(FieldParams.LOANSTATUS, UNLOAN);
        }
        Logger.d("dismissloading....");
        dismissLoading();

    }

    @Override
    public void updateApk(VersionBean versonBean) {
        new UpdateManager(mView.getBaseActivity(), versonBean).start();
    }

    public void updateStatus(final String token) {
        final MainActView view = (MainActView) mView;
        showLoading(null);
//        mUserApi.getLatestLoanApp(token, "Main")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
        TokenManager.DataUpdateManager.requestLoanLatestData(10,
                        new Subscriber<LatestLoanAppBean>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("MainActivity:updatestatus--oncompleted");
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("MainActivity: updatestatus--" + e.getMessage());
                        dismissLoading();
                        TokenManager.putMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
                        if (isAttached()) {
                            view.setSelect(TABLOAN);
                        }
                    }

                    @Override
                    public void onNext(LatestLoanAppBean latestLoanAppBean) {
                        LoggerWrapper.d("onnext");
                        TokenManager tokenManager = TokenManager.getInstance();
                        tokenManager.storeMessage(FieldParams.LATESTBEAN, latestLoanAppBean);
                        dealResult(latestLoanAppBean.getStatus());
                        Logger.d("MainActivity: updatestatus--" + "success");
                        if (isAttached()) {
                            view.setSelect(TABLOAN);
                        }

                    }
                });
    }

    @Override
    public void initThirdService() {
        AppsFlyerLib.getInstance().startTracking(mView.getBaseActivity().getApplication(), "ynLJnCRQMzbG8ncHcBWnwh");
        new AppUpdater(mView.getBaseActivity())
                .setDisplay(Display.DIALOG)
                .setDuration(Duration.NORMAL)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .start();


        if (TokenManager.getInstance().hasLogin() && HarvestInfoManager.getInstance().hasImei()) {
            try {
                if (!HarvestInfoManager.getInstance().hasContact()) {
                    new PermissionManager().fetchPersonInfo(mView.getBaseActivity(), Manifest.permission.READ_CONTACTS);
                }

                if (!HarvestInfoManager.getInstance().hasCallLogs()) {
                    new PermissionManager().fetchPersonInfo(mView.getBaseActivity(), Manifest.permission.READ_CALL_LOG);
                }

                if (!HarvestInfoManager.getInstance().hasSms()) {
                    new PermissionManager().fetchPersonInfo(mView.getBaseActivity(), Manifest.permission.READ_SMS);
                }
                if (!HarvestInfoManager.getInstance().hasLocation()) {
                    new PermissionManager().fetchPersonInfo(mView.getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!HarvestInfoManager.getInstance().hasInstallAppInfo()) {
                    Logger.d("begin get install app info");
                    HarvestInfoManager.getInstance().setInstallAppStatus("0");
                    HarvestInfoManager.getInstance().setInstallAppJsonobject(PermissionManager.getInstallApp(mView.getBaseActivity()));
                }

                if (!HarvestInfoManager.getInstance().hasMachineType()) {
                    HarvestInfoManager.getInstance().setMachineTypeStatus("0");
                    HarvestInfoManager.getInstance().setMachineTypeJsonobject(PermissionManager.getMachineType(mView.getBaseActivity()));
                }
            } catch (Exception e) {
                Logger.d("Exception: " + e.getMessage());
            }

        }
    }
}

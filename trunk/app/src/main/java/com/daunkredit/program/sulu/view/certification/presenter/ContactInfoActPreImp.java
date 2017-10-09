package com.daunkredit.program.sulu.view.certification.presenter;

import android.Manifest;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.harvester.HarvestInfoManager;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.daunkredit.program.sulu.harvester.PermissionManager.formSend;
import static com.daunkredit.program.sulu.harvester.PermissionManager.getCallLogs;
import static com.daunkredit.program.sulu.harvester.PermissionManager.getContactInfo;

/**
 * @作者:My
 * @创建日期: 2017/6/21 13:32
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class ContactInfoActPreImp extends BaseActivityPresenterImpl implements ContactInfoActPresenter {
    @Override
    public void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(mView.getBaseActivity());

        rxPermissions.requestEach(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)
                .doOnNext(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.name.equals(Manifest.permission.READ_CONTACTS)) {
                            if (permission.granted && !HarvestInfoManager.getInstance().hasContact() ) {
                                HarvestInfoManager.getInstance().setContactStatus("0");
                                HarvestInfoManager.getInstance().setContactJsonobject(getContactInfo(mView.getBaseActivity()));
                            }
                        } else if (permission.name.equals(Manifest.permission.READ_CALL_LOG)) {
                            if (permission.granted && !HarvestInfoManager.getInstance().hasCallLogs()) {
                                HarvestInfoManager.getInstance().setCallLogsStatus("0");
                                HarvestInfoManager.getInstance().setCallLogsJsonobject(getCallLogs(mView.getBaseActivity()));
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Permission>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("Exception: "+ e.getMessage());

                    }

                    @Override
                    public void onNext(Permission permission) {
                        if (!isAttached()) {
                            return;
                        }
                        if (permission.name.equals(Manifest.permission.READ_CONTACTS)) {
                            if (permission.granted) {
                                if (HarvestInfoManager.getInstance().hasContact() && "0".equals(HarvestInfoManager.getInstance().getContactStatus())) {
                                    formSend(mView.getBaseActivity(), ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getContactJsonobject());
                                }

                            }

                        } else if (permission.name.equals(Manifest.permission.READ_CALL_LOG)) {
                            if (permission.granted) {
                                if (HarvestInfoManager.getInstance().hasCallLogs() && "0".equals(HarvestInfoManager.getInstance().getCallLogsStatus())) {
                                    formSend(mView.getBaseActivity(), ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getCallLogsJsonobject());
                                }
                            }
                        }
                    }
                });
    }
}

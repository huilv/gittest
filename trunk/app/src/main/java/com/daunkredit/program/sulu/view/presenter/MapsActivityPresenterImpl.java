package com.daunkredit.program.sulu.view.presenter;

import android.content.Context;
import android.location.Address;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.GeoLocationBean;
import com.daunkredit.program.sulu.common.network.GeoLocationApi;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.view.MapsActivityView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/7/20 15:39
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MapsActivityPresenterImpl extends BaseActivityPresenterImpl implements MapsActivityPresenter {

    @Override
    public void getAddressesByGetCall(double latitude, double longitude) {
        GeoLocationApi.getLocationByLocation(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GeoLocationBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e("error",e);
                        XLeoToast.showMessage(R.string.tips4inputaddress);
                    }

                    @Override
                    public void onNext(GeoLocationBean geoLocationBean) {
                        LoggerWrapper.e("geo data bean:" + geoLocationBean.getResults().get(0).getFormatted_address() );
                        if (mView != null) {
                            ((MapsActivityView)mView).onAddressesObtainByGet(geoLocationBean);
                        }
                    }
                });
    }

    @Override
    public void getAddressesByGeoCoder(Context mapsActivity, double latitude, double longitude) {
        GeoLocationApi.getAddresses(mapsActivity,latitude,longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Address>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.d(e);
                        if (mView != null) {
                            ((MapsActivityView)mView).onAddressesObtainError(e);
                        }
                    }

                    @Override
                    public void onNext(List<Address> addresses) {
                        LoggerWrapper.e("geo data bean:" + addresses.get(0).getAdminArea());
                        if (mView != null) {
                            ((MapsActivityView)mView).onAddressesObtain(addresses);
                        }
                    }
                });
    }
}

package com.daunkredit.program.sulu.view;

import android.location.Address;

import com.daunkredit.program.sulu.app.base.BaseActivityView;
import com.daunkredit.program.sulu.bean.GeoLocationBean;

import java.util.List;

/**
 * @作者:My
 * @创建日期: 2017/7/20 15:37
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MapsActivityView extends BaseActivityView {
    void onAddressesObtain(List<Address> addresses);

    void onAddressesObtainError(Throwable e);

    void onAddressesObtainByGet(GeoLocationBean geoLocationBean);
}

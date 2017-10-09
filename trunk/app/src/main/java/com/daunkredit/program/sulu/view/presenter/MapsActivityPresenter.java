package com.daunkredit.program.sulu.view.presenter;

import android.content.Context;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;

/**
 * @作者:My
 * @创建日期: 2017/7/20 15:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MapsActivityPresenter extends BaseActivityPresenter {
    void getAddressesByGetCall(double latitude, double longitude);

    void getAddressesByGeoCoder(Context mapsActivity, double latitude, double longitude);
}

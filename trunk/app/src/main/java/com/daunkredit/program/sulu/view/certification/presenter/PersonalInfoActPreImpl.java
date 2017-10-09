package com.daunkredit.program.sulu.view.certification.presenter;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.PersonalInfoBean;
import com.daunkredit.program.sulu.bean.RegionBean;
import com.daunkredit.program.sulu.common.InfoAdapterString;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.certification.status.RegionLevel;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.orhanobut.logger.Logger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.daunkredit.program.sulu.view.certification.status.RegionLevel.area;
import static com.daunkredit.program.sulu.view.certification.status.RegionLevel.province;

/**
 * @作者:My
 * @创建日期: 2017/6/21 13:40
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class PersonalInfoActPreImpl extends BaseActivityPresenterImpl implements PersonalInfoActPresenter {
    /**
     * Set province
     */
    public InfoAdapterString getProvinceAdapter() {
        final InfoAdapterString provinceAdapter = new InfoAdapterString(mView.getBaseActivity());

        mUserApi.getRegion(province.toString(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegionBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLeoToast.showMessage(R.string.show_get_region_faild);
                        Logger.d("Get province failed");
                    }

                    @Override
                    public void onNext(RegionBean regionBean) {
                        for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                            provinceAdapter.addItem(regionsBean.getName(), InfoType.PROVINCE, regionsBean.getId(), regionsBean.getLevel());
                        }
                    }
                });

        return provinceAdapter;
    }

    /**
     * Set city
     */
    public InfoAdapterString getCityAdapter(PersonalInfoBean personalInfoBean) {
        final InfoAdapterString cityAdapter = new InfoAdapterString(mView.getBaseActivity());

        if (personalInfoBean.getProvince() == null) {
            XLeoToast.showMessage(R.string.show_input_province_tips);
        } else {
            mUserApi.getRegion(RegionLevel.city.toString(), personalInfoBean.getProvince().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RegionBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            XLeoToast.showMessage(R.string.show_get_region_faild);
                            Logger.d("Get city failed");
                        }

                        @Override
                        public void onNext(RegionBean regionBean) {
                            for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                                cityAdapter.addItem(regionsBean.getName(), InfoType.CITY, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });

        }

        return cityAdapter;
    }

    /**
     * Set street
     */
    public InfoAdapterString getStreetAdapter(PersonalInfoBean personalInfoBean) {
        final InfoAdapterString streetAdapter = new InfoAdapterString(mView.getBaseActivity());

        if (personalInfoBean.getCity() == null) {
            XLeoToast.showMessage(R.string.show_input_city_first);
        } else {
            mUserApi.getRegion(RegionLevel.district.toString(), personalInfoBean.getCity().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RegionBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            XLeoToast.showMessage(R.string.show_get_region_faild);
                            Logger.d("Get street failed");
                        }

                        @Override
                        public void onNext(RegionBean regionBean) {
                            for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                                streetAdapter.addItem(regionsBean.getName(), InfoType.STREET, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });

        }
        return streetAdapter;
    }

    /**
     * Set area
     */
    public InfoAdapterString getAreaAdapter(PersonalInfoBean personalInfoBean) {
        final InfoAdapterString areaAdapter = new InfoAdapterString(mView.getBaseActivity());
        if (personalInfoBean.getDistrict() == null) {
            XLeoToast.showMessage(R.string.show_input_street_first);
        } else {
            mUserApi.getRegion(area.toString(), personalInfoBean.getDistrict().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RegionBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            XLeoToast.showMessage(R.string.show_get_region_faild);
                            Logger.d("Get area failed");
                        }

                        @Override
                        public void onNext(RegionBean regionBean) {
                            for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                                areaAdapter.addItem(regionsBean.getName(), InfoType.AREA, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });

        }
        return areaAdapter;
    }




}

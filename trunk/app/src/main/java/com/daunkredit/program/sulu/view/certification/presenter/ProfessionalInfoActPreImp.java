package com.daunkredit.program.sulu.view.certification.presenter;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.EmploymentBean;
import com.daunkredit.program.sulu.bean.RegionBean;
import com.daunkredit.program.sulu.common.InfoAdapterString;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.certification.status.RegionLevel;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:11
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class ProfessionalInfoActPreImp extends BaseActivityPresenterImpl implements ProfessionalInfoActPresenter {

    /**
     * Set company province adapter
     * @return
     */

    public InfoAdapterString getCompanyProvinceAdapter(EmploymentBean employmentBean){
        final InfoAdapterString companyProvinceAdapter = new InfoAdapterString(mView.getBaseActivity());

        mUserApi.getRegion(RegionLevel.province.toString(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegionBean>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        XLeoToast.showMessage(R.string.show_get_region_faild);
                        Logger.d("Get company province failed");
                    }

                    @Override
                    public void onNext(RegionBean regionBean) {
                        for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                            companyProvinceAdapter.addItem(regionsBean.getName(), InfoType.C_PROVINCE, regionsBean.getId(), regionsBean.getLevel());
                        }
                    }
                });

        return companyProvinceAdapter;
    }


    /**
     * Set company city adapter
     * @return
     */

    public InfoAdapterString getCompanyCityAdapter(EmploymentBean employmentBean){
        final InfoAdapterString companyCityAdapter = new InfoAdapterString(mView.getBaseActivity());

        if (employmentBean.getCompanyProvince() == null) {
            XLeoToast.showMessage(R.string.show_input_province_tips);
        } else {
            mUserApi.getRegion(RegionLevel.city.toString(), employmentBean.getCompanyProvince().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RegionBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            XLeoToast.showMessage(R.string.show_get_region_faild);
                            Logger.d("Get conpany city failed");
                        }

                        @Override
                        public void onNext(RegionBean regionBean) {
                            for (RegionBean.RegionsBean regionsBean : regionBean.getRegions()) {
                                companyCityAdapter.addItem(regionsBean.getName(), InfoType.C_CITY, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });

        }
        return companyCityAdapter;
    }

    /**
     * Set company street adapter
     * @return
     */

    public InfoAdapterString getCompanyStreetAdapter(EmploymentBean employmentBean) {
        final InfoAdapterString companyStreetAdapter = new InfoAdapterString(mView.getBaseActivity());
        if (employmentBean.getCompanyCity() == null) {
            XLeoToast.showMessage(R.string.show_input_city);
        } else {
            mUserApi.getRegion(RegionLevel.district.toString(), employmentBean.getCompanyCity().getId())
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
                                companyStreetAdapter.addItem(regionsBean.getName(), InfoType.C_STREET, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });
        }
        return companyStreetAdapter;
    }


    /**
     * Set company area adapter
     * @return
     */

    public InfoAdapterString getCompanyAreaAdapter(EmploymentBean employmentBean){
        final InfoAdapterString companyAreaAdapter = new InfoAdapterString(mView.getBaseActivity());
        if (employmentBean.getCompanyDistrict() == null) {
           XLeoToast.showMessage(R.string.show_input_street_first);
        } else {
            mUserApi.getRegion(RegionLevel.area.toString(), employmentBean.getCompanyDistrict().getId())
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
                                companyAreaAdapter.addItem(regionsBean.getName(), InfoType.C_AREA, regionsBean.getId(), regionsBean.getLevel());
                            }
                        }
                    });

        }
        return companyAreaAdapter;
    }
//mbdlv.getText().toString() + companyTel.getText().toString().trim()
    @Override
    public void uploadJobInfo(EmploymentBean employmentBean, TextView companyStreet) {
        mUserApi.submitEmploymentInfo(employmentBean.getCompanyName()
                ,employmentBean.getCompanyProvince().getName()
                ,employmentBean.getCompanyCity().getName()
                ,companyStreet.getText().toString()
                ,employmentBean.getCompanyArea().getName()
                ,employmentBean.getCompanyAddress()
                , employmentBean.getCompanyPhone()
                ,employmentBean.getJobType()
                ,employmentBean.getSalary()
                , TokenManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mView.getBaseActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                        Logger.d("submit personall info failed.");
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Logger.d("Submit success!");
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().setResult(Activity.RESULT_OK);
                        mView.getBaseActivity().dismissLoading();
                        mView.getBaseActivity().finish();

                    }
                });
    }

}

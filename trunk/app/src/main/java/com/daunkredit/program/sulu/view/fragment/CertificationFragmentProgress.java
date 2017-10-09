package com.daunkredit.program.sulu.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.bean.ContactInfoBean;
import com.daunkredit.program.sulu.bean.ProgressBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.MainActivity;
import com.daunkredit.program.sulu.view.certification.ContactInfoActivity;
import com.daunkredit.program.sulu.view.certification.PersonalInfoActivity;
import com.daunkredit.program.sulu.view.certification.ProfessionalInfoActivity;
import com.daunkredit.program.sulu.view.certification.UploadPhotoActivity;
import com.daunkredit.program.sulu.view.certification.timeline.OrderStatus;
import com.daunkredit.program.sulu.view.certification.timeline.TimeLineAdapter;
import com.daunkredit.program.sulu.view.certification.timeline.TimeLineModel;
import com.daunkredit.program.sulu.view.certification.timeline.TimeLineViewHolder;
import com.daunkredit.program.sulu.view.fragment.presenter.CertifiFraProPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.CertifiFraProPresenter;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * Created by Miaoke on 2017/2/20.
 */

public class CertificationFragmentProgress extends BaseFragment<CertifiFraProPresenter> implements CertifiFraProView {

    private static final int REQUEST_PERSONAL = 1100;
    private static final int REQUEST_PROFESSIONAL = 1101;
    private static final int REQUEST_CONTACT = 1102;
    private static final int REQUEST_PHOTO = 1103;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.id_button_certification_submit)
    Button submit;
    @BindView(R.id.id_progressbar_certification)
    ProgressBar mIdProgressbarCertification;
    @BindView(R.id.id_textview_data_integrity)
    TextView mIdTextviewDataIntegrity;
    @BindView(R.id.id_textview_rating)
    TextView mIdTextviewRating;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();

    @BindString(R.string.textview_field_personal_info)
    String personalInfo;

    @BindString(R.string.textview_field_professional_info)
    String professionalInfo;

    @BindString(R.string.textview_field_contract_info)
    String contractInfo;

    @BindString(R.string.textview_field_Upload_photos)
    String uploadPhtos;
    private ProgressBean mProgressBean;


    public static ContactInfoBean mContactInfoBean;

    final UserApi api = ServiceGenerator.createService(UserApi.class);
    private long mCheckTime;
    private boolean doActive;

    @Override
    protected CertifiFraProPresenter initPresenter() {
        return new CertifiFraProPreImp();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_cert;
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    @Override
    protected boolean initHeader(TextView view) {
        view.setText(R.string.textview_authentication);
        return false;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mActivity, mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        mProgressBean = new ProgressBean();
        checkState();

    }

    private void checkState() {

        if (MainActivity.isSubmitVisible == false) {
            Logger.d("Button invisible");
            submit.setVisibility(View.INVISIBLE);
        } else {
            Logger.d("Button visible");
            submit.setVisibility(View.VISIBLE);
            updateSubmitState();
        }

        TokenManager instance = TokenManager.getInstance();
        boolean loginStateChange = instance.isLoginStateChange(mCheckTime);
        mCheckTime = System.currentTimeMillis();
        if (loginStateChange) {
            if (instance.hasLogin()) {
                mPresenter.downloadState();
            } else {
                resetProgress();
            }
        }

    }


    public void resetProgress() {
        mProgressBean = new ProgressBean();
        mProgressBean.setEmploymentPart(false);
        mProgressBean.setPersonalInfoPart(false);
        mProgressBean.setFilePart(false);
        mProgressBean.setContactPart(false);
        updateProgress();
    }


    @Override
    protected void initData() {

    }

    public void updateProgress() {
        doActive = false;
        int progress = 0;
        if (mProgressBean.isPersonalInfoPart()) {
            mDataList.get(0).setStatus(OrderStatus.COMPLETED);
            progress += 25;
        } else if (!mProgressBean.isPersonalInfoPart()) {
            mDataList.get(0).setStatus(OrderStatus.ACTIVE);
            doActive = true;
        }

        if (mProgressBean.isEmploymentPart()) {
            progress += 25;
            mDataList.get(1).setStatus(OrderStatus.COMPLETED);
        } else if (!mProgressBean.isEmploymentPart()) {
            if (doActive) {
                mDataList.get(1).setStatus(OrderStatus.INACTIVE);
            } else {
                mDataList.get(1).setStatus(OrderStatus.ACTIVE);
                doActive = true;
            }
        }

        if (mProgressBean.isContactPart()) {
            progress += 25;
            mDataList.get(2).setStatus(OrderStatus.COMPLETED);
        } else if (!mProgressBean.isContactPart()) {
            if (doActive) {
                mDataList.get(2).setStatus(OrderStatus.INACTIVE);
            } else {
                mDataList.get(2).setStatus(OrderStatus.ACTIVE);
                doActive = true;
            }
        }

        if (mProgressBean.isFilePart()) {
            progress += 25;
            mDataList.get(3).setStatus(OrderStatus.COMPLETED);
        } else if (!mProgressBean.isFilePart()) {
            if (doActive) {
                mDataList.get(3).setStatus(OrderStatus.INACTIVE);
            } else {
                mDataList.get(3).setStatus(OrderStatus.ACTIVE);
                doActive = true;
            }
        }

        updateSubmitState();

        mIdProgressbarCertification.setProgress(progress);
        mIdTextviewRating.setText(progress + "%");
        if (mTimeLineAdapter != null) {
            mTimeLineAdapter.notifyDataSetChanged();
        }
    }

    public void updateSubmitState() {
        if (submit != null && submit.getVisibility() == View.VISIBLE) {

            if (checkoutComplete()) {
                submit.setClickable(true);
                submit.setAlpha(0.8f);
            } else {
                submit.setClickable(false);
                submit.setAlpha(0.3f);
            }
        }
    }

    @Override
    public void onProgressBeaObtain(ProgressBean progressBean) {
        mProgressBean = progressBean;
    }

    private boolean checkoutComplete() {
        if (mProgressBean != null && mProgressBean.isCompleted()) {
            return true;
        } else if (mProgressBean != null && mProgressBean.isContactPart() && mProgressBean.isEmploymentPart()
                && mProgressBean.isFilePart() && mProgressBean.isPersonalInfoPart()) {
            mProgressBean.setCompleted(true);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            mProgressBean.setFilePart(true);
            updateProgress();
        } else if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK) {
            mProgressBean.setContactPart(true);
            updateProgress();
        } else if (requestCode == REQUEST_PROFESSIONAL && resultCode == Activity.RESULT_OK) {
            mProgressBean.setEmploymentPart(true);
            updateProgress();
        } else if (requestCode == REQUEST_PERSONAL && resultCode == Activity.RESULT_OK) {
            mProgressBean.setPersonalInfoPart(true);
            updateProgress();
        } else {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDataListItems() {
        mDataList.add(new TimeLineModel(
                TimeLineModel.CertType.PERSONAL,
                R.drawable.cert_status_icon_personal_level,
                personalInfo,
                OrderStatus.INACTIVE));

        mDataList.add(new TimeLineModel(
                TimeLineModel.CertType.PROFESSIONAL,
                R.drawable.cert_status_icon_professional_level,
                professionalInfo,
                OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel(
                TimeLineModel.CertType.CONTACT,
                R.drawable.cert_status_icon_contact_level,
                contractInfo,
                OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel(TimeLineModel.CertType.UPLOAD_PHOTOS,
                R.drawable.cert_status_icon_upload_photos_level,
                uploadPhtos,
                OrderStatus.INACTIVE));
    }

    @Subscribe
    public void onCertItemClicked(TimeLineViewHolder.CertItemClickedEvent event) {
        if (!mActivity.isLogin()) {
            mActivity.changeTo(LoginActivity.class);
            return;
        }

//        Logger.d(getClass().getName() + "  " + event.getCertType());
        if (event.getCertType() == TimeLineModel.CertType.PERSONAL) {
            mActivity.changeToForResult(PersonalInfoActivity.class, REQUEST_PERSONAL);

        } else if (event.getCertType() == TimeLineModel.CertType.PROFESSIONAL) {
            mActivity.changeToForResult(ProfessionalInfoActivity.class, REQUEST_PROFESSIONAL);

        } else if (event.getCertType() == TimeLineModel.CertType.CONTACT) {
            api.getContactInfo(TokenManager.getInstance().getToken())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mActivity != null && isAdded()) {
                                        mActivity.showLoading(getString(R.string.loading_loading));
                                    }
                                }
                            });
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ContactInfoBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mActivity != null && isAdded()) {
                                mActivity.dismissLoading();
                            }
                            Logger.d("Get ContactInfo Failed: " + e.getMessage());
                            ToastManager.showToast(getString(R.string.show_get_contactinfo_fail));
                        }

                        @Override
                        public void onNext(ContactInfoBean contactInfoBean) {
                            if (mActivity != null && isAdded()) {
                                mActivity.dismissLoading();
                            }
                            mContactInfoBean = contactInfoBean;
                            mActivity.changeToForResult(ContactInfoActivity.class, REQUEST_CONTACT);
                        }
                    });

        } else if (event.getCertType() == TimeLineModel.CertType.UPLOAD_PHOTOS) {
            mActivity.changeToForResult(UploadPhotoActivity.class, REQUEST_PHOTO);
        }
    }

    @OnClick(R.id.id_button_certification_submit)
    public void loaning() {
        UserEventQueue.add(new ClickEvent(submit.toString(), ActionType.CLICK, submit.getText().toString()));
        Logger.d(getClass().getName() + " start loaningFragment");
        RxBus.get().post(new GotoLoaning());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            checkState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        MainActivity.isSubmitVisible = false;
    }

    public static class GotoLoaning {
    }
}


package com.daunkredit.program.sulu.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.App;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.fragment.presenter.MeFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.MeFraPresenter;
import com.daunkredit.program.sulu.view.me.AboutActivity;
import com.daunkredit.program.sulu.view.me.HelpCenterActivity;
import com.daunkredit.program.sulu.view.me.MyInvitedInfoActivity;
import com.daunkredit.program.sulu.view.me.MyLoanActivity;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.sulu.kotlin.activity.ActivityDetailAcivity;
import com.sulu.kotlin.activity.MyActivityCenterActivity;
import com.sulu.kotlin.activity.MyCouponActivity;
import com.sulu.kotlin.activity.MyOnLineServiceActivity;
import com.sulu.kotlin.data.ActivityInfoBean;
import com.sulu.kotlin.data.MeInfoBean;
import com.x.leo.rollview.OnItemClickListener;
import com.x.leo.rollview.RollView;
import com.x.leo.rollview.RollViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;

import static com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager.newDialog;

/**
 * Created by Miaoke on 2017/2/24.
 */

public class MeFragment extends BaseFragment<MeFraPresenter> implements View.OnClickListener, MeFraView {

    @BindView(R.id.id_textview_my_loan)
    TextView mIdTextviewMyLoan;
    @BindView(R.id.id_imagebutton_my_loan)
    ImageButton mIdImagebuttonMyLoan;
    @BindView(R.id.ll_myloan)
    LinearLayout mLlMyloan;
    @BindView(R.id.id_textview_pusat_kegiatan)
    TextView mPusat_Kegiatan;
    @BindView(R.id.id_imagebutton_security_setting)
    ImageButton mIdImagebuttonSecuritySetting;
    @BindView(R.id.ll_activity_center)
    LinearLayout mLlActivityCenter;
    @BindView(R.id.id_textview_help_center)
    TextView mIdTextviewHelpCenter;
    @BindView(R.id.id_imagebutton_help_center)
    ImageButton mIdImagebuttonHelpCenter;
    @BindView(R.id.ll_help)
    LinearLayout mLlHelp;
    @BindView(R.id.id_textview_about)
    TextView mIdTextviewAbout;
    @BindView(R.id.id_imagebutton_about)
    ImageButton mIdImagebuttonAbout;
    @BindView(R.id.ll_about)
    LinearLayout mLlAbout;
    @BindView(R.id.id_textview_customer_service_hotline)
    TextView mIdTextviewCustomerServiceHotline;
    @BindView(R.id.ll_hotline)
    LinearLayout mLlHotline;
    @BindView(R.id.sv_me)
    ScrollView mSvMe;
    @BindView(R.id.id_textview_online_service)
    TextView mOnlineService;
    @BindView(R.id.id_imagebutton_my_repayment_log)
    ImageButton mIdImagebuttonMyRepaymentLog;
    @BindView(R.id.ll_online_service_me)
    LinearLayout mllOnline_service;
    @BindView(R.id.tv_uername_me_fragment)
    TextView tvUsername;
    @BindView(R.id.btn_login_out)
    TextView btnLogin;
    @BindView(R.id.ll_userspan_me_fragment)
    LinearLayout llUserSpan;
    @BindView(R.id.rv_me)
    RollView mHeaderRoller;
    @BindView(R.id.tv_num_kupon)
    TextView tvNumKupon;
    @BindView(R.id.ll_kupon)
    LinearLayout llKupon;
    @BindView(R.id.tv_num_undang_teman)
    TextView tvNumUndangTeman;
    @BindView(R.id.ll_undang_teman)
    LinearLayout llUndangTeman;
    private RollViewAdapter mHeaderRollerAdapter;
    private ArrayList<String> mDatas;
    private ArrayList<ActivityInfoBean> dataBanner;

    @Override
    protected MeFraPresenter initPresenter() {
        return new MeFraPreImp();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_me;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mLlMyloan.setOnClickListener(this);
        mLlAbout.setOnClickListener(this);
        mLlHelp.setOnClickListener(this);
        mLlHotline.setOnClickListener(this);
        mLlActivityCenter.setOnClickListener(this);
        mllOnline_service.setOnClickListener(this);
        llKupon.setOnClickListener(this);
        llUndangTeman.setOnClickListener(this);
        mDatas = new ArrayList<>();
        mHeaderRollerAdapter = new RollViewAdapter(getContext(),mDatas);
        mHeaderRollerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int data) {
                Intent intent = new Intent(getContext(), ActivityDetailAcivity.class);
                intent.putExtra(FieldParams.ACTIVITY_DETAIL_IMAGE_URL, dataBanner.get(data).getUrl());
                startActivity(intent);
            }
        });
        mHeaderRoller.setAdapter(mHeaderRollerAdapter);
        initNotLoginUserInfo();
    }

    @Override
    protected void initData() {
        mPresenter.initUserAccountInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.initUserAccountInfo();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_myloan:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.textview_me_my_loan)));
                mActivity.changeTo(new Intent(getContext(), MyLoanActivity.class), true);
                break;
            case R.id.ll_online_service_me:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.onlineQA)));
                mActivity.changeTo(new Intent(getContext(),MyOnLineServiceActivity.class),true);
                break;
            case R.id.ll_about:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.textview_me_about_company)));
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            case R.id.ll_help:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.textview_me_help_center)));
                startActivity(new Intent(getContext(), HelpCenterActivity.class));
                break;
            case R.id.ll_hotline:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.textview_me_customer_service_hotline)));
                showDialTips();
                break;
            case R.id.ll_activity_center:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.text_pusat_kegiatan)));
                startActivity(new Intent(getContext(), MyActivityCenterActivity.class));
                break;
            case R.id.ll_undang_teman:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, getString(R.string.undang_teman)));
                mActivity.changeTo(new Intent(getContext(),MyInvitedInfoActivity.class),true);
                break;
            case R.id.ll_kupon:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,getString(R.string.kupon)));
                mActivity.changeTo(new Intent(getContext(),MyCouponActivity.class),true);
                break;
            default:
        }
    }

    private void showDialTips() {
        View view = View.inflate(getContext(), R.layout.dialog_hotline, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.7f),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.3f)
        ));
        final Dialog dialog = newDialog(getContext(), view, false);
        dialog.show();
        TextView text = (TextView) view.findViewById(R.id.tv_hotline_text);
        text.setText(String.format(getString(R.string.show_dial_hotline), App.HOTLINE));
        view.findViewById(R.id.btn_hotline_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, ((TextView) v).getText().toString()));
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.btn_hotline_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, ((TextView) v).getText().toString()));
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + App.HOTLINE);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initUserInfo(MeInfoBean data) {
        if (TokenManager.getInstance().hasLogin()) {
            tvUsername.setText(data.getName() + "");
            btnLogin.setText(R.string.button_logout);
            tvNumKupon.setText(data.getAvailableCouponCount() + "");
            tvNumUndangTeman.setText(data.getInviteeCount() + "");
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogout();
                }
            });
        } else {
            initNotLoginUserInfo();
        }
        initRollAd(data);
    }

    private void initNotLoginUserInfo() {
        tvUsername.setText(R.string.button_login);
        btnLogin.setText(R.string.button_login);
        tvNumKupon.setText("0");
        tvNumUndangTeman.setText("0");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login();
            }
        });
    }

    private void initRollAd(MeInfoBean data) {
        dataBanner = data.getBanner();
        if (dataBanner != null && dataBanner.size() >0) {
            mDatas.clear();
            //dataBanner.add(0,new ActivityInfoBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503381298694&di=71cbfa592987f5a01d9f7fdaf37a72dc&imgtype=0&src=http%3A%2F%2Fi662.photobucket.com%2Falbums%2Fuu344%2FXS000%2Frtyheryiu46ti4o3toierutofgieruotgur.jpg"));
            for (ActivityInfoBean activityInfoBean : dataBanner) {
                mDatas.add(activityInfoBean.getUrl());
            }
            mHeaderRollerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean doLogout() {
        View view = View.inflate(getContext(), R.layout.dialog_question_for_result, null);
        final Dialog dialog = DialogManager.newDialog(getContext(), view, true);
        TextView tv_header = (TextView) view.findViewById(R.id.text_header);
        TextView tv_content = (TextView) view.findViewById(R.id.text_content);
        tv_content.setText("do you want to log out ?");
        tv_header.setText("log out");
        view.findViewById(R.id.ensure_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mPresenter.logout();
            }
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return false;
    }
}

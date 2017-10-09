package com.daunkredit.program.sulu.view.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.daunkredit.program.sulu.enums.LoanStatus;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.camera.TakeVideoActivity;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanInProFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanInProFraPresenter;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Miaoke on 2017/3/6.
 */

public class LoanInProcessFragment extends BaseFragment<LoanInProFraPresenter> implements LoanInProcessView {


    @BindView(R.id.id_textview_latest_loan_info)
    TextView idTextviewLatestLoanInfo;
    @BindView(R.id.id_linearlayout_loan_info)
    LinearLayout idLinearlayoutLoanInfo;
    @BindView(R.id.id_textview_process_loan_app_id)
    TextView idTextviewProcessLoanAppId;
    @BindView(R.id.id_textview_process_loan_app_time)
    TextView idTextviewProcessLoanAppTime;
    @BindView(R.id.id_textview_process_loan_app_amount)
    TextView idTextviewProcessLoanAppAmount;
    @BindView(R.id.id_textview_process_loan_app_period)
    TextView idTextviewProcessLoanAppPeriod;
    @BindView(R.id.id_textview_process_loan_app_cost)
    TextView idTextviewProcessLoanAppCost;
    @BindView(R.id.id_textview_process_loan_total_amount)
    TextView idTextviewProcessLoanTotalAmount;
    @BindView(R.id.id_textview_process_loan_app_bank_code)
    TextView idTextviewProcessLoanAppBankCode;
    @BindView(R.id.id_textview_process_loan_app_bank_no)
    TextView idTextviewProcessLoanAppBankNo;
    @BindView(R.id.id_textview_process_loan_app_ktp)
    TextView idTextviewProcessLoanAppKtp;
    @BindView(R.id.id_buttom_loaning_goto_video)
    Button idButtomLoaningGotoVideo;
    @BindView(R.id.lv_review_status)
    ListView lv_status;
    @BindView(R.id.ib_info_span_controller)
    ImageButton ibInfoSpanController;
    @BindView(R.id.ll_span_loan_progress)
    LinearLayout llSpanLoanProgress;
    private LoanAppBeanFather mLatestLoanAppBean;
    private ListAdapter mStatusAdapter;
    private Animator mAnimator;
    private float mCurrentAnimatedValue;
    private int mSpanHeight;

    @Override
    protected LoanInProFraPresenter initPresenter() {
        return new LoanInProFraPreImp();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_loan_in_process;
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        showView();
        mCurrentAnimatedValue = 1;
        getSpanHeight();
//        setSpanHeight(0.3f);
    }

    private void setSpanHeight(float percent) {
        int height = (int) (mSpanHeight * percent + 0.5f);
        llSpanLoanProgress.setLayoutParams(new LinearLayout.LayoutParams(llSpanLoanProgress.getWidth(),
                height));
        llSpanLoanProgress.invalidate();
    }

    private void getSpanHeight() {
        boolean notAllSame = false;
        int temInt = -1;
        for (int i = 0; i < llSpanLoanProgress.getChildCount(); i++) {
            View childAt = llSpanLoanProgress.getChildAt(i);
            childAt.measure(0, 0);
            int measuredHeight = childAt.getMeasuredHeight();
            if (temInt == -1) {
                temInt = measuredHeight;
            } else if (measuredHeight != temInt) {
                notAllSame = true;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            int margin = layoutParams.bottomMargin + layoutParams.topMargin;
            mSpanHeight += (measuredHeight + margin + 0.5f);
            Log.d(TAG, "getSpanHeight:\n measuredHeight:" + childAt.getMeasuredHeight() + "\nview:" + childAt.toString());
        }
        mSpanHeight += (llSpanLoanProgress.getPaddingBottom() + llSpanLoanProgress.getPaddingTop());
        if (!notAllSame) {
            mSpanHeight -= llSpanLoanProgress.getChildAt(0).getMeasuredHeight() / 2 * (llSpanLoanProgress.getChildCount() - 2);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showView();
        }
    }

    private void showView() {
        TokenManager instance = TokenManager.getInstance();
        mLatestLoanAppBean = (LatestLoanAppBean) instance
                .getMessage(FieldParams.LATESTBEAN);
        Object message = instance.getAndRemove(FieldParams.SHOW_MY_LOAN_INFO);
        if (message != null && message instanceof Boolean && (boolean) message) {
            LoanAppBeanFather loanBean = (LoanAppBeanFather) instance.getAndRemove(FieldParams.LOAN_APP_INFO_BEAN);
            if (loanBean != null) {
                mLatestLoanAppBean = loanBean;
            }
        }
        if (mLatestLoanAppBean == null) {
            return;
        }

        idTextviewProcessLoanAppId.setText(mLatestLoanAppBean.getLoanAppId());
        idTextviewProcessLoanAppKtp.setText(mLatestLoanAppBean.getCredentialNo());
        idTextviewProcessLoanAppTime.setText(TimeFormat.convertTime(mLatestLoanAppBean.getCreateTime()));
        idTextviewProcessLoanAppAmount.setText(StringFormatUtils.indMoneyFormat(mLatestLoanAppBean.getAmount()));
        idTextviewProcessLoanAppPeriod.setText(mLatestLoanAppBean.getPeriod() + getString(R.string.days));
        idTextviewProcessLoanAppCost.setText(StringFormatUtils.indMoneyFormat(mLatestLoanAppBean.getCost()));
        idTextviewProcessLoanTotalAmount.setText(StringFormatUtils.indMoneyFormat(mLatestLoanAppBean.getTotalAmount()));
        idTextviewProcessLoanAppBankCode.setText(mLatestLoanAppBean.getBankCode());
        idTextviewProcessLoanAppBankNo.setText(mLatestLoanAppBean.getCardNo());

        List<LatestLoanAppBean.StatusLogsBean> statusLogsBean = mLatestLoanAppBean.getStatusLogs();
        LoanStatus loanStatus = (LoanStatus) TokenManager.getInstance().getMessage(FieldParams.LOANSTATUS);
        if (loanStatus == LoanStatus.LOANINPROCESS) {
            if (idButtomLoaningGotoVideo.getVisibility() != View.INVISIBLE) {
                idButtomLoaningGotoVideo.setVisibility(View.INVISIBLE);
                Logger.d("Set button invisible.");
            }
        }
        if (loanStatus == LoanStatus.SUBMITTED) {
            if (idButtomLoaningGotoVideo.getVisibility() != View.VISIBLE)
                idButtomLoaningGotoVideo.setVisibility(View.VISIBLE);
            Logger.d("Set button visible.");
        }

        if (mLatestLoanAppBean.getStatusLogs() != null && mLatestLoanAppBean.getStatusLogs().size() > 0) {
            showCurrentStatusList(mLatestLoanAppBean.getStatusLogs());
        }

    }

    private void showCurrentStatusList(List<LoanAppBeanFather.StatusLogsBean> statusLogs) {
        mStatusAdapter = new LoanInProcessStatusAdapter(statusLogs, this);
        lv_status.setAdapter(mStatusAdapter);
        ViewGroup.LayoutParams layoutParams = lv_status.getLayoutParams();
        layoutParams.height = 0;
        View view = null;
        for (int i = 0; i < mStatusAdapter.getCount(); i++) {
            view = mStatusAdapter.getView(i, null, lv_status);
            view.measure(0, 0);
            layoutParams.height += view.getMeasuredHeight();
            view = null;
        }
        lv_status.setLayoutParams(layoutParams);
    }


    @Override
    protected void initData() {

    }


    @OnClick(R.id.id_buttom_loaning_goto_video)
    public void gotoVideo() {
        UserEventQueue.add(new ClickEvent(idButtomLoaningGotoVideo.toString(), ActionType.CLICK, idButtomLoaningGotoVideo.getText().toString()));
        Intent intent = new Intent(mActivity, TakeVideoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ib_info_span_controller)
    public void cancel() {
        UserEventQueue.add(new ClickEvent(ibInfoSpanController.toString(), ActionType.CLICK, "arraw span"));
        if (mLatestLoanAppBean == null) {
            return;
        }
        if (ibInfoSpanController.isSelected()) {
            hideInfoSpan();
            ibInfoSpanController.setSelected(false);
        } else {
            showInfoSpan();
            ibInfoSpanController.setSelected(true);
        }
    }

    private void hideInfoSpan() {
        ValueAnimator animator = ValueAnimator.ofFloat(mCurrentAnimatedValue, 0f);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mCurrentAnimatedValue = animatedValue;
                setSpanHeight(mCurrentAnimatedValue);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimator = animation;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        animator.start();
    }

    public static final String TAG = "LoanInProcessFragment";

    private void showInfoSpan() {
        ValueAnimator animator = ValueAnimator.ofFloat(mCurrentAnimatedValue, 1f);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mCurrentAnimatedValue = animatedValue;
                Log.e(TAG, "onAnimationUpdate: " + animatedValue);
               setSpanHeight(mCurrentAnimatedValue);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimator = animation;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        animator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}

package com.daunkredit.program.sulu.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.TotalAmount;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.XLeoSp;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPresenter;
import com.daunkredit.program.sulu.view.me.helpcenter.RepaymentRaidersInstruction2;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.data.LoanRange;
import com.x.leo.slidebar.OnSlideDrag;
import com.x.leo.slidebar.SlideBarWithText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @作者:My
 * @创建日期: 2017/7/12 17:46
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanNormalFragment2 extends BaseFragment<LoanNormalFraPresenter> implements LoanNormalView {
    @BindView(R.id.id_imageview_loan_statement)
    ImageView        mIdImageviewLoanStatement;
    @BindView(R.id.id_textview_loan_statement)
    TextView         mIdTextviewLoanStatement;
    @BindView(R.id.sb_money)
    SlideBarWithText mSbMoney;
    @BindView(R.id.id_imageview_repay_statement)
    ImageView        mIdImageviewRepayStatement;
    @BindView(R.id.id_textview_repay_statement)
    TextView         mIdTextviewRepayStatement;
    @BindView(R.id.sb_term)
    SlideBarWithText mSbTerm;
    @BindView(R.id.id_textview_repayment_statement)
    TextView         mIdTextviewRepaymentStatement;
    @BindView(R.id.id_textview_unloan_total_repayment)
    TextView         mIdTextviewUnloanTotalRepayment;
    @BindView(R.id.id_button_current_loan_u)
    Button           mIdButtonCurrentLoanU;
    @BindView(R.id.id_textview_repayment_instruction)
    TextView         mRepayment_instruction;
    private double mRate = 1;

    @Override
    protected LoanNormalFraPresenter initPresenter() {
        return new LoanNormalFraPreImp();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_loan_normal_2;
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    private void showButtonTextByStatus() {
        LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
        if (latestLoanAppBean != null) {
            if (FieldParams.LoanStatus.SUBMITTED.equals(latestLoanAppBean.getStatus())) {
                mSbMoney.setValueProgress(latestLoanAppBean.getAmount());
                mSbTerm.setValueProgress(latestLoanAppBean.getPeriod());
                mIdButtonCurrentLoanU.setText(R.string.button_to_take_video);
            } else {
                mSbMoney.setPercentProgress(0);
                mSbTerm.setPercentProgress(0);
                mIdButtonCurrentLoanU.setText(R.string.button_current_loan);
            }
        }else{
            mSbMoney.setPercentProgress(0);
            mSbTerm.setPercentProgress(0);
            mIdButtonCurrentLoanU.setText(R.string.button_current_loan);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        LoanRange loanRange =
                new LoanRange(1000000.0,0.01,12000000.0,14.0,6000000,7.0,1.0,"D",0);
        loanRange.setAmountStep(1000000.0);
        loanRange.setMaxAmount(12000000.0);
        loanRange.setMinAmount(6000000.0);
        loanRange.setMinPeriod(7.0);
        loanRange.setMaxPeriod(14.0);
        loanRange.setPeriodStep(1.0);
        loanRange.setInterestRate(0.01);
        loanRange.setServiceFee(0.0);
        loanRange.setPeriodUnit("D");
        onSpanObtain(loanRange);
        mRepayment_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, RepaymentRaidersInstruction2.class));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @OnClick(R.id.id_button_current_loan_u)
    public void goCurrentLoan() {
        UserEventQueue.add(new ClickEvent(mIdButtonCurrentLoanU.toString(), ActionType.CLICK, mIdButtonCurrentLoanU.getText().toString()));
        Logger.d("LoanNormalFragment current loan clicked! token: " + TokenManager.getInstance().getToken());
        TotalAmount loanData = new TotalAmount();
        loanData.setAmount((long) mSbMoney.getCurrentValue());
        loanData.setDay((int) mSbTerm.getCurrentValue());
        loanData.setRate((int) (mRate * 100));
        TokenManager.putMessage(FieldParams.TO_LOAN_DATA, loanData);
        mPresenter.getCurrentLoan();
    }
    @Override
    protected void initData() {
        mPresenter.requestDataSpan();
    }

    @Override
    public void onSpanObtain(final LoanRange loanRange) {
        mRate = loanRange.getInterestRate();
        TokenManager.putMessage(FieldParams.CURRENT_LOAN_RATE,mRate);
        XLeoSp.getInstance(getContext()).putDouble(FieldParams.CURRENT_LOAN_RATE, mRate);
        final double diffMoney = 100 /((loanRange.getMaxAmount() - loanRange.getMinAmount())/loanRange.getAmountStep());
        final SlideBarWithText.ValueConvertor convertorMoney = new SlideBarWithText.ValueConvertor() {
            @Override
            public String convertorValue(double value) {
                return StringFormatUtils.indMoneyFormat(value);
            }

            @Override
            public double ValueFromPercent(int progress) {
                return (int)(progress / diffMoney + 0.5f) * loanRange.getAmountStep() + loanRange.getMinAmount();
            }
        };
        mSbMoney.setValueConvertor(convertorMoney);
        mSbMoney.setMinimum(loanRange.getMinAmount());
        mSbMoney.setMaximum(loanRange.getMaxAmount());
        mSbMoney.setOnDragCallBack(new OnSlideDrag() {
            @Override
            public void onDragStart(View v) {

            }

            @Override
            public void onDraging(View v, int percent) {
                double value = convertorMoney.ValueFromPercent(percent);
                mIdTextviewUnloanTotalRepayment.setText(StringFormatUtils.indMoneyFormat(loanRange.getServiceFee() + value * (1 + mSbTerm.getCurrentValue() * loanRange.getInterestRate())));
                ((TextView)v).setText(convertorMoney.convertorValue(value));
            }

            @Override
            public void onDragEnd(View v, boolean isComplete) {

            }
        });


        final double termDiff = 100 / ((loanRange.getMaxPeriod() - loanRange.getMinPeriod())/loanRange.getPeriodStep());
        final SlideBarWithText.ValueConvertor convertorTerm = new SlideBarWithText.ValueConvertor() {
            @Override
            public String convertorValue(double value) {
                String termUnit = getString(R.string.days);
                if(TextUtils.equals(loanRange.getPeriodUnit(),"D")){
                    termUnit = getString(R.string.days);
                }else if(TextUtils.equals(loanRange.getPeriodUnit(),"W")){
                    termUnit = getString(R.string.weeks);
                }else if(TextUtils.equals(loanRange.getPeriodUnit(),"M")){
                    termUnit = getString(R.string.months);
                }else if(TextUtils.equals(loanRange.getPeriodUnit(),"Y")){
                    termUnit = getString(R.string.years);
                }else{
                   // throw new IllegalArgumentException("wrong periodUnit");
                }
                return (int) value + termUnit;
            }

            @Override
            public double ValueFromPercent(int progress) {
                return (int)(progress / termDiff + 0.5f)*loanRange.getPeriodStep() + loanRange.getMinPeriod();
            }
        };
        mSbTerm.setValueConvertor(convertorTerm);
        mSbTerm.setMinimum(loanRange.getMinPeriod());
        mSbTerm.setMaximum(loanRange.getMaxPeriod());
        mSbTerm.setOnDragCallBack(new OnSlideDrag() {
            @Override
            public void onDragStart(View v) {

            }

            @Override
            public void onDraging(View v, int percent) {
                double value = convertorTerm.ValueFromPercent(percent);
                ((TextView)v).setText(convertorTerm.convertorValue(value));
                mIdTextviewUnloanTotalRepayment.setText(StringFormatUtils.indMoneyFormat(loanRange.getServiceFee() + mSbMoney.getCurrentValue() * (1 + value * loanRange.getInterestRate())));
            }

            @Override
            public void onDragEnd(View v, boolean isComplete) {

            }
        });

        showButtonTextByStatus();
        mIdTextviewUnloanTotalRepayment.setText(StringFormatUtils.indMoneyFormat(loanRange.getServiceFee() + mSbMoney.getCurrentValue() * (1 + mSbTerm.getCurrentValue() *loanRange.getInterestRate())));
    }
}

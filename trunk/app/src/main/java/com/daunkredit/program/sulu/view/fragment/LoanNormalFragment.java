package com.daunkredit.program.sulu.view.fragment;


import android.os.Bundle;
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
import com.daunkredit.program.sulu.harvester.PermissionManager;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPresenter;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.data.LoanRange;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by milo on 17-2-13.
 */

public class LoanNormalFragment extends BaseFragment<LoanNormalFraPresenter> implements LoanNormalView {


    @BindView(R.id.id_imageview_loan_statement)
    ImageView idImageviewLoanStatement;
    @BindView(R.id.id_textview_loan_statement)
    TextView  idTextviewLoanStatement;
    @BindView(R.id.id_button_loan_amount_a)
    Button    idButtonLoanAmountA;
    @BindView(R.id.id_button_loan_amount_b)
    Button    idButtonLoanAmountB;
    @BindView(R.id.id_imageview_repay_statement)
    ImageView idImageviewRepayStatement;
    @BindView(R.id.id_textview_repay_statement)
    TextView  idTextviewRepayStatement;
    @BindView(R.id.id_button_loan_days_a)
    Button    idButtonLoanDaysA;
    @BindView(R.id.id_button_loan_days_b)
    Button    idButtonLoanDaysB;
    @BindView(R.id.id_textview_repayment_statement)
    TextView  idTextviewRepaymentStatement;
    @BindView(R.id.id_button_current_loan_u)
    Button    idButtonCurrentLoanU;
    @BindView(R.id.id_textview_)
    TextView  idTextview;
    @BindView(R.id.id_textview_unloan_total_repayment)
    TextView  idTextviewUnloanTotalRepayment;


    public static TotalAmount totalAmount;


    @Override
    protected LoanNormalFraPresenter initPresenter() {
        return new LoanNormalFraPreImp();
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_unloan;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        totalAmount = new TotalAmount();
        RxBus.get().register(this);

        showButtonTextByStatus();
        InitGroupView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                new PermissionManager().sendAll(mActivity);
            }
        }).start();
    }

    private void showButtonTextByStatus() {
        LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);
        if (latestLoanAppBean != null) {
            if (FieldParams.LoanStatus.SUBMITTED.equals(latestLoanAppBean.getStatus())) {
                idButtonCurrentLoanU.setText(R.string.button_to_take_video);
            } else {
                idButtonCurrentLoanU.setText(R.string.button_current_loan);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showButtonTextByStatus();
        }
    }

    @Override
    protected void initData() {

    }

    Observable<View> clicked(final View view) {
        return Observable.create(new Observable.OnSubscribe<View>() {
            @Override
            public void call(final Subscriber<? super View> subscriber) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, ((Button) v).getText().toString()));
                        subscriber.onNext(v);

                    }
                });
            }
        });
    }


    public void InitGroupView() {
        Observable<View> amountAEventStream = clicked(idButtonLoanAmountA);
        Observable<View> amountBEventStream = clicked(idButtonLoanAmountB);

        Observable<View> loanDaysAEventStream = clicked(idButtonLoanDaysA);
        Observable<View> loanDaysBEventStream = clicked(idButtonLoanDaysB);

        amountAEventStream
                .mergeWith(amountBEventStream)
                .startWith(idButtonLoanAmountA)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<View>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e, "onError");
                    }

                    @Override
                    public void onNext(View view) {
                        idButtonLoanAmountA.setSelected(false);
                        idButtonLoanAmountB.setSelected(false);
                        Button selectedButton = (Button) view;
                        selectedButton.setSelected(true);
                        long amount = idButtonLoanAmountA.isSelected() ? 600000 : 1200000;
                        int day = idButtonLoanDaysA.isSelected() ? 7 : 14;
                        totalAmount.setAmount(amount);
                        totalAmount.setDay(day);
                        idTextviewUnloanTotalRepayment.setText(StringFormatUtils.indMoneyFormat(totalAmount.getTotalRepayment()).toString());

                    }
                });

        loanDaysAEventStream
                .mergeWith(loanDaysBEventStream)
                .startWith(idButtonLoanDaysA)
                .subscribe(new Subscriber<View>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(View view) {
                        idButtonLoanDaysA.setSelected(false);
                        idButtonLoanDaysB.setSelected(false);
                        Button selectedButton = (Button) view;
                        selectedButton.setSelected(true);
                        long amount = idButtonLoanAmountA.isSelected() ? 600000 : 1200000;
                        int day = idButtonLoanDaysA.isSelected() ? 7 : 14;
                        totalAmount.setAmount(amount);
                        totalAmount.setDay(day);
                        idTextviewUnloanTotalRepayment.setText(StringFormatUtils.indMoneyFormat(totalAmount.getTotalRepayment()).toString());

                    }
                });
    }

    /**
     * current loan button
     */

    @OnClick(R.id.id_button_current_loan_u)
    public void goCurrentLoan() {
        UserEventQueue.add(new ClickEvent(idButtonCurrentLoanU.toString(), ActionType.CLICK, idButtonCurrentLoanU.getText().toString()));
        Logger.d("LoanNormalFragment current loan clicked! token: " + TokenManager.getInstance().getToken());
        TokenManager.putMessage(FieldParams.TO_LOAN_DATA, totalAmount);
        mPresenter.getCurrentLoan();
        /**
         * currency : IDR
         * depositChannel : BLUEPAY
         * depositId : 0
         * depositMethod : ALFAMART
         * operatorId : string
         * paymentCode : string
         * price : 0
         * productId : string
         */
        //        LoanRepaymentFragment.price = "111222333";
        //        LoanRepaymentFragment.depositRB = new DepositResponseBean();
        //        LoanRepaymentFragment.paymentCode = "110011";
        //        LoanRepaymentFragment.depositRB.setDepositMethod("BCA");
        //        LoanRepaymentFragment.depositRB.setCurrency("IDR");
        //        LoanRepaymentFragment.depositRB.setDepositChannel("BLUEPAY");
        //        LoanRepaymentFragment.depositRB.setDepositId("001");
        //        LoanRepaymentFragment.depositRB.setOperatorId("111000");
        //        LoanRepaymentFragment.depositRB.setPaymentCode("110012");
        //        LoanRepaymentFragment.depositRB.setPrice(12111222);
        //        LoanRepaymentFragment.depositRB.setProductId("001");
        //        startActivity(new Intent(getContext(), BankPaymentActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void onSpanObtain(LoanRange loanRange) {

    }
}

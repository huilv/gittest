package com.daunkredit.program.sulu.view.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.bean.DepositResponseBean;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.InfoAdapter;
import com.daunkredit.program.sulu.common.InfoAdapterEnum;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanRepayExpFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanRepaymentExpFraPresenter;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanRepaymentPreImp;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.x.leo.circles.CircleProgressBarView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Miaoke on 2017/3/6.
 */

public class LoanRepaymentExpiryFragment extends BaseFragment<LoanRepaymentExpFraPresenter> implements LoanRepaymentExpFraView {


    @BindView(R.id.id_buttom_want_repay_expi)
    Button                repayButtonEx;
    @BindView(R.id.id_textview_repayment_expird_days)
    TextView              idTextviewRepaymentExpirdDays;
    @BindView(R.id.id_textview_repayment_total_amount_ex)
    TextView              idTextviewRepaymentTotalAmountEx;
    @BindView(R.id.id_textview_repayment_due_date)
    TextView              idTextviewRepaymentDueDate;
    @BindView(R.id.id_linearlayout_circle)
    LinearLayout          mIdLinearlayoutCircle;
    @BindView(R.id.cpbv_loanrepaymentexpiry)
    CircleProgressBarView mCircleProgressBarView;


    private Dialog  dialogPlus;
    private BluePay bluePay;
    static StringBuilder stateString = null;
    final  UserApi       api         = ServiceGenerator.createService(UserApi.class);
    DepositResponseBean depositRB;
    private String repayMethodorBank;

    private LoanAppBeanFather mLoanBean;
    private boolean           isBlueSdkInited;
    private String paymentCode;
    private XLeoProgressDialog mProgressDialog;


    @Override
    protected LoanRepaymentExpFraPresenter initPresenter() {
        return new LoanRepayExpFraPreImp();
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    @Override
    protected boolean initHeader(TextView view) {
        view.setText(R.string.repayment_text);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_repayment_expiry;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        mProgressDialog = new XLeoProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        bluePay = BluePay.getInstance();
        mPresenter.initView(bluePay,mProgressDialog);

        TokenManager instance = TokenManager.getInstance();

        mLoanBean = (LoanAppBeanFather) instance.getMessage(FieldParams.LATESTBEAN);
        Object message = instance.getAndRemove(FieldParams.SHOW_MY_LOAN_INFO);
        if (message != null && message instanceof Boolean && (boolean) message) {
            LoanAppBeanFather loanBean = (LoanAppBeanFather) instance.getAndRemove(FieldParams.LOAN_APP_INFO_BEAN);
            if (loanBean != null) {
                mLoanBean = loanBean;
            }
        }
        if (mLoanBean == null) {
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhoneState = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            //                        int sendSms = getActivity().checkSelfPermission(Manifest.permission.SEND_SMS);
            //                        && sendSms == PackageManager.PERMISSION_GRANTED
            if (readPhoneState == PackageManager.PERMISSION_GRANTED && !isBlueSdkInited) {
                mPresenter.initBlueSDK(bluePay,mProgressDialog);
                isBlueSdkInited = true;
            }
        }
    }

    @Override
    protected void initData() {

        LoanRepaymentFragment.price = StringFormatUtils.indMoneyFormat(mLoanBean.getTotalAmount()).toString();

        SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormatLocal.setTimeZone(TimeZone.getDefault());

        int remainingDays = Math.abs(mLoanBean.getRemainingDays());

        idTextviewRepaymentExpirdDays.setText(String.valueOf(remainingDays));

        Logger.d("ParseException: dueTime: %s ",
                TimeFormat.convertTime(mLoanBean.getDueDate()));


        idTextviewRepaymentTotalAmountEx.setText(StringFormatUtils.indMoneyFormat(mLoanBean.getTotalAmount()).toString());
        idTextviewRepaymentDueDate.setText(TimeFormat.convertTime(mLoanBean.getDueDate(), "dd-MM-yyyy"));
        mCircleProgressBarView.setProgress(100);

    }

//    /**
//     * Init the Blue SDK
//     */
//    public void initBlueSDK() {
//        mActivity.showLoading(null);
//        Client.init(getActivity(), new BlueInitCallback() {
//            @Override
//            public void initComplete(String loginResult,
//                                     String resultDesc) {
//                mActivity.dismissLoading();
//                String result = null;
//                try {
//                    Logger.d("loginResult", loginResult);
//                    Logger.d("BluePay", resultDesc);
//                    System.out.println("loginResult: " + loginResult + "---resultDesc: " + resultDesc);
//                    if (loginResult.equals(LoginResult.LOGIN_SUCCESS)) {
//
//                        BluePay.setLandscape(false);//设置BluePay的相关UI是否使用横屏UI
//                        BluePay.setShowCardLoading(true);// 该方法设置使用cashcard时是否使用sdk的loading框
//                        BluePay.setShowResult(true);// 设置是否使用支付结果展示窗
//
//                        result = "Init Success!";
//
//                        Logger.d("LoanRepaymentExpiryFragment", "result: " + result);
//                        System.out.println("result: " + result);
//
//                    } else if (loginResult
//                            .equals(LoginResult.LOGIN_FAIL)) {
//
//                        result = "User Login Failed!";
//
//                        Logger.d("LoanRepaymentExpiryFragment", "result: " + result);
//                        System.out.println("result: " + result);
//                    } else {
//                        StringBuilder sbStr = new StringBuilder(
//                                "Fail! The code is:")
//                                .append(loginResult)
//                                .append(" desc is:").append(resultDesc);
//                        stateString.append(sbStr.toString());
//                        result = sbStr.toString();
//
//                        System.out.println("result: " + result);
//                        Logger.d("LoanRepaymentExpiryFragment", "result: " + result);
//                    }
//                } catch (Exception e) {
//                    result = e.getMessage();
//                    Logger.d("LoanRepaymentExpiryFragment", "result: " + result);
//
//                    System.out.println("result: " + result);
//                }
//                Logger.d(result, LoanRepaymentExpiryFragment.this);
//                //                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
//            }
//
//        });
//
//    }

//    public void initRepaymentMethodAdapter(DepositMethodsBean depositMethodsBean) {
//        //BankAdapter bankAdapter = new BankAdapter(getActivity());
//        InfoAdapterEnum repaymentMethodAdapter = new InfoAdapterEnum(getContext());
//        for (String method : depositMethodsBean.getDepositMethods()) {
//            repaymentMethodAdapter.addItem(PayMethodEnum.valueOf(method), InfoType.REPAYMETHOD);
//        }
//        Logger.d("Get method success. ");
//        dialogPlus = DialogManager.newListViewDialog(getActivity())
//                .setHeader(R.layout.pop_dialog_header)
//                // .setFooter(R.layout.pop_dialog_button)
//                .setAdapter(repaymentMethodAdapter)
//                .setGravity(Gravity.CENTER)
//                .setExpanded(false)
//                .create();
//        dialogPlus.show();
//    }

//    public void onDepositResponseBeanObtain(DepositResponseBean depositResponseBean) {
//        depositRB = depositResponseBean;
//    }


    /**
     * show repayment methods
     */
    private void showDialog() {
//        mPresenter.getRepayMethodAdapter();
        mPresenter.getRepaymentMethodAdapter(new LoanRepaymentPreImp.CallBack() {
            @Override
            public void onMethodsObtain(InfoAdapterEnum repaymentMethodAdapter) {
                dialogPlus = DialogManager.newListViewDialog(getActivity())
                        .setHeader(R.layout.pop_dialog_header)
                        // .setFooter(R.layout.pop_dialog_button)
                        .setAdapter(repaymentMethodAdapter)
                        .setGravity(Gravity.CENTER)
                        .setExpanded(false)
                        .create();
                dialogPlus.show();
            }
        });
    }



    @OnClick(R.id.id_buttom_want_repay_expi)
    public void setRepaymentMethod() {
        UserEventQueue.add(new ClickEvent(repayButtonEx.toString(), ActionType.CLICK, repayButtonEx.getText().toString()));
        Logger.d("Loan Repayment expiry Fragment Choose Bank");
        showDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void doOnBlueMessage(BlueMessage blueMessage) {
        String message = "result code:" + blueMessage.getCode() + " message:"
                + blueMessage.getDesc() + " code :" + blueMessage.getCode() + "   price:"
                + blueMessage.getPrice() + " Payment channel:" + blueMessage.getPublisher();

        if (!TextUtils.isEmpty(blueMessage.getOfflinePaymentCode())) {
            // offline payment code 不为空，说明这个是印尼的offline，可以展示paymentCode给用户
            message += ", " + blueMessage.getOfflinePaymentCode()
                    + ". please go to " + blueMessage.getPublisher()
                    + " to finish this payment";
        }
        Logger.d(message);

        String title = "";
        if (blueMessage.getCode() == 200) {
            title = "Success";
        } else if (blueMessage.getCode() == 201) {
            paymentCode = blueMessage.getOfflinePaymentCode();
            //代表请求成功,计费还未完成
            title = "Request success,in progressing...";
        } else if (blueMessage.getCode() == 603) {
            // 用户取消
            title = "User cancel";
        } else {
            //请求失败
            title = "Fail";
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        // do not need to show.
        //        mActivity.runOnUiThread(new Runnable() {
        //            @Override
        //            public void run() {
        //                dialog.show();
        //            }
        //        });

    }

    @Override
    public void setStateString(String string) {
        stateString.append(string);
    }

    @Override
    public void onMethodChoose(InfoAdapter.InfoItem data) {
        Logger.d("sendRepayRequest=====");
        if (data.getType() == InfoType.REPAYMETHOD) {
            if (dialogPlus != null && dialogPlus.isShowing()) {
                dialogPlus.dismiss();
            }
            repayMethodorBank = data.getValueStr();
            mPresenter.doRepayRequest(mLoanBean,repayMethodorBank,bluePay);
        }
    }
}

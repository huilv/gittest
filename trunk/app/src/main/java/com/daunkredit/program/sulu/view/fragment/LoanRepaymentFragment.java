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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanRepaymentFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoanRepaymentFraPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.x.leo.circles.CircleProgressBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Miaoke on 2017/2/21.
 */

public class LoanRepaymentFragment extends BaseFragment<LoanRepaymentFraPresenter> implements LoanRepaymentFraView {

    private static final int REQUEST_CODE_ASK_SEND_SMS = 100027;
    @BindView(R.id.id_textview_days)
    TextView              idTextviewDays;
    @BindView(R.id.id_linearlayout_circle)
    LinearLayout          idLinearlayoutCircle;
    @BindView(R.id.id_textview_repayment_amount_re)
    TextView              idTextviewRepaymentAmountRe;
    @BindView(R.id.id_textview_repayment_due_date)
    TextView              idTextviewRepaymentDueDate;
    @BindView(R.id.id_textview_been_payment_text)
    TextView              idTextviewBeenPaymentText;
    @BindView(R.id.id_textview_been_payment)
    TextView              idTextviewBeenPayment;
    @BindView(R.id.id_textview_total_text)
    TextView              idTextviewRemainText;
    @BindView(R.id.id_buttom_want_repay)
    Button                idButtomWantRepay;
    @BindView(R.id.ibt_arc_down)
    ImageButton           mIbtArcDown;
    @BindView(R.id.pb_repayment_slide)
    ProgressBar           mPbRepaymentSlide;
    @BindView(R.id.ll_repayment_progress_wide)
    LinearLayout          mLlRepaymentProgressWide;
    @BindView(R.id.pb_repayment_wide)
    ProgressBar           mPbRepaymentWide;
    @BindView(R.id.cpbv_loanrepayment)
    CircleProgressBarView mCircleProgressBarView;


    private Dialog  dialogPlus;
    private String  repayMethod;
    private BluePay bluePay;
    private Dialog  mProgressDialog;
    static StringBuilder stateString = null;
    public static DepositResponseBean depositRB;

    public static String paymentCode;
    public static String price;

    protected static final int REQUEST_CODE_ASK_CALL_PHONE = 100026;
    private LoanAppBeanFather mLoanAppBean;
    private String            repayMethodorBank;
    private boolean           isBlueSdkInited;

    @Override
    protected LoanRepaymentFraPresenter initPresenter() {
        return new LoanRepaymentFraPreImp();
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
        return R.layout.fragment_main_repayment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        bluePay = BluePay.getInstance();
        mProgressDialog = new XLeoProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhoneState = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int sendSms = getActivity().checkSelfPermission(Manifest.permission.SEND_SMS);
            if (readPhoneState != PackageManager.PERMISSION_GRANTED || sendSms != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissions = new ArrayList<>();
                if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_PHONE_STATE);
                }

                if (sendSms != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.SEND_SMS);
                }
                getActivity().requestPermissions(permissions.toArray(new String[permissions.size()]),
                        REQUEST_CODE_ASK_SEND_SMS);
            } else {
                mPresenter.initBlueSDK(bluePay,mProgressDialog);
            }
        } else {
            mPresenter.initBlueSDK(bluePay, mProgressDialog);
        }


        if (!getDatas()) {
            return;
        }
        initStartView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhoneState = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//                        int sendSms = getActivity().checkSelfPermission(Manifest.permission.SEND_SMS);
            //            && sendSms == PackageManager.PERMISSION_GRANTED
            if (readPhoneState == PackageManager.PERMISSION_GRANTED && !isBlueSdkInited) {
                mPresenter.initBlueSDK(bluePay, mProgressDialog);
                isBlueSdkInited = true;
            }
        }
    }

    private boolean getDatas() {
        TokenManager instance = TokenManager.getInstance();

        mLoanAppBean = (LoanAppBeanFather) instance.getMessage(FieldParams.LATESTBEAN);
        Object message = instance.getAndRemove(FieldParams.SHOW_MY_LOAN_INFO);
        if (message != null && message instanceof Boolean && (boolean) message) {
            LoanAppBeanFather loanBean = (LoanAppBeanFather) instance.getAndRemove(FieldParams.LOAN_APP_INFO_BEAN);
            if (loanBean != null) {
                mLoanAppBean = loanBean;
            }
        }
        return mLoanAppBean != null;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getDatas()) {
                initStartView();
                initData();
            }
        }
    }

    private void initStartView() {

        mIbtArcDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK));
                if (mIbtArcDown.isSelected()) {
                    mIbtArcDown.setSelected(false);
                    mLlRepaymentProgressWide.setVisibility(View.VISIBLE);
                    mPbRepaymentSlide.setVisibility(View.GONE);
                } else {
                    mIbtArcDown.setSelected(true);
                    mLlRepaymentProgressWide.setVisibility(View.GONE);
                    mPbRepaymentSlide.setVisibility(View.VISIBLE);
                }
            }
        });
        double paidAmount = mLoanAppBean.getPaidAmount();
        double totalAmount = mLoanAppBean.getTotalAmount();
        if (totalAmount != 0) {
            int tempProgress = (int) (paidAmount / totalAmount);
            mPbRepaymentSlide.setProgress(tempProgress);
            mPbRepaymentWide.setProgress(tempProgress);
        }
    }

    @Override
    protected void initData() {
        if (mLoanAppBean == null) {
            return;
        }
        price = StringFormatUtils.indMoneyFormat(mLoanAppBean.getTotalAmount());
        int remainingDays = Math.abs(mLoanAppBean.getRemainingDays());
        if (mLoanAppBean.getRemainingDays() < 0) {
            remainingDays = 0;
        }
        if (mLoanAppBean.getPeriod() != 0) {
            mCircleProgressBarView.setProgress((mLoanAppBean.getPeriod() - remainingDays) * 100 / mLoanAppBean.getPeriod());
        }
        idTextviewDays.setText(String.valueOf(remainingDays));

        Logger.d("ParseException: dueTime: %s ",
                TimeFormat.convertTime(mLoanAppBean.getCreateTime()),
                TimeFormat.convertTime(mLoanAppBean.getDueDate()));

        idTextviewRepaymentAmountRe.setText(StringFormatUtils.indMoneyFormat(mLoanAppBean.getTotalAmount()));
        idTextviewRepaymentDueDate.setText(TimeFormat.convertTime(mLoanAppBean.getDueDate(), "dd-MM-yyyy"));
        idTextviewBeenPayment.setText(StringFormatUtils.indMoneyFormat(mLoanAppBean.getPaidAmount()));
        idTextviewRemainText.setText(StringFormatUtils.indMoneyFormat(mLoanAppBean.getRemainAmount()));

        Logger.d("Been payment: %s  RemindAmount: %s TotalAmount: %s DueDate: %s", StringFormatUtils.moneyFormat(mLoanAppBean.getPaidAmount()),
                StringFormatUtils.moneyFormat(mLoanAppBean.getRemainAmount()),
                StringFormatUtils.moneyFormat(mLoanAppBean.getTotalAmount()),
                TimeFormat.convertTime(mLoanAppBean.getDueDate()));
    }






    /**
     * show repayment methods
     */
    private void showDialog() {
        //BankAdapter bankAdapter = new BankAdapter(getActivity());
        mActivity.showLoading(null);
        mPresenter.getRepaymentMethodAdapter(new LoanRepaymentFraPreImp.CallBack(){

            @Override
            public void onMethodsObtain(InfoAdapterEnum repaymentMethodAdapter) {
                mActivity.dismissLoading();
                dialogPlus = new DialogManager.DialogBuilder(getActivity())
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

    //支付返回时，UI的变化
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
        sendRepayRequest(data);
    }


    public void sendRepayRequest(InfoAdapter.InfoItem event) {
        if (event.getType() == InfoType.REPAYMETHOD) {
            if (dialogPlus != null && dialogPlus.isShowing()) {
                dialogPlus.dismiss();
            }
            repayMethodorBank = event.getValueStr();
            Logger.d("Repayment Method or Bank: " + repayMethodorBank);
            mPresenter.doRepayRequest(mLoanAppBean,repayMethodorBank,bluePay);

            //            bluePay.payByOffline(getActivity(),
            //                    mLoanAppBean.getLoanAppId(), //transactionId，该笔交易的唯一标识，不能 超过 32 位
            //                    "customerId", //CustomerId，可以是用户 id、游戏 id，也 可以是空字符串，但是长度不能高于 50 位
            //                    "IDR", //Currency,只能传 Config.K_CURRENCY_ID
            //                    50000 + "", //price 目前只支持整数的字符串
            //                    "loan",  //PropsName,商品名称，比如金币、钻石、体 力等;长度限制为 50，并且不能为 null
            //                    repayMethod, //publisher 支付方式，目前只有两个可选:ATM/OTC PublisherCode.PUBLISHER_OFFLINE_OTC
            //                    "130000000010",  //msisdn 手机号码，可以为空，也可以不为 空，长度不能超过 15 位
            //                    true,  //是否使用 sdk 的 loading 对话框。true 为使 用，false 为不使用
            //                    callback);  //计费回调


            //            bluePay.payByOffline(getActivity(),
            //                    "123456799999", //transactionId，该笔交易的唯一标识，不能 超过 32 位
            //                    "customerId", //CustomerId，可以是用户 id、游戏 id，也 可以是空字符串，但是长度不能高于 50 位
            //                    "IDR", //Currency,只能传 Config.K_CURRENCY_ID
            //                    50000 + "", //price 目前只支持整数的字符串
            //                    "loan",  //PropsName,商品名称，比如金币、钻石、体 力等;长度限制为 50，并且不能为 null
            //                    "otc", //publisher 支付方式，目前只有两个可选:ATM/OTC PublisherCode.PUBLISHER_OFFLINE_OTC
            //                    "130000000010",  //msisdn 手机号码，可以为空，也可以不为 空，长度不能超过 15 位
            //                    true,  //是否使用 sdk 的 loading 对话框。true 为使 用，false 为不使用
            //                    callback);  //计费回调

        }
    }

    //    IPayCallback callback = new IPayCallback() {
    //        @Override
    //        public void onFinished(BlueMessage msg) {
    //            {
    //                String message = "result code:" + msg.getCode() + " message:"
    //                        + msg.getDesc() + " code :" + msg.getCode() + "   price:"
    //                        + msg.getPrice() + " Payment channel:" + msg.getPublisher();
    //
    //                if (!TextUtils.isEmpty(msg.getOfflinePaymentCode())) {
    //                    // offline payment code 不为空，说明这个是印尼的offline，可以展示paymentCode给用户
    //                    message += ", " + msg.getOfflinePaymentCode()
    //                            + ". please go to " + msg.getPublisher()
    //                            + " to finish this payment";
    //                }
    //                Logger.d(message);
    //
    //                String title = "";
    //                if (msg.getCode() == 200) {
    //                    title = "Success";
    //                } else if (msg.getCode() == 201) {
    //                    paymentCode = msg.getOfflinePaymentCode();
    //                    //代表请求成功,计费还未完成
    //                    title = "Request success,in progressing...";
    //                } else if (msg.getCode() == 603) {
    //                    // 用户取消
    //                    title = "User cancel";
    //                } else {
    //                    //请求失败
    //                    title = "Fail";
    //                }
    //
    //                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
    //                dialog.setTitle(title);
    //                dialog.setMessage(message);
    //                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
    //                        new DialogInterface.OnClickListener() {
    //
    //                            @Override
    //                            public void onClick(DialogInterface dialog, int which) {
    //
    //                            }
    //                        });
    //
    //                dialog.show();
    //            }
    //
    //        }
    //
    //        @Override
    //        public String onPrepared() {
    //            return null;
    //        }
    //    };


    @OnClick(R.id.id_buttom_want_repay)
    public void setRepayMethod() {
        UserEventQueue.add(new ClickEvent(idButtomWantRepay.toString(), ActionType.CLICK, idButtomWantRepay.getText().toString()));
        Logger.d("Select repayment method.");
        showDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}

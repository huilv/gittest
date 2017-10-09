package com.daunkredit.program.sulu.view.fragment.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.bluepay.pay.BluePay;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentExpFraView;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;

import java.util.ArrayList;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:15
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanRepayExpFraPreImp extends LoanRepaymentPreImp implements LoanRepaymentExpFraPresenter {
    private static final   int REQUEST_CODE_ASK_SEND_SMS   = 100027;
    protected static final int REQUEST_CODE_ASK_CALL_PHONE = 100026;

    @Override
    public void initView(BluePay bluePay, XLeoProgressDialog progressDialog) {
        LoanRepaymentExpFraView view = (LoanRepaymentExpFraView) mView;
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhoneState = mView.getBaseActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int sendSms = mView.getBaseActivity().checkSelfPermission(Manifest.permission.SEND_SMS);
            if (readPhoneState != PackageManager.PERMISSION_GRANTED || sendSms != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissions = new ArrayList<>();
                if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_PHONE_STATE);
                }

                if (sendSms != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.SEND_SMS);
                }
                mView.getBaseActivity().requestPermissions(permissions.toArray(new String[permissions.size()]),
                        REQUEST_CODE_ASK_SEND_SMS);
            } else {
                initBlueSDK(bluePay,progressDialog);
            }
        } else {
            initBlueSDK(bluePay,progressDialog);
        }
    }


    //    /**
//     * Get the Repayment method
//     *
//     * @return
//     */
//    public void getRepayMethodAdapter() {
//        final LoanRepaymentExpFraView view = (LoanRepaymentExpFraView) mView;
//        final InfoAdapterEnum repaymentMethodAdapter = new InfoAdapterEnum(mView.getBaseActivity());
//        mUserApi.getDepostMethods(TokenManager.getInstance().getToken())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DepositMethodsBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        XLeoToast.showMessage(R.string.show_payment_methods_failed);
//                        Logger.d("Get method failed: " + e.getMessage());
//
//                    }
//
//                    @Override
//                    public void onNext(DepositMethodsBean depositMethodsBean) {
//                        view.initRepaymentMethodAdapter(depositMethodsBean);
//                    }
//                });
//    }

//    public void doRepayRequest(LoanAppBeanFather loanBean, final String repayMethodorBank, final BluePay bluePay) {
//        final LoanRepaymentExpFraView view = (LoanRepaymentExpFraView) mView;
//        mUserApi.doDeposit(loanBean.getLoanAppId()
//                , "IDR"
//                , repayMethodorBank
//                , TokenManager.getInstance().getToken())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        mView.getBaseActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mView.getBaseActivity().showLoading("loading...");
//                            }
//                        });
//
//                    }
//                })
//                .filter(new Func1<DepositResponseBean, Boolean>() {
//                    @Override
//                    public Boolean call(DepositResponseBean depositResponseBean) {
//
//                        Logger.d("Method: %s, Channel: %s ", depositResponseBean.getDepositMethod(), depositResponseBean.getDepositChannel());
//
//                        view.onDepositResponseBeanObtain(depositResponseBean);
//                        if ((repayMethodorBank.equals("BCA") || repayMethodorBank.equals("MANDIRI") || repayMethodorBank.equals("BNI")) && depositResponseBean.getDepositChannel().equals("XENDIT")) {
//
//                            mView.getBaseActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mView.getBaseActivity().dismissLoading();
//                                    Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
//                                    mView.getBaseActivity().startActivity(intent);
//                                }
//                            });
//                            Logger.d(Thread.currentThread().getName());
//
//                        } else if ("BLUEPAY".equals(depositResponseBean.getDepositChannel()) && !StringUtil.isNullOrEmpty(depositResponseBean.getPaymentCode())) {
//                            LoanRepaymentFragment.paymentCode = depositResponseBean.getPaymentCode();
//                            mView.getBaseActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mView.getBaseActivity().dismissLoading();
//                                    Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
//                                    mView.getBaseActivity().startActivity(intent);
//                                }
//                            });
//                            Logger.d(Thread.currentThread().getName());
//                        }
//
//                        //走bluepay
//                        //                      return (repayMethodorBank.equals("ALFAMART") || repayMethodorBank.equals("OTHERS")) && depositResponseBean.getDepositChannel().equals("BLUEPAY");
//                        return "BLUEPAY".equals(depositResponseBean.getDepositChannel()) && StringUtil.isNullOrEmpty(depositResponseBean.getPaymentCode());
//
//
//                    }
//                }).flatMap(new Func1<DepositResponseBean, Observable<BlueMessage>>() {    //支付请求
//            @Override
//            public Observable<BlueMessage> call(DepositResponseBean depositResponseBean) {
//                return pay(depositResponseBean,bluePay);
//            }
//        })
//                .doOnNext(new Action1<BlueMessage>() {  //支付结果的附加处理，可以用来表明多个步骤的操作进行到了哪一步
//                    @Override
//                    public void call(BlueMessage blueMessage) {
//                        doOnBlueMessage(blueMessage);
//                    }
//                })
//                .filter(new Func1<BlueMessage, Boolean>() { //支付结果过滤，符合条件的才会发往下一步处理
//                    @Override
//                    public Boolean call(BlueMessage blueMessage) {
//                        return blueMessage.getCode() == 201;
//                    }
//                }).observeOn(Schedulers.io())
//                .flatMap(new Func1<BlueMessage, Observable<ResponseBody>>() {   //上传支付结果
//                    @Override
//                    public Observable<ResponseBody> call(BlueMessage blueMessage) {
//                        Logger.d(Thread.currentThread().getName());
//                        return mUserApi.sendPayCode(depositRB.getDepositId(),
//                                blueMessage.getOfflinePaymentCode(),
//                                TokenManager.getInstance().getToken());
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseBody>() {
//                    @Override
//                    public void onCompleted() {
//                        Logger.d("OnComplete");
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Logger.d("get repayment info failed" + e.getMessage());
//                        XLeoToast.showMessage(R.string.generate_payment_code_failed);
//                        mView.getBaseActivity().dismissLoading();
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) { //上传支付结果操作的结果
//                        // 展示还款信息页面
//                        mView.getBaseActivity().dismissLoading();
//                        Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
//                        mView.getBaseActivity().startActivity(intent);
//
//                    }
//                });
//
//
//    }
//
//    Observable<BlueMessage> pay(final DepositResponseBean depositResponseBean, final BluePay bluePay) {
//        depositRB = depositResponseBean;
//        return Observable.create(new Observable.OnSubscribe<BlueMessage>() {
//            @Override
//            public void call(final Subscriber<? super BlueMessage> subscriber) {
//
//                String mMethod;
//
//                if (depositResponseBean.getDepositMethod().equals("ALFAMART")) {
//                    mMethod = "otc";
//                } else {
//                    mMethod = "atm";
//                }
//
//                bluePay.payByOffline(mView.getBaseActivity(),
//                        depositResponseBean.getDepositId(), //transactionId，该笔交易的唯一标识，不能 超过 32 位
//                        "1", //CustomerId，可以是用户 id、游戏 id，也 可以是空字符串，但是长度不能高于 50 位
//                        depositResponseBean.getCurrency(), //Currency,只能传 Config.K_CURRENCY_ID
//                        depositResponseBean.getPrice() + "", //price 目前只支持整数的字符串
//                        "loan",  //PropsName,商品名称，比如金币、钻石、体 力等;长度限制为 50，并且不能为 null
//                        mMethod, //publisher 支付方式，目前只有两个可选:ATM/OTC PublisherCode.PUBLISHER_OFFLINE_OTC
//                        "1",  //msisdn 手机号码，可以为空，也可以不为 空，长度不能超过 15 位
//                        // do not need to show
//                        false,  //是否使用 sdk 的 loading 对话框。true 为使 用，false 为不使用
//                        new IPayCallback() {
//                            @Override
//                            public void onFinished(BlueMessage blueMessage) {
//                                subscriber.onNext(blueMessage);
//                            }
//
//                            @Override
//                            public String onPrepared() {
//                                return null;
//                            }
//                        });
//            }
//        });
//    }
//
//    //支付返回时，UI的变化
//    private void doOnBlueMessage(BlueMessage blueMessage) {
//        String message = "result code:" + blueMessage.getCode() + " message:"
//                + blueMessage.getDesc() + " code :" + blueMessage.getCode() + "   price:"
//                + blueMessage.getPrice() + " Payment channel:" + blueMessage.getPublisher();
//
//        if (!TextUtils.isEmpty(blueMessage.getOfflinePaymentCode())) {
//            // offline payment code 不为空，说明这个是印尼的offline，可以展示paymentCode给用户
//            message += ", " + blueMessage.getOfflinePaymentCode()
//                    + ". please go to " + blueMessage.getPublisher()
//                    + " to finish this payment";
//        }
//        Logger.d(message);
//
//        String title = "";
//        if (blueMessage.getCode() == 200) {
//            title = "Success";
//        } else if (blueMessage.getCode() == 201) {
//            LoanRepaymentFragment.paymentCode = blueMessage.getOfflinePaymentCode();
//            //代表请求成功,计费还未完成
//            title = "Request success,in progressing...";
//        } else if (blueMessage.getCode() == 603) {
//            // 用户取消
//            title = "User cancel";
//        } else {
//            //请求失败
//            title = "Fail";
//        }
//
//        final AlertDialog dialog = new AlertDialog.Builder(mView.getBaseActivity()).create();
//        dialog.setTitle(title);
//        dialog.setMessage(message);
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//        //        mActivity.runOnUiThread(new Runnable() {
//        //            @Override
//        //            public void run() {
//        //                dialog.show();
//        //            }
//        //        });
//
//
//    }

}

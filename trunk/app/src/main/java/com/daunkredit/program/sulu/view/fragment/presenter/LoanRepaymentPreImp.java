package com.daunkredit.program.sulu.view.fragment.presenter;

import android.app.Dialog;
import android.content.Intent;

import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.bluepay.pay.Client;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.LoginResult;
import com.common.StringUtil;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl;
import com.daunkredit.program.sulu.bean.DepositMethodsBean;
import com.daunkredit.program.sulu.bean.DepositResponseBean;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.InfoAdapter;
import com.daunkredit.program.sulu.common.InfoAdapterEnum;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.view.BankPaymentActivity;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.certification.status.PayMethodEnum;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentExpFraView;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentFragment;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.fragment.OnInfoAdapterItemClickListener;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.daunkredit.program.sulu.view.fragment.LoanRepaymentFragment.depositRB;

/**
 * @作者:My
 * @创建日期: 2017/6/23 17:49
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanRepaymentPreImp extends BaseFragmentPresenterImpl implements LoanRepaymentPresenter {
    @Override
    public void getRepaymentMethodAdapter(final CallBack callBack) {

        mUserApi.getDepostMethods(TokenManager.getInstance().getToken().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DepositMethodsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLeoToast.showMessage(R.string.show_payment_methods_failed);
                        ;
                        Logger.d("Get method failed: " + e.getMessage());

                    }

                    @Override
                    public void onNext(DepositMethodsBean depositMethodsBean) {
                        InfoAdapterEnum repaymentMethodAdapter = new InfoAdapterEnum(mView.getBaseActivity());
                        for (String method : depositMethodsBean.getDepositMethods()) {
                            repaymentMethodAdapter.addItem(PayMethodEnum.valueOf(method), InfoType.REPAYMETHOD);
                        }
                        repaymentMethodAdapter.setOnItemClickListener(new OnInfoAdapterItemClickListener() {
                            @Override
                            public void onItemClick(InfoAdapter.InfoItem data) {
                                if (mView instanceof LoanRepaymentView) {
                                    ((LoanRepaymentView)mView).onMethodChoose(data);
                                }else if(mView instanceof LoanRepaymentExpFraView){
                                    ((LoanRepaymentExpFraView)mView).onMethodChoose(data);
                                }
                            }
                        });
                        callBack.onMethodsObtain(repaymentMethodAdapter);
                        Logger.d("Get method success. ");
                    }
                });

    }

    @Override
    public void doRepayRequest(LoanAppBeanFather mLoanAppBean, final String repayMethodorBank, final BluePay bluePay) {
        final LoanRepaymentView view = (LoanRepaymentView) mView;
        mUserApi.doDeposit(mLoanAppBean.getLoanAppId()
                , "IDR"
                , repayMethodorBank
                , TokenManager.getInstance().getToken())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //                        showDialog();
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.getBaseActivity().showLoading("loading...");
                            }
                        });

                        Logger.d(Thread.currentThread().getName());

                    }
                })
                .filter(new Func1<DepositResponseBean, Boolean>() {
                    @Override
                    public Boolean call(final DepositResponseBean depositResponseBean) {
                        Logger.d("Method: %s, Channel: %s ", depositResponseBean.getDepositMethod(), depositResponseBean.getDepositChannel());
                        depositRB = depositResponseBean;

                        if ((repayMethodorBank.equals("BCA") || repayMethodorBank.equals("MANDIRI") || repayMethodorBank.equals("BNI")) && depositResponseBean.getDepositChannel().equals("XENDIT")) {
                            if (isAttached()) {
                                mView.getBaseActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.getBaseActivity().dismissLoading();
                                        Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
                                        mView.getBaseActivity().startActivity(intent);
                                    }
                                });
                            }

                            Logger.d(Thread.currentThread().getName());

                        } else if ("BLUEPAY".equals(depositResponseBean.getDepositChannel()) && !StringUtil.isNullOrEmpty(depositResponseBean.getPaymentCode())) {

                            LoanRepaymentFragment.paymentCode = depositResponseBean.getPaymentCode();
                            if (isAttached()) {
                                mView.getBaseActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.getBaseActivity().dismissLoading();
                                        Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
                                        mView.getBaseActivity().startActivity(intent);
                                    }
                                });
                            }
                            Logger.d(Thread.currentThread().getName());

                        }
                        //走bluepay
                        //                      return (repayMethodorBank.equals("ALFAMART") || repayMethodorBank.equals("OTHERS")) && depositResponseBean.getDepositChannel().equals("BLUEPAY");
                        return "BLUEPAY".equals(depositResponseBean.getDepositChannel()) && StringUtil.isNullOrEmpty(depositResponseBean.getPaymentCode());
                    }
                })
                .flatMap(new Func1<DepositResponseBean, Observable<BlueMessage>>() {    //支付请求
                    @Override
                    public Observable<BlueMessage> call(DepositResponseBean depositResponseBean) {
                        Logger.d(Thread.currentThread().getName());
                        return pay(depositResponseBean, bluePay);
                    }

                })
                .doOnNext(new Action1<BlueMessage>() {  //支付结果的附加处理，可以用来表明多个步骤的操作进行到了哪一步
                    @Override
                    public void call(final BlueMessage blueMessage) {
                        Logger.d(Thread.currentThread().getName());
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                view.doOnBlueMessage(blueMessage);
                            }
                        });
                    }
                })
                .filter(new Func1<BlueMessage, Boolean>() { //支付结果过滤，符合条件的才会发往下一步处理
                    @Override
                    public Boolean call(BlueMessage blueMessage) {
                        Logger.d(Thread.currentThread().getName());
                        return blueMessage.getCode() == 201;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<BlueMessage, Observable<ResponseBody>>() {   //上传支付结果
                    @Override
                    public Observable<ResponseBody> call(BlueMessage blueMessage) {
                        Logger.d(Thread.currentThread().getName());
                        return mUserApi.sendPayCode(depositRB.getDepositId(),
                                blueMessage.getOfflinePaymentCode(),
                                TokenManager.getInstance().getToken());
                    }
                }) // Need to think
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("OnComplete");

                    }


                    @Override
                    public void onError(Throwable e) {
                        Logger.d("get repayment info failed" + e.getMessage());
                        XLeoToast.showMessage(R.string.generate_payment_code_failed);
                        if (isAttached()) {
                            mView.getBaseActivity().dismissLoading();
                        }
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) { //上传支付结果操作的结果
                        // 展示还款信息页面
                        mView.getBaseActivity().dismissLoading();
                        Intent intent = new Intent(mView.getBaseActivity(), BankPaymentActivity.class);
                        mView.getBaseActivity().startActivity(intent);

                    }
                });

    }

    @Override
    public void initBlueSDK(BluePay bluePay, final Dialog mProgressDialog) {
        final LoanRepaymentView view = (LoanRepaymentView) mView;
        mProgressDialog.show();
        Client.init(mView.getBaseActivity(), new BlueInitCallback() {

            @Override
            public void initComplete(String loginResult,
                                     String resultDesc) {
                mProgressDialog.dismiss();
                String result = null;
                try {
                    Logger.d("loginResult", loginResult);
                    Logger.d("BluePay", resultDesc);
                    System.out.println("loginResult: " + loginResult + "---resultDesc: " + resultDesc);
                    if (loginResult.equals(LoginResult.LOGIN_SUCCESS)) {

                        BluePay.setLandscape(false);//设置BluePay的相关UI是否使用横屏UI
                        BluePay.setShowCardLoading(true);// 该方法设置使用cashcard时是否使用sdk的loading框
                        BluePay.setShowResult(true);// 设置是否使用支付结果展示窗

                        result = "init success!";

                        Logger.d("LoanRepaymentFragment", "result: " + result);
                        System.out.println("result: " + result);

                    } else if (loginResult
                            .equals(LoginResult.LOGIN_FAIL)) {

                        result = "User Login Failed!";

                        Logger.d("LoanRepaymentFragment", "result: " + result);
                        System.out.println("result: " + result);
                    } else {
                        StringBuilder sbStr = new StringBuilder(
                                "Fail! The code is:")
                                .append(loginResult)
                                .append(" desc is:").append(resultDesc);
                        view.setStateString(sbStr.toString());
                        result = sbStr.toString();

                        System.out.println("result: " + result);
                        Logger.d("LoanRepaymentFragment", "result: " + result);
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                    Logger.d("LoanRepaymentFragment", "result: " + result);

                    System.out.println("result: " + result);
                }
                Logger.d(result, this);
                //                Toast.makeText(mView.getBaseActivity(), result, Toast.LENGTH_SHORT).show();
            }

        });

    }

    Observable<BlueMessage> pay(final DepositResponseBean depositResponseBean, final BluePay bluePay) {
        return Observable.create(new Observable.OnSubscribe<BlueMessage>() {
            @Override
            public void call(final Subscriber<? super BlueMessage> subscriber) {
                String mMethod;

                if (depositResponseBean.getDepositMethod().equals("ALFAMART")) {
                    mMethod = "otc";
                } else {
                    mMethod = "atm";
                }

                bluePay.payByOffline(mView.getBaseActivity(),
                        depositResponseBean.getDepositId(), //transactionId，该笔交易的唯一标识，不能 超过 32 位
                        "1", //CustomerId，可以是用户 id、游戏 id，也 可以是空字符串，但是长度不能高于 50 位
                        depositResponseBean.getCurrency(), //Currency,只能传 Config.K_CURRENCY_ID
                        depositResponseBean.getPrice() + "", //price 目前只支持整数的字符串
                        "loan",  //PropsName,商品名称，比如金币、钻石、体 力等;长度限制为 50，并且不能为 null
                        mMethod, //publisher 支付方式，目前只有两个可选:ATM/OTC PublisherCode.PUBLISHER_OFFLINE_OTC
                        "1",  //msisdn 手机号码，可以为空，也可以不为 空，长度不能超过 15 位
                        // do not need to show
                        false,  //是否使用 sdk 的 loading 对话框。true 为使 用，false 为不使用
                        new IPayCallback() {
                            @Override
                            public void onFinished(BlueMessage blueMessage) {
                                subscriber.onNext(blueMessage);
                            }

                            @Override
                            public String onPrepared() {
                                return null;
                            }
                        });


            }
        });
    }


    public interface CallBack {
        void onMethodsObtain(InfoAdapterEnum repaymentMethodAdapter);
    }
}

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
import com.daunkredit.program.sulu.bean.DepositMethodsBean;
import com.daunkredit.program.sulu.bean.DepositResponseBean;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.InfoAdapterEnum;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.view.BankPaymentActivity;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.certification.status.PayMethodEnum;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentFragment;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentView;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.orhanobut.logger.Logger;

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
 * @创建日期: 2017/6/21 16:54
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanRepaymentFraPreImp extends LoanRepaymentPreImp implements LoanRepaymentFraPresenter {

}

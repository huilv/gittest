package com.daunkredit.program.sulu.view.fragment.presenter;

import android.app.Dialog;

import com.bluepay.pay.BluePay;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;

/**
 * @作者:My
 * @创建日期: 2017/6/23 17:48
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoanRepaymentPresenter extends BaseFragmentPresenter {
    void getRepaymentMethodAdapter(LoanRepaymentFraPreImp.CallBack callBack);

    void doRepayRequest(LoanAppBeanFather mLoanAppBean, final String repayMethodorBank, final BluePay bluePay);

    void initBlueSDK(BluePay bluePay, Dialog progressDialog);
}

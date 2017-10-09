package com.daunkredit.program.sulu.view.fragment.presenter;

import com.bluepay.pay.BluePay;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:14
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoanRepaymentExpFraPresenter extends LoanRepaymentPresenter{
    void initView(BluePay bluePay, XLeoProgressDialog progressDialog);

    //void getRepayMethodAdapter();

   // void doRepayRequest(LoanAppBeanFather loanBean, String repayMethodorBank, BluePay bluePay);
}

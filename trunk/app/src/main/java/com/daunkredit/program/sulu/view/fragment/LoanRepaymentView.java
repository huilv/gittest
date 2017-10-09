package com.daunkredit.program.sulu.view.fragment;

import com.bluepay.pay.BlueMessage;
import com.daunkredit.program.sulu.app.base.BaseFragmentView;
import com.daunkredit.program.sulu.common.InfoAdapter;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:52
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoanRepaymentView extends BaseFragmentView {
    void doOnBlueMessage(BlueMessage blueMessage);

    void setStateString(String string);

    void onMethodChoose(InfoAdapter.InfoItem data);
}

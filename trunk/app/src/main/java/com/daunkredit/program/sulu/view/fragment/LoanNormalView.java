package com.daunkredit.program.sulu.view.fragment;

import com.daunkredit.program.sulu.app.base.BaseFragmentView;
import com.sulu.kotlin.data.LoanRange;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:01
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoanNormalView extends BaseFragmentView{
    void onSpanObtain(LoanRange loanRange);
}

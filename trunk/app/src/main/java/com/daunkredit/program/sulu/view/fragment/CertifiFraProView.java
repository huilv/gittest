package com.daunkredit.program.sulu.view.fragment;

import com.daunkredit.program.sulu.app.base.BaseFragmentView;
import com.daunkredit.program.sulu.bean.ProgressBean;

/**
 * @作者:My
 * @创建日期: 2017/6/21 15:29
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface CertifiFraProView extends BaseFragmentView {

    void resetProgress();

    void updateProgress();

    void updateSubmitState();

    void onProgressBeaObtain(ProgressBean progressBean);
}

package com.daunkredit.program.sulu.view.presenter;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;
import com.daunkredit.program.sulu.bean.MsgInboxBean;

/**
 * @作者:My
 * @创建日期: 2017/6/21 18:30
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MsgBoxActPresenter extends BaseActivityPresenter {
    void initData();

    void setMsgChecked(MsgInboxBean msgId);
}

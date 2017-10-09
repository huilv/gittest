package com.daunkredit.program.sulu.app.base.presenter;

import com.daunkredit.program.sulu.app.base.BaseActivityView;

/**
 * @作者:My
 * @创建日期: 2017/6/21 9:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface BaseActivityPresenter extends BaseSuluPresenter{
    void uploadXTrace();

    void initYW();

    void attach(BaseActivityView tBaseActivity);

    void disAttach();

    boolean isAttached();
}

package com.daunkredit.program.sulu.app.base.presenter;

import com.daunkredit.program.sulu.app.base.BaseFragmentView;

/**
 * @作者:My
 * @创建日期: 2017/6/21 10:07
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface BaseFragmentPresenter extends BaseSuluPresenter {
    void attach(BaseFragmentView v);

    void detach();

    void uploadXTrace();

    boolean isAttached();
}

package com.daunkredit.program.sulu.view.fragment.presenter;

import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter;

/**
 * @作者:My
 * @创建日期: 2017/6/21 16:58
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MeFraPresenter extends BaseFragmentPresenter {
    void initUserAccountInfo();

    void logout();

    void login();
}

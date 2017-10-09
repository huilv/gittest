package com.daunkredit.program.sulu.app.base.presenter;

/**
 * @作者:My
 * @创建日期: 2017/6/26 16:56
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface BaseSuluPresenter {
    void showLoading(String string);

    void dismissLoading();

    void finish();
}

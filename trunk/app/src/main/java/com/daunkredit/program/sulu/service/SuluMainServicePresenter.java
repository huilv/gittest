package com.daunkredit.program.sulu.service;

/**
 * @作者:My
 * @创建日期: 2017/7/17 14:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

interface SuluMainServicePresenter {
    void initBinder(SuluMainService.SuluMainBinder binder);

    void startBasicTask();

    void onUnbind();
}

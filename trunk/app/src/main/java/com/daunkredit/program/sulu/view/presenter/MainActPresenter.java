package com.daunkredit.program.sulu.view.presenter;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;
import com.daunkredit.program.sulu.bean.VersionBean;

/**
 * @作者:My
 * @创建日期: 2017/6/21 18:10
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MainActPresenter extends BaseActivityPresenter{
    void dealResult(String status);

    void updateApk(VersionBean versonBean);

    void updateStatus(String token);

    void initThirdService();

}

package com.daunkredit.program.sulu.view.login.presenter;

import android.widget.Button;
import android.widget.EditText;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;
import com.daunkredit.program.sulu.bean.LoginRequestBean;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoginActPresenter extends BaseActivityPresenter {
    void obtainSmsCode(Button buttonObtain, EditText editTextPhone);

    void doLogin(LoginRequestBean loginRequestBean);
}

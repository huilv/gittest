package com.daunkredit.program.sulu.view.login;

import android.text.Editable;
import android.widget.EditText;

import com.daunkredit.program.sulu.app.base.BaseActivityView;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:44
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface LoginActView extends BaseActivityView {
    void checkInputState(EditText v, Editable s);
    boolean checkLegalState(EditText editTextPhone);
    void refreshCaptcha();

    void onLoginError();
}

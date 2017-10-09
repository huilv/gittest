package com.daunkredit.program.sulu.widget.selfdefedittext;

import android.content.Context;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/3/28 18:40
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public abstract class OnCheckInputResultAdapter implements OnCheckInputResult {


    @Override
    public abstract boolean onCheckResult(EditText v);

    @Override
    public void onWrong(EditText v) {
        v.setTextColor(v.getResources().getColor(R.color.colorAlerm_red));
        v.requestFocus();
    }

    @Override
    public void onReEdit(EditText v) {
        v.setTextColor(v.getResources().getColor(R.color.white));
    }

    @Override
    public void onRight(EditText v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        v.clearFocus();
    }

    @Override
    public void onTextChanged(EditText v, Editable s) {
    }
}

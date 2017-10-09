package com.daunkredit.program.sulu.widget.selfdefedittext;

import android.text.Editable;
import android.widget.EditText;

/**
 * @作者:My
 * @创建日期: 2017/3/28 18:39
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface OnCheckInputResult {
    boolean onCheckResult(EditText v);

    void onWrong(EditText v);

    void onReEdit(EditText v);
    void onRight(EditText v);

    void onTextChanged(EditText v, Editable s);
}

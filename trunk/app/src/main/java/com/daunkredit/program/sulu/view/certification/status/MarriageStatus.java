package com.daunkredit.program.sulu.view.certification.status;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/3/24 11:37
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public enum MarriageStatus implements InfoValueType {
    MARRIED("MARRIED", R.string.enum_marriage_married), SINGLE("SINGLE", R.string.enum_marriage_single), DIVORCED("DIVORCED", R.string.enum_marriage_divorced), WIDOWED("WIDOWED", R.string.enum_marriage_widowed);
    private final String mValue;
    private final int showString;

    MarriageStatus(String value, int stringId){
        this.mValue = value;
        this.showString = stringId;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public int getShowString() {
        return showString;
    }
}

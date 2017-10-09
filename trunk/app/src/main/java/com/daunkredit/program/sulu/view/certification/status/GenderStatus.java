package com.daunkredit.program.sulu.view.certification.status;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/3/24 12:07
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public enum GenderStatus implements InfoValueType{
    MALE("MALE", R.string.enum_gender_male), FEMALE("FEMALE",R.string.enum_gender_female);
    private final int mStrigId;
    private final String mValue;

    GenderStatus(String value, int stringId){
        mValue = value;
        mStrigId = stringId;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public int getShowString() {
        return mStrigId;
    }
}

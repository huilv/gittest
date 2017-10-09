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

public enum DurationStatus implements InfoValueType {
    THREE_MONTH("THREE_MONTH", R.string.enum_period_three_month),SIX_MONTH("SIX_MONTH",R.string.enum_period_six_month),ONE_YEAR("ONE_YEAR",R.string.enum_period_one_year),TWO_YEAR("TWO_YEAR",R.string.enum_period_two_year),OVER_TWO_YEAR("OVER_TWO_YEAR",R.string.enum_period_over_two_year);
    private final String mValue;
    private final int showString;

    DurationStatus(String value, int stringId){
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

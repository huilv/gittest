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

public enum SalaryStatus implements InfoValueType{
    BELOW_2B("BELOW_2B", R.string.enum_salary_below_2b),
    BETWEEN_2B_4B("BETWEEN_2B_4B", R.string.enum_salary_between_2b_4b),
    BETWEEN_4B_8B("BETWEEN_4B_8B", R.string.enum_salary_between_4b_8b),
   // BETWEEN_8B_12B("BETWEEN_8B_12B", R.string.enum_salary_between_8b_12b),
    //BETWEEN_12B_20B("BETWEEN_12B_20B", R.string.enum_salary_between_12b_20b),
   // OVER_20B("OVER_20B", R.string.enum_salary_over_20b);
    OVER_8B("OVER_8B", R.string.enum_salary_over_8b);
    private final int mStrigId;
    private final String mValue;

    SalaryStatus(String value, int stringId){
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

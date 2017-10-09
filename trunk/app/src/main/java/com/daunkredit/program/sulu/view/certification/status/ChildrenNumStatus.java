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

public enum ChildrenNumStatus implements InfoValueType {
    ZERO("ZERO", R.string.enum_children_zero),ONE("ONE",R.string.enum_children_one),TWO("TWO",R.string.enum_children_two),
    THREE("THREE",R.string.enum_children_three),FOUR("FOUR",R.string.enum_children_four), OVER_FOUR("OVER_FOUR", R.string.enum_children_overfour);
    private final String mValue;
    private final int showString;

    ChildrenNumStatus(String value, int stringId){
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

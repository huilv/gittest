package com.daunkredit.program.sulu.widget.selfdefedittext;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/3/31 14:06
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public enum StyleEnum {
    RECTANGLE(R.drawable.selector_edittext_input),LINE(0),WITHRIGHTBUTTON(1);
    private int style;
    StyleEnum(int style){
        this.style = style;
    }
    public int getStyle(){
        return style;
    }
}

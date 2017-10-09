package com.daunkredit.program.sulu.common;

import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.sulu.kotlin.fragment.OnInfoAdapterItemClickListener;

import org.jetbrains.annotations.NotNull;

/**
 * @作者:My
 * @创建日期: 2017/3/24 11:59
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface InfoAdapter {


    void setOnItemClickListener(@NotNull OnInfoAdapterItemClickListener onInfoAdapterItemClickListener);

    interface InfoItem{

        String getInfoStr();
        String getValueStr();
        InfoType getType();
        int getId();
    }
}

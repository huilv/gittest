package com.daunkredit.program.sulu.service;

import android.content.Context;

/**
 * @作者:My
 * @创建日期: 2017/7/18 11:28
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

interface TraceServiceInterface {
    Context getCtx();

    boolean isPermissionGranted();
}

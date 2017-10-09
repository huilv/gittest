package com.daunkredit.program.sulu.service;

import android.content.Context;
import android.content.Intent;

/**
 * @作者:My
 * @创建日期: 2017/7/17 14:44
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

interface SuluMainServiceInterface {
    Context getCtx();

    void sendBroadCoast(Intent intent);

    boolean isNeedUpdate(int latestVersionCode);
}

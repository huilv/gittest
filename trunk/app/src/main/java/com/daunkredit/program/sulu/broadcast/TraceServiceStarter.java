package com.daunkredit.program.sulu.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.service.TraceService;

/**
 * @作者:My
 * @创建日期: 2017/7/18 16:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TraceServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerWrapper.d("receive broadcast and restart service");
        context.startService(new Intent(context, TraceService.class));
    }
}

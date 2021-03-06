package com.daunkredit.program.sulu.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
/**
 * @作者:My
 * @创建日期: 2017/6/26 10:21
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LocalMemoryManager {


    public static long getMaxMem(Context context) {
        long maxRunMemory = Runtime.getRuntime().maxMemory();
        LoggerWrapper.d("MemoryManager", "maxRunMemory:" + maxRunMemory);
        long memClassInt = 0L;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(am != null) {
            int maxM = am.getMemoryClass();
            memClassInt = (long)(maxM * 1024 * 1024);
        }

        LoggerWrapper.d("MemoryManager", "memClassInt:" + memClassInt);
        long maxM1 = 0L;
        if(memClassInt < maxRunMemory) {
            maxM1 = memClassInt;
        } else {
            maxM1 = maxRunMemory;
        }

        return maxM1;
    }

    public static long getVmMemRemained() {
        return Runtime.getRuntime().maxMemory() - getVMAlloc();
    }

    public static long getVMAlloc() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static long getNativeHeapSize() {
        return Debug.getNativeHeapSize();
    }


}

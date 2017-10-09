package com.daunkredit.program.sulu.common.utils;

import com.orhanobut.logger.Logger;

/**
 * @作者:My
 * @创建日期: 2017/4/21 12:22
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoggerWrapper {
    private static final boolean DOLOG = true;
    public static void d(String message,Object ...obj){
        if (DOLOG) {
            Logger.d(message,obj);
        }
    }

    public static void d(Object o){
        if (DOLOG) {
            Logger.d(o);
        }
    }

    public static void e(String message,Object ... objs){
        if (DOLOG) {
            Logger.e(message,objs);
        }
    }
    public static void e(Throwable e,String message,Object ... objs){
        if (DOLOG) {
            Logger.e(e,message,objs);
        }
    }

    public static void w(String message,Object...objs){
        if (DOLOG) {
            Logger.w(message,objs);
        }
    }
}

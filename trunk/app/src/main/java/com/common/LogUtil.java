package com.common;

import android.text.TextUtils;
import android.util.Log;

import com.socks.library.KLog;

import static com.common.LogUtil.Level.A;
import static com.common.LogUtil.Level.D;
import static com.common.LogUtil.Level.E;
import static com.common.LogUtil.Level.I;
import static com.common.LogUtil.Level.JSON;
import static com.common.LogUtil.Level.V;
import static com.common.LogUtil.Level.W;
import static com.common.LogUtil.Level.XML;

/**
 * Created by hsg on 12/16/16.
 * <p>
 * use klog format log info, such RegionLevel json, xml etc
 * but cannot show error stack, so use log to show error stack
 */

public final class LogUtil {
    private static final String DEFAULT_TAG = "LogUtil";
    private static String tag;
    private static boolean isShowLog;

    enum Level {
        V,
        D,
        I,
        W,
        E,
        A,
        JSON,
        XML
    }

    private LogUtil() {
    }

    public static void init(boolean isShowLog) {
        init(isShowLog, DEFAULT_TAG);
    }

    public static void init(boolean isShowLog, final String tag) {
        LogUtil.isShowLog = isShowLog;
        LogUtil.tag = TextUtils.isEmpty(tag) ? DEFAULT_TAG : tag;
        KLog.init(isShowLog);
    }

    /**
     * show error stack
     *
     * @param e
     */
    public static void e(Throwable e) {
        e(tag, e);
    }

    /**
     * show error stack
     *
     * @param msg
     * @param e
     */
    public static void e(final String msg, Throwable e) {
        e(tag, msg, e);
    }

    /**
     * show error stack
     *
     * @param tag
     * @param msg
     * @param e
     */
    public static void e(final String tag, final String msg, Throwable e) {
        if (isShowLog) {
            Log.e(tag, msg, e);
        }
    }

    public static void v(final String msg) {
        v(tag, msg);
    }

    public static void d(final String msg) {
        d(tag, msg);
    }

    public static void i(final String msg) {
        i(tag, msg);
    }

    public static void w(final String msg) {
        w(tag, msg);
    }

    public static void e(final String msg) {
        e(tag, msg);
    }

    public static void a(final String msg) {
        a(tag, msg);
    }

    public static void json(final String msg) {
        json(tag, msg);
    }

    public static void xml(final String msg) {
        xml(tag, msg);
    }

    public static void v(final String tag, final String msg) {
        printLog(V, tag, msg);
    }

    public static void d(final String tag, final String msg) {
        printLog(D, tag, msg);
    }

    public static void i(final String tag, final String msg) {
        printLog(I, tag, msg);
    }

    public static void w(final String tag, final String msg) {
        printLog(W, tag, msg);
    }

    public static void e(final String tag, final String msg) {
        printLog(E, tag, msg);
    }

    public static void a(final String tag, final String msg) {
        printLog(A, tag, msg);
    }

    public static void json(final String tag, final String msg) {
        printLog(JSON, tag, msg);
    }

    public static void xml(final String tag, final String msg) {
        printLog(XML, tag, msg);
    }

    private static void printLog(final Level level, final String tag, final String msg) {
        switch (level) {
            case V:
                KLog.v(tag, msg);
                break;
            case D:
                KLog.d(tag, msg);
                break;
            case I:
                KLog.i(tag, msg);
                break;
            case W:
                KLog.w(tag, msg);
                break;
            case E:
                KLog.e(tag, msg);
                break;
            case A:
                KLog.a(tag, msg);
                break;
            case JSON:
                KLog.json(tag, msg);
                break;
            case XML:
                KLog.xml(tag, msg);
                break;
            default:
                break;
        }
    }
}

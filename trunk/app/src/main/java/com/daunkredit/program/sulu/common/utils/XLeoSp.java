package com.daunkredit.program.sulu.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @作者:My
 * @创建日期: 2017/5/15 17:03
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XLeoSp {
    public static XLeoSp            sXLeoSp;
    private       SharedPreferences mSp;
    private XLeoSp(){
    }
    private XLeoSp(Context context){
        mSp = context.getSharedPreferences("config.cfg",Context.MODE_PRIVATE);
    }
    public  static XLeoSp getInstance(Context context){
        if (sXLeoSp == null) {
            synchronized (XLeoSp.class) {
                if (sXLeoSp == null) {
                    sXLeoSp = new XLeoSp(context);
                }
            }
        }
        return sXLeoSp;
    }

    public boolean getBoolean(String key){
        return mSp.getBoolean(key, false);
    }
    public void putBoolean(String key, boolean value){
        mSp.edit().putBoolean(key,value).commit();
    }
    public void remove(String key){
        mSp.edit().remove(key).commit();
    }

    public Long getLong(String key) {
        return mSp.getLong(key,-1);
    }

    public void putLong(String key, Long value) {
        mSp.edit().putLong(key,value).commit();
    }

    public void putDouble(String currentLoanRate, double mRate) {
        mSp.edit().putString(currentLoanRate,"" + mRate).commit();
    }
    public double getDouble(String key,double value){
        String string = mSp.getString(key, "" + value);
        return Double.valueOf(string);
    }
}

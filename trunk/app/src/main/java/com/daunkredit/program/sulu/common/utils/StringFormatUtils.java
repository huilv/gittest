package com.daunkredit.program.sulu.common.utils;

import android.content.Context;
import android.os.Environment;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;

/**
 * @作者:My
 * @创建日期: 2017/3/23 16:05
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class StringFormatUtils {


    public static String dayTimeFormat(Context context, int period) {
        StringBuffer result = new StringBuffer();
        int days = period;
        if (days > 365) {
            int year = days / 365;
            result.append(year + context.getString(R.string.years));
            days = days % 365;
        }

        if (days >30) {
            int month = days / 30;
            result.append(month + context.getString(R.string.months));
            days = days & 30;
        }

        result.append(days + context.getString(R.string.days));
        return result.toString();
    }

    public static String moneyFormat(int amount) {
        StringBuffer result = new StringBuffer();
        while(amount > 1000){
            int temp = amount % 1000;
            amount = amount / 1000;
            String tempResult;
            if(temp < 10){
                tempResult = "00" + temp;
            }else if(temp <100){
                tempResult = "0" + temp;
            }else{
                tempResult = temp + "";
            }
            result.insert(0,"," + tempResult);
        }
        result.insert(0, "" + amount);
        return result.toString();
    }

    public static String indMoneyFormat(double money){
        return  String.format("Rp.%,.0f", money).replaceAll(",", ".")+",00";
    }
    public static String moneyFormat(double paidAmount){
        return moneyFormat(String.valueOf(paidAmount));
    }

    public static String moneyFormat(String paidAmount) {
        if (paidAmount == null) {
            return null;
        }
        String[] split = null;
        if (paidAmount.contains(".")) {
             split = paidAmount.split("\\.");
            paidAmount = split[0];
        }
        StringBuilder result = new StringBuilder();
        int length = paidAmount.length();
        int startIndex = length;
        String substring = null;
        while(startIndex > 3) {
            substring = paidAmount.substring(startIndex - 3, startIndex);
            startIndex = startIndex - 3;
            result.insert(0, "," + substring);
        }
        result.insert(0, paidAmount.substring((0), startIndex));
        if (split != null && split.length >=2) {
           result.append("." + split[1].substring(0,1));
        }
        return result.toString();
    }


    public static File getFileByFileName(Context context,String fileName){
        File resultParent;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            resultParent = new File(Environment.getExternalStorageDirectory(), FieldParams.APP_NAME);
        }else{
            resultParent = new File(context.getFilesDir(),FieldParams.APP_NAME);
        }
        if (!resultParent.exists()) {
            resultParent.mkdirs();
        }
        return new File(resultParent,fileName);
    }

    @Nullable
    public static double formatDouble(double d, int i) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}

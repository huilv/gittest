package com.daunkredit.program.sulu.common.utils;

import com.common.StringUtil;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Miaoke on 2017/3/23.
 */

public class TimeFormat {


    public static String convertTime(String srcTime){
        return convertTime(srcTime,"yyy-MM-dd HH:mm");
    }

    public static String convertTimeDay(String srcTime){
        return convertTime(srcTime,"yy-MM-dd");
    }

    public static String convertTime(String srcTime,String formatStyle){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat(formatStyle);
        simpleDateFormatLocal.setTimeZone(TimeZone.getDefault());
        try {
            if(StringUtil.isNullOrEmpty(srcTime)){
                return "0";
            }
            Date srcDate = simpleDateFormat.parse(srcTime);
            String targetTime = simpleDateFormatLocal.format(srcDate);
            return targetTime;
        } catch (ParseException e) {
            e.printStackTrace();
            Logger.d("parse time exception: " +e.getMessage());
        }

        return "0";
    }

}

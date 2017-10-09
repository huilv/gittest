package com.daunkredit.program.sulu.common.network;

/**
 * Created by localuser on 2017/2/27.
 *
 * 请求captcha图形验证码
 */

public class CaptchaApi {

    public static String formatCaptchaUrl(String sid){
        return formatCaptchaUrl(sid, 200, 80);
    }

    public static String formatCaptchaUrl(String sid, int width, int height){
        return String.format(Holder.captchaUrl, sid, width, height);
    }

    private static class Holder{
        private static String captchaUrl = ServiceGenerator.getApiBaseUrl() + "auth/captcha?sid=%1$s&width=%2$d&height=%3$d";

    }

    public static void main(String[] argv){

        System.out.println(formatCaptchaUrl("1"));
    }

}

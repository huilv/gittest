package com.daunkredit.program.sulu.bean;

/**
 * Created by localuser on 2017/2/27.
 */

public class LoginRequestBean {

    public Mobile mobile;
    public SMSCode smsCode;
    public CaptchaSID captchaSid;
    public Captcha captcha;
    public String inviteCode;


    public static class SMSCode{
        String smsCode;

        public SMSCode(String str){
            this.smsCode = str;
        }
        public String toString(){
            return smsCode;
        }
    }

    public static class CaptchaSID{
        String captchaSid;

        public CaptchaSID(String str){
            this.captchaSid = str;
        }
        public String toString(){
            return captchaSid;
        }
    }

    public static class Captcha{
        String captcha;

        public Captcha(String str){
            this.captcha = str;
        }
        public String toString(){
            return captcha;
        }
    }

    public static class Mobile{
        String mobile;

        public Mobile(String str){
            this.mobile = str;
        }
        public String toString(){
            return mobile;
        }
    }
}

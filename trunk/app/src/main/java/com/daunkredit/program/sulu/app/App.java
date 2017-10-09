package com.daunkredit.program.sulu.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.wxlib.util.SysUtil;
import com.daunkredit.program.sulu.app.custom.alipush.CustomPushNotifactionManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.tencent.bugly.crashreport.CrashReport;


public class App extends MultiDexApplication {

    //百川云旺app_key
    public static final String ALI_IM_APP_KEY  = "24638605";
    //百川云旺客服账号
    public static final String SERVICE_ACCOUNT = "谁动了我的狂乱的小草";
    //热线电话
    public static final String HOTLINE         = "+86 02154660121-305";
    private static final String TAG            = "app_log";
    //小米通道
    private String miAppid = "2882303761517577598";
    private String miAppKey = "5261757761598";
    public static App instance;

    public void onCreate() {
        super.onCreate();

       //bug崩溃分析
        CrashReport.initCrashReport(getApplicationContext(), "92317fd2ff", false);
 //测试测试
        //aaaaaaaaaaaaaaaa
        SysUtil.setApplication(this);
        if(SysUtil.isTCMSServiceProcess(this)){
            return;
        }
        if(SysUtil.isMainProcess()){
            YWAPI.init(this, ALI_IM_APP_KEY);
        }
        instance = this;
        initCloudChannel(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LoggerWrapper.d(TAG, "init cloudchannel success");
                CustomPushNotifactionManager.init();
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LoggerWrapper.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(applicationContext, miAppid, miAppKey);
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(applicationContext);
    }

}
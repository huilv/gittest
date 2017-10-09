package com.daunkredit.program.sulu.app.custom.alipush;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.alibaba.sdk.android.push.MiPushSystemNotificationActivity;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.AppStateManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.view.SplashActivity;

import java.util.Map;

/**
 * @作者:My
 * @创建日期: 2017/5/16 10:55
 * @描述:小米弹窗支持
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XiaoMiSupportActivity extends MiPushSystemNotificationActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.xiaomisupport_activity);
    }

    @Override
    protected void onMiPushSysNoticeOpened(String s, String s1, Map<String, String> map) {
        AppStateManager.isRunForeground(this, new AppStateManager.StateObtainListener() {
                @Override
                public void onSuccess(boolean result) {
                    if (!result) {
                        startActivity(new Intent(XiaoMiSupportActivity.this, SplashActivity.class));
                        finish();
                    }else{
                        finish();
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    LoggerWrapper.e(e,"get app running state failed");
                    finish();
                }
        });
        LoggerWrapper.d("xiao mi support notice opened");
    }
}

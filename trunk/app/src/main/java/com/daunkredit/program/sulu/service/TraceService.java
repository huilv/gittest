package com.daunkredit.program.sulu.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.daunkredit.program.sulu.common.utils.LoggerWrapper;

/**
 * @作者:My
 * @创建日期: 2017/7/18 11:27
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TraceService extends Service implements TraceServiceInterface {
    private static Service               mInstance;
    private static Notification          mNotification;
    private        TraceServicePresenter mPresenter;
    private boolean mFristStarted = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LoggerWrapper.d("onbind");
        return null;
    }

    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoggerWrapper.d("onstart");

        if (this.mFristStarted) {
            this.mFristStarted = false;
            startKernel(this.getApplicationContext());
        }

        mPresenter.startGpsTrace();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mPresenter = new TraceServicePresenterImpl(this);

        mInstance = this;
        mNotification = new Notification();
        if (Build.VERSION.SDK_INT < 18) {
            try {
                startForeground(9734, mNotification);
            } catch (Exception e) {
            }
        } else {
            try {
                startForeground(9734, mNotification);
                KernelService kernelService = new KernelService();
                kernelService.startForeground(9734, mNotification);
                kernelService.stopSelf();
            } catch (Exception e) {
            }
        }

        LoggerWrapper.d("oncreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LoggerWrapper.d("ondestroy");

        if (Build.VERSION.SDK_INT < 18) {
            try {
                this.stopForeground(true);
            } catch (Throwable var2) {
            }
        } else {
            stopKernel(this.getApplicationContext());
        }

        mPresenter.onDestroy();
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LoggerWrapper.d("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public Context getCtx() {
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean isPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


    static void startKernel(Context context) {
        try {
            int cellVersion = getSupportForegroundVer(context);
            if (Build.VERSION.SDK_INT < cellVersion) {
                Intent innerService;
                innerService = new Intent(context, TraceService.KernelService.class);
                context.startService(innerService);
            }

        } catch (Throwable var2) {
            LoggerWrapper.e("TraceService", "startKernel" + var2);
        }
    }

    static void stopKernel(Context var0) {
        try {
            int var1 = getSupportForegroundVer(var0);
            if (Build.VERSION.SDK_INT < var1) {
                Intent var3;
                var3 = new Intent(var0, TraceService.KernelService.class);
                var0.stopService(var3);
            }

        } catch (Throwable var2) {
            LoggerWrapper.e("TraceService", "stopKernel" + var2);
        }
    }

    static int getSupportForegroundVer(Context var0) {
        int var1 = 24;
        return var1;
    }

    public static class KernelService extends Service {
        private static TraceService.KernelService sKernelService;

        public KernelService() {
        }

        public void onCreate() {
            super.onCreate();
            sKernelService = this;
        }

        public int onStartCommand(Intent var1, int var2, int var3) {
            try {
                TraceService.mInstance.startForeground(9734, TraceService.mNotification);
                this.startForeground(9734, TraceService.mNotification);
                //                this.stopForeground(true);
                this.stopSelf();
            } catch (Throwable var7) {
            }

            return super.onStartCommand(var1, var2, var3);
        }

        public void onDestroy() {
            try {
                this.stopForeground(true);
            } catch (Throwable var2) {
                LoggerWrapper.e("TraceService", "onDestroy" + var2);
            }

            sKernelService = null;
            super.onDestroy();
        }

        public IBinder onBind(Intent var1) {
            return null;
        }
    }
}

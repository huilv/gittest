package com.daunkredit.program.sulu.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.daunkredit.program.sulu.common.utils.LoggerWrapper;

/**
 * @作者:My
 * @创建日期: 2017/4/6 14:01
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SuluMainService extends Service implements SuluMainServiceInterface {

    private SuluMainBinder    mBinder;

    private SuluMainServicePresenter mPresenter;
    private ServiceConnection mConn;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPresenter = new SuluMainServicePresenterImpl(this);
        mPresenter.startBasicTask();

        mBinder = new SuluMainBinder();
        mPresenter.initBinder(mBinder);
        bindAndStartAnotherService();
        return mBinder;
    }

    private void bindAndStartAnotherService() {
        LoggerWrapper.d("bindandstart");
        Intent bindIntent = new Intent(this,TraceService.class);
        startService(bindIntent);
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(bindIntent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unbindService(mConn);
        mPresenter.onUnbind();
        mPresenter = null;
        return super.onUnbind(intent);
    }

    @Override
    public Context getCtx() {
        return getBaseContext();
    }

    @Override
    public void sendBroadCoast(Intent intent) {
        sendBroadcast(intent);
    }

    @Override
    public boolean isNeedUpdate(int latestVersionCode) {
        PackageManager packageManager =getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return packageInfo.versionCode < latestVersionCode;
    }

    public class SuluMainBinder extends Binder {
        Handler  mHandler;
        Runnable mTask;
        private Handler mMainHandler;

        public void refrashStatus() {
            mHandler.post(mTask);
        }

        public void setMainActivityHandler(Handler handler) {
            mMainHandler = handler;
        }

        public Handler getMainActivityHandler() {
            return mMainHandler;
        }

        public void clearTask() {
            mHandler.removeCallbacksAndMessages(null);
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        public Handler getHandler() {
            return mHandler;
        }

        public void setTask(Runnable task) {
            mTask = task;
        }

        public Runnable getTask() {
            return mTask;
        }
    }
}
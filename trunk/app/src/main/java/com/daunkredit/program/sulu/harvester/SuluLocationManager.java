package com.daunkredit.program.sulu.harvester;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * @作者:My
 * @创建日期: 2017/6/23 10:40
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SuluLocationManager {

    private static final long HEART_BEAT_STEP = 60 * 60 * 1000;
    private Handler mHandler;
    Context mContext;
    private LocationManager mLocationManager;

    private LocationListener mLocationListener;
    private boolean          isStarted;
    private Location         mLocation;
    private List<CallBack>   mCallBacks;
    public               Long earlyTime    = 0L;
    public               Long latestTime   = 0L;
    private static final int  mMinTime     = 0;
    private static final int  CACHE_TIMES  = 3;
    private              int  current_time = 0;
    private static final int  CATCHSPACE   = 1000 * 2 * 10;

    public static class Holder {
        private static SuluLocationManager sSuluLocationManager;

        public static SuluLocationManager getInstance(Context context) {
            if (sSuluLocationManager == null) {
                synchronized (SuluLocationManager.class) {
                    if (sSuluLocationManager == null) {
                        sSuluLocationManager = new SuluLocationManager(context);
                    }
                }
            }
            return sSuluLocationManager;
        }
    }

    private SuluLocationManager() {
    }

    private SuluLocationManager(Context context) {
        mContext = context;
        mCallBacks = new ArrayList<>();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mHandler = new Handler(Looper.myLooper());
    }

    public void startLocation(CallBack callBack) {
        mCallBacks.add(callBack);
        if (isStarted) {
            return;
        }
        isStarted = true;
        startListenerLocationChange();
        startHeartBeatLocation();
    }

    private void startHeartBeatLocation() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                List<String> providers = mLocationManager.getProviders(true);
                String locationProvider;
                Location locationObject = null;
                if (locationObject == null && providers.contains(LocationManager.GPS_PROVIDER)) {
                    locationProvider = LocationManager.GPS_PROVIDER;
                    locationObject = mLocationManager.getLastKnownLocation(locationProvider);
                    Logger.d("Get GPS Provider");
                }

                if (locationObject == null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    locationObject = mLocationManager.getLastKnownLocation(locationProvider);
                    Logger.d("Get network provider");
                }

                if (locationObject == null && providers.contains(LocationManager.PASSIVE_PROVIDER)) {
                    locationProvider = LocationManager.PASSIVE_PROVIDER;
                    locationObject = mLocationManager.getLastKnownLocation(locationProvider);
                    Logger.d("PASSIVE_PROVIDER location provider.");
                }
                for (CallBack callBack : mCallBacks) {
                    callBack.onObtain(locationObject);
                }
                mHandler.postDelayed(this, HEART_BEAT_STEP);
            }
        });
    }

    private void startListenerLocationChange() {
        // 获取系统的LocationManager服务

        // 定义一个LocationListener来响应定位更新
        // 当地理位置信息有变化的时候回调
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // 当地理位置信息有变化的时候回调
                makeUseOfLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                LoggerWrapper.d("onProviderEnabled: ");
            }

            public void onProviderDisabled(String provider) {
                LoggerWrapper.d("onProviderDisabled");
            }
        };

        // 向Location Manager注册LocationListener监听定位更新

        List<String> providers = mLocationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mMinTime, 0, mLocationListener);
            LoggerWrapper.d("Get GPS Provider");
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, 0, mLocationListener);
            LoggerWrapper.d("Get network provider");
        }

        if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, mMinTime, 0, mLocationListener);
            LoggerWrapper.d("Get passive provider");
        }
    }

    public void stopLocation() {
        if (mLocationManager != null && isStarted) {
            mLocationManager.removeUpdates(mLocationListener);
            mCallBacks.clear();
            mCallBacks = null;
        }
    }

    public Location getLocation() {
        return mLocation;
    }

    private synchronized void makeUseOfLocation(Location location) {
        LoggerWrapper.d("location handler");
        current_time++;
        if (isBetterLocation(location, mLocation)) {
            mLocation = location;
        }
        if (current_time >= CACHE_TIMES) {
            if (mCallBacks.size() > 0) {
                earlyTime = latestTime;
                latestTime = System.currentTimeMillis();
                for (CallBack callBack : mCallBacks) {
                    callBack.onObtain(mLocation);
                }
                current_time = 0;
            }
        }
    }

    /**
     * 判断是一个新的定位测量值是否比当前的定位修正值更好
     *
     * @param location            需要评估的新定位测量值
     * @param currentBestLocation 当前的定位修正值，也就是你想要用来跟新定位测量值比较的定位数据
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // 如果当前没有定位修正值，那么新的定位测量值肯定是更好的
            return true;
        }

        // 检查新的定位测量值是更新的数据还是更旧的数据
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > CATCHSPACE;
        boolean isSignificantlyOlder = timeDelta < -CATCHSPACE;
        boolean isNewer = timeDelta > 0;

        // 如果新的定位测量值晚于当前的定位修正值两分钟，那么使用新的定位测量值，因为用户可能已经移动了
        if (isSignificantlyNewer) {
            return true;
            // 如果新的定位测量值早于当前定位修正值两分钟，那么新的定位测量值应该是过时的
        } else if (isSignificantlyOlder) {
            return false;
        }

        // 检查新的定位测量值精度是否更加精确
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // 检查两个定位测量值是否来源于同一个定位数据源
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // 组合定位的及时性和准确度来评估定位的质量
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * 检查两个定位数据源是否是同一个数据源
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public interface CallBack {
        void onObtain(Object object);
    }
}

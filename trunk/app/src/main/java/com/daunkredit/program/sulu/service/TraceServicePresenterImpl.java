package com.daunkredit.program.sulu.service;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;

import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.harvester.PermissionManager;
import com.daunkredit.program.sulu.harvester.SuluLocationManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @作者:My
 * @创建日期: 2017/7/18 11:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

class TraceServicePresenterImpl implements TraceServicePresenter {
    private final TraceServiceInterface mTraceService;
    private       Handler               mHandler;
    private       Runnable              mGpsTask;

    public TraceServicePresenterImpl(TraceServiceInterface traceService) {
        mTraceService = traceService;
    }

    @Override
    public void startGpsTrace() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.myLooper());
        }
        LoggerWrapper.d("startGps");
        if (Build.VERSION.SDK_INT >= 23) {
            if (mGpsTask == null) {
                mGpsTask = new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        LoggerWrapper.d("gpsTask");
                        if (mTraceService == null) {

                        } else if (!mTraceService.isPermissionGranted()) {
                            LoggerWrapper.d("loaction denied");
                            mHandler.postDelayed(this, 2 * 60 * 1000);
                        } else {
                            startLocationTrace();
                        }
                    }
                };
            }

        } else {
            startLocationTrace();
        }
        mHandler.post(mGpsTask);
    }

    private void startLocationTrace() {
        if (mTraceService == null) {
            return;
        }
        LoggerWrapper.d("startLocation");
        SuluLocationManager.Holder.getInstance(mTraceService.getCtx())
                .startLocation(new SuluLocationManager.CallBack() {
                    @Override
                    public void onObtain(Object object) {
                        if (object != null && object instanceof Location) {
                            sendNewLocation((Location) object);
                        }
                    }
                });
    }

    private void sendNewLocation(Location object) {
        JSONObject locationJsonObject = null;
        try {
            locationJsonObject = getLocationJsonObject(object);
            LoggerWrapper.d("sendNewLocation:" + locationJsonObject);
            if (locationJsonObject != null) {
                PermissionManager.formSend(mTraceService.getCtx(), ServiceGenerator.HARVESTER_URL, locationJsonObject);
            }
        } catch (JSONException e) {
        }
    }

    private JSONObject getLocationJsonObject(Location object) throws JSONException {
        if (object != null) {
            LoggerWrapper.d("getlocationJson:" + object);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", object.getLatitude());
            jsonObject.put("longitude", object.getLongitude());
            jsonObject.put("altitude", object.getAltitude());
            jsonObject.put("createTime", object.getTime());
            return jsonObject;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mTraceService != null) {
            SuluLocationManager.Holder.getInstance(mTraceService.getCtx())
                    .stopLocation();
        }
    }
}

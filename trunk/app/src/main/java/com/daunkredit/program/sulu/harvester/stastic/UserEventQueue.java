package com.daunkredit.program.sulu.harvester.stastic;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.daunkredit.program.sulu.app.App;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.harvester.PermissionManager;
import com.daunkredit.program.sulu.harvester.def.ProtocolName;
import com.daunkredit.program.sulu.harvester.def.ProtocolVersion;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Miaoke on 11/05/2017.
 */

public class UserEventQueue {

    public static final int MAX_CACHE_NUMBER = 10;

    public static BlockingDeque<UserEvent> eventQueue = new LinkedBlockingDeque<>(50);

    public static ThreadPoolExecutor newCachedThreadPool = new ThreadPoolExecutor(2, 7, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(5));

    public static boolean isSending;


    public synchronized static void add(final UserEvent userEvent) {
        try {
            eventQueue.put(userEvent);
            if (eventQueue.size() >= MAX_CACHE_NUMBER && !isSending) {
                isSending = true;
                LinkedBlockingDeque<UserEvent> temp = new LinkedBlockingDeque(10);
                for (int i = 0; i < 10; i++) {
                    UserEvent take = eventQueue.take();
                    temp.put(take);
                }
                take(temp);
                isSending = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void take(final LinkedBlockingDeque<UserEvent> temp) {
        final Gson gson = new Gson();
        final JSONObject userEventEntity = new JSONObject();
        final JSONArray userEventArray = new JSONArray();


        newCachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < temp.size(); i++) {
                    PageView pageView;
                    ClickEvent clickEvent;

                    try {
                        UserEvent userEvent = temp.take();
                        if (userEvent.getActionType() == ActionType.PAGE) {
                            pageView = (PageView) userEvent;
                            userEventArray.put(gson.toJson(pageView));

                            Logger.d("pageView: " + gson.toJson(pageView));
                        } else if (userEvent.getActionType() == ActionType.CLICK) {
                            clickEvent = (ClickEvent) userEvent;
                            userEventArray.put(gson.toJson(clickEvent));

                            Logger.d("clickEvent: " + gson.toJson(clickEvent));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    PackageInfo packageInfo = App.instance.getPackageManager().getPackageInfo(App.instance.getPackageName(), 0);

                    userEventEntity.put("protocolName", ProtocolName.BEHAVIOR_MSG);
                    userEventEntity.put("protocolVersion", ProtocolVersion.V_1_0);
                    userEventEntity.put("versionName", packageInfo.versionName);

                    userEventEntity.put("data", userEventArray);
                } catch (JSONException ex) {
                    Logger.d("JSONException: " + ex.getMessage());
                    ex.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
                }

                if (userEventArray.length() > 0) {
                    Logger.d("Begin to send user action event");
                    PermissionManager.formSend(App.instance.getApplicationContext(), ServiceGenerator.HARVESTER_URL, userEventEntity);
                }
            }
        });
    }

}

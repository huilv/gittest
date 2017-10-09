package com.daunkredit.program.sulu.common.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by milo on 17-2-14.
 */

public class HttpUtil {

    public static void sendGetRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .method("GET", null)
                .url(address)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendPostRequest(String address, okhttp3.Callback callback, RequestBody requestBody, String captchaSid, String captcha ,String smsCode) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();

            Request request = new Request.Builder()
                    .method("POST", requestBody)
                    .header("X-CAPTCHA-SID",captchaSid)
                    .header("X-CAPTCHA",captcha)
                    .header("X-SMS-CODE",smsCode)
                    .url(address)
                    .build();
            client.newCall(request).enqueue(callback);
        }
    }

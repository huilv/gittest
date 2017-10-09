package com.daunkredit.program.sulu.common.network;


import com.daunkredit.program.sulu.view.login.Handle401Interceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    //    prod
    public static final String HARVESTER_URL = "115.159.99.132";
    private static final String API_BASE_URL = "http://45.76.52.206:8888";

    //    Tets
//    public static final String HARVESTER_URL = "115.159.99.132";
//    private static final String API_BASE_URL = "http://192.168.31.115:8888";


    //    public  static final String  API_BASE_URL = "http://192.168.31.132:8888/";
    private static HttpLoggingInterceptor logging;
    private static OkHttpClient.Builder httpClientBuilder;
    private static OkHttpClient okHttpClient;
    private static Retrofit.Builder retrofitBuilder;
    private static Retrofit retrofit;

    static {
        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .addInterceptor(new Handle401Interceptor())
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//
//                        Logger.d(request.toString());
//
//
//                        return chain.proceed(request);
//                    }
//                })
        ;

        okHttpClient = httpClientBuilder.build();

        retrofitBuilder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = retrofitBuilder.client(okHttpClient).build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static String getApiBaseUrl() {
        return API_BASE_URL;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
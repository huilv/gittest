package com.daunkredit.program.sulu.common.network;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.daunkredit.program.sulu.bean.GeoLocationBean;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;

/**
 * @作者:My
 * @创建日期: 2017/7/20 14:03
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class GeoLocationApi {
    public static final String BaseUrl = "http://maps.googleapis.com";
   ///maps/api/geocode/json?latlng=%s,%s&sensor=true&language=zh_cn
    private static OkHttpClient.Builder httpClientBuilder;
    private static OkHttpClient         okHttpClient;
    private static Retrofit.Builder     retrofitBuilder;
    private static Retrofit retrofit;

    private static void init(){
        httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES);

        okHttpClient = httpClientBuilder.build();

        retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = retrofitBuilder.client( okHttpClient ).build();
    }
    public static Observable<GeoLocationBean> getLocationByLocation(Location location){
        return getLocationByLocation(location.getLatitude(), location.getLongitude());
    }

    public static Observable<GeoLocationBean> getLocationByLocation(double latitude, double longitude) {
        if (retrofit == null) {
            synchronized (GeoLocationApi.class){
                if (retrofit == null) {
                    init();
                }
            }
        }
        return retrofit.create(GeoApi.class).getLocationByLocation(latitude + "," + longitude);
    }

    public static Observable<List<Address>> getAddresses(final Context ctx, final double latitude, final double longitude){
        Observable<List<Address>> geoObservable = Observable.create(new Observable.OnSubscribe<List<Address>>() {
            @Override
            public void call(Subscriber<? super List<Address>> subscriber) {
                try {
                    List<Address> fromLocation = new Geocoder(ctx.getApplicationContext()).getFromLocation(latitude, longitude, 100);
                    subscriber.onNext(fromLocation);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
        return geoObservable;
    }

    interface GeoApi{
        @GET("maps/api/geocode/json?sensor=true&language=in")
        Observable<GeoLocationBean> getLocationByLocation(@Query("latlng") String latlng);
    }
}

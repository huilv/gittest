package com.daunkredit.program.sulu.common.network;

import android.util.Pair;

import com.orhanobut.logger.Logger;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by localuser on 2017/3/1.
 */

public class FileUploadUtil {


    public static Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>>
    uploadPhotoFile(FileUploadType type, File file, String token) {
        MediaType mediaType;
        mediaType = MediaType.parse(FileUtils.getMimeType(file));
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(mediaType, file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        //        RequestBody fileType = RequestBody.create(MultipartBody.FORM, FileUploadType.KTP_PHOTO.name().getBytes());

        // finally, execute the request

        final FileUploadApi service = ServiceGenerator.createService(FileUploadApi.class);
        final Call<ResponseBody> call = service.uploadPhoto(body, type.name(), token);

        return Observable.create(new Observable.OnSubscribe<Pair<Call<ResponseBody>, Response<ResponseBody>>>() {
            @Override
            public void call(final Subscriber<? super Pair<Call<ResponseBody>, Response<ResponseBody>>> subscriber) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        Logger.v("Upload", "success");
                        if(response.isSuccessful()){
                            subscriber.onNext(new Pair<>(call, response));
                        }else{
                            subscriber.onError(new HttpException(response));

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Logger.e("Upload error:", t.getMessage());
                        subscriber.onError(new UploadFileException(call, t));

                    }
                });


            }
        });

    }

    public static Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>>
    uploadVideoFile(FileUploadType type, File file, String token, String loanAppId) {

        MediaType mediaType = MediaType.parse(FileUtils.getMimeType(file));
        RequestBody requestFile = RequestBody.create(mediaType, file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        //        RequestBody fileType = RequestBody.create(MultipartBody.FORM, FileUploadType.KTP_PHOTO.name().getBytes());
        // finally, execute the request

        final FileUploadApi service = ServiceGenerator.createService(FileUploadApi.class);
        final Call<ResponseBody> call = service.uploadVideo(body, loanAppId, type.name(), token);

        return Observable.create(new Observable.OnSubscribe<Pair<Call<ResponseBody>, Response<ResponseBody>>>() {
            @Override
            public void call(final Subscriber<? super Pair<Call<ResponseBody>, Response<ResponseBody>>> subscriber) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        Logger.v("Upload video", "success");
                        subscriber.onNext(new Pair<>(call, response));
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Logger.e("Upload video error:", t.getMessage());
                        subscriber.onError(new UploadFileException(call, t));

                    }
                });


            }
        });

    }

    public static Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>>
    uploadVideoFile(FileUploadType type, String path, String token, String loanAppId) {

        File f = new File(path);
        return uploadVideoFile(type, f, token, loanAppId);
    }

    public static Observable<Pair<Call<ResponseBody>, Response<ResponseBody>>>
    uploadPhotoFile(FileUploadType type, String path, String token) {

        File f = new File(path);
        return uploadPhotoFile(type, f, token);
    }


    public static class UploadFileException extends Throwable {
        Call<ResponseBody> call;

        public UploadFileException(Call<ResponseBody> call, Throwable t) {
            super(t);
            this.call = call;
        }

        public Call<ResponseBody> getCall() {
            return call;
        }

    }
}

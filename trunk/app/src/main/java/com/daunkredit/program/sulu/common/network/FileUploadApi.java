package com.daunkredit.program.sulu.common.network;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface FileUploadApi {

    @Multipart
    @PUT("record/files")
    Call<ResponseBody> uploadPhoto(
            @Part MultipartBody.Part photoFile,
            @Query("fileType") String fileType,
            @Header("X-AUTH-TOKEN") String token
    );


    @Multipart
    @PUT("loanapp/contract/video")
    Call<ResponseBody>  uploadVideo(@Part MultipartBody.Part videoFile,
                                          @Query("loanAppId") String loanAppId,
                                          @Query("fileType") String fileType,
                                          @Header("X-AUTH-TOKEN") String token);

    @GET("record/files")
    Observable<RecordFilesResponse> recordFiles(@Header("X-AUTH-TOKEN") String token);




}
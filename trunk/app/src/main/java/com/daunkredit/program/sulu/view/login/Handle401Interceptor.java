package com.daunkredit.program.sulu.view.login;

import com.daunkredit.program.sulu.bean.LoginTokenResponse;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.adapter.rxjava.Result;
import rx.observables.BlockingObservable;

public class Handle401Interceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);
        Logger.d("Response code: "+response.code());
        if(response.code() == 401){
//            response = handle401(chain, request, response);
            if(request.toString().contains("splash")){
                Logger.d("contains splash");
                TokenManager.isExpired = true;
            }else {
                TokenManager.getInstance().refreshTokeExpired();
            }

        }
        return response;
    }


    /**
     *401 token过期时用refreshToken去刷新的token
     *刷新的token时，返回
     * {
     "logRef": "8e0935b9-513b-4541-9111-287b09c40235",
     "exCode": "TOKEN_REFRESH_INVALID",
     "message": "无效的刷新令牌"
     }时，则跳转到登录界面重新登录
     *
     * */
    private static Response handle401(Interceptor.Chain chain, Request request, Response response) {

        try {

            LoginTokenResponse newTokenResponse = getTokenFromNet();

            Request newRequest = request.newBuilder()
                    .addHeader("auth",newTokenResponse.getToken())
                    .build();

            response = chain.proceed(newRequest);

        } catch (Exception e) {
            e.printStackTrace();
            //操作时产生的异常，客户端重试
            response = response.newBuilder().code(500).message(e.getMessage()).build();
        }

        return response;
    }

    /**用refreshToken刷新token*/
    private static LoginTokenResponse getTokenFromNet() throws Exception{

        String refreshToken = TokenManager.getInstance().getRefreshToken();
        String mobile = TokenManager.getInstance().getMobile();

        if( refreshToken == null){
            throw new NullPointerException("refreshToken== null");
        }

        UserApi userApi = ServiceGenerator.createService(UserApi.class);
        BlockingObservable<Result<LoginTokenResponse>> bo = BlockingObservable.from(userApi.refreshToken(mobile, refreshToken));

        Result result = bo.first();

        return handleRefreshTokenResult(result);

    }

    public static LoginTokenResponse handleRefreshTokenResult(Result<LoginTokenResponse> result) throws Exception{

        Response response = result.response().raw();
        Logger.d(response.toString());

        if(response.code() == 200){

            LoginTokenResponse loginResponse = result.response().body();

            TokenManager.getInstance().setToken(loginResponse.getToken(), 0);
            TokenManager.getInstance().setRefreshToken(loginResponse.getRefreshToken(), 0);

            return loginResponse;

        }else if(response.code() == 500){
            //refresh_token 自身过期了，token也要清除，则直接弹出Login界面
            TokenManager.getInstance().refreshTokeExpired();
        }
        throw new NullPointerException("LoginResponse not ok response.code() == 500 or other reason");
    }
}

package com.daunkredit.program.sulu.common.network.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.common.network.CaptchaApi;
import com.daunkredit.program.sulu.bean.LoginRequestBean;
import com.daunkredit.program.sulu.bean.LoginTokenResponse;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TestCaptchaActivity extends AppCompatActivity {

    ImageView imageView;

    LoginRequestBean requestBean;
    String mobile = "15026561387";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_captcha);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCaptcha();
            }
        });


//        refreshCaptcha();


        final UserApi api = ServiceGenerator.createService(UserApi.class);

        requestBean = new LoginRequestBean();

        requestBean.mobile = new LoginRequestBean.Mobile(mobile);
        requestBean.smsCode = new LoginRequestBean.SMSCode("000000");

        api.login(requestBean.smsCode, requestBean.captchaSid, requestBean.captcha, requestBean.mobile,"test")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(TestCaptchaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.d("e:"+e);
                    }

                    @Override
                    public void onNext(LoginTokenResponse response) {
                        Logger.d("token "+response.getToken());
                        Logger.d("refresh_token "+response.getRefreshToken());

                    }
                });


//        api.sendSms(mobile)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseBody>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                        Toast.makeText(TestCaptchaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        Logger.d("e:"+e);
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody o) {
//                        Toast.makeText(TestCaptchaActivity.this, ""+o.toString(), Toast.LENGTH_SHORT).show();
//                        Logger.d("type:"+o.contentType());
//                        Logger.d("len:"+o.contentLength());
//                    }
//                });


    }

    int refreshCount;
    private void refreshCaptcha(){
        refreshCount++;
        Glide
                .with(this)
                .load(CaptchaApi.formatCaptchaUrl(""+refreshCount))
                .signature(new StringSignature(""+System.currentTimeMillis()))
                .into(imageView);
    }
}

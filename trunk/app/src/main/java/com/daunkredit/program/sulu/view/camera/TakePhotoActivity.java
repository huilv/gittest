package com.daunkredit.program.sulu.view.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.network.FileUploadType;
import com.daunkredit.program.sulu.view.camera.presenter.TakePhotoActPreImp;
import com.daunkredit.program.sulu.view.camera.presenter.TakePhotoActPresenter;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.hwangjr.rxbus.RxBus;

public class TakePhotoActivity extends BaseActivity<TakePhotoActPresenter> implements TakePhotoActView{

    RelativeLayout relativeLayout;

    private ImageButton btnCapture = null;
    Button         mbtnCancel;
    CameraView     cameraView;
    FileUploadType mFileUploadType;
    private boolean mIsKTP;

    protected static final int  REQUEST_STORAGE_PERMISSION = 100010;

    @Override
    protected int getLayoutResId() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return  R.layout.activity_take_photo;
    }

    @Override
    protected void init() {
        RxBus.get().register(this);
        if(Build.VERSION.SDK_INT >= 23) {
            int extenalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (extenalStorage != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
        Intent intent = getIntent();
        mIsKTP = intent.getBooleanExtra(FieldParams.PHOTO_TYPE, false);
        mFileUploadType = mIsKTP ? FileUploadType.KTP_PHOTO : FileUploadType.EMPLOYMENT_PHOTO;
        relativeLayout = (RelativeLayout) findViewById(R.id.containerImg);
        cameraView = (CameraView) findViewById(R.id.cameraView);

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                mPresenter.onTaken(picture, mIsKTP);
            }
        });


        mbtnCancel = (Button) findViewById(R.id.button_cancel);
        mbtnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnCapture = (ImageButton) findViewById(R.id.button_shoot);
        btnCapture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= 23){
                    int extenalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(extenalStorage != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION );
                    }else{
                        btnCapture.setClickable(false);
                        showLoading(getString(R.string.loading_storing_img));
                        cameraView.captureImage();
                    }
                }else{

                    btnCapture.setClickable(false);
                    showLoading(getString(R.string.loading_storing_img));
                    cameraView.captureImage();
                }
            }
        });
    }

    @Override
    protected TakePhotoActPresenter initPresenterImpl() {
        return new TakePhotoActPreImp();
    }




    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }
}

package com.daunkredit.program.sulu.view.camera.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.PhotoInfo;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.hwangjr.rxbus.RxBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/6/21 12:14
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TakePhotoActPreImp extends BaseActivityPresenterImpl implements TakePhotoActPresenter {
    public void onTaken(final byte[] data, final boolean mIsKTP) {
        Observable.create(new Observable.OnSubscribe<PhotoInfo>(){
            @Override
            public void call(Subscriber<? super PhotoInfo> subscriber) {
                LoggerWrapper.d("onTaken:start");
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                ByteArrayInputStream byteArrayInputStream = null;
                Bitmap bitmap = null;
                try {
                    byteArrayInputStream = new ByteArrayInputStream(data);
                    SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(BitmapFactory.decodeStream(byteArrayInputStream,null,opts));
                    bitmap = softReference.get();
                }catch (Exception e){
                    LoggerWrapper.d(e);
                    subscriber.onError(e);
                }finally {
                    if (byteArrayInputStream != null) {
                        try {
                            byteArrayInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //容易oom
                //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,opts);
                Bitmap cameraBitmap;
                if (bitmap.getWidth() < bitmap.getHeight()) {
                    cameraBitmap = rotateBitmap(bitmap);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                        System.gc();
                    }
                } else {
                    cameraBitmap = bitmap;
                }

                File myImage = StringFormatUtils.getFileByFileName(mView.getBaseActivity(), mIsKTP ? FieldParams.KTP_IMAGE : FieldParams.WORK_IMAGE);
                if (myImage.exists()) {
                    myImage.delete();
                }

                try {
                    FileOutputStream out = new FileOutputStream(myImage);
                    cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    cameraBitmap.recycle();
                    cameraBitmap = null;
                    System.gc();
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.d("In Saving File", e + "");
                    subscriber.onError(e);
                } catch (IOException e) {
                    Log.d("In Saving File", e + "");
                    subscriber.onError(e);
                }
                PhotoInfo photoinfo = new PhotoInfo();
                photoinfo.isKTP = mIsKTP;
                photoinfo.mFile = myImage;
                LoggerWrapper.d("onTaken:end");
                subscriber.onNext(photoinfo);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<PhotoInfo>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        ToastManager.showToast(mView.getBaseActivity().getString(R.string.show_generate_wrong));
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                        mView.getBaseActivity().finish();
                    }
                    @Override
                    public void onNext(PhotoInfo photoInfo) {
                        RxBus.get().post(photoInfo);
                        if (!isAttached()) {
                            return;
                        }
                        mView.getBaseActivity().dismissLoading();
                        mView.getBaseActivity().finish();
                    }
                });


        //        Intent intent = new Intent();
        //        intent.setAction(Intent.ACTION_VIEW);
        //
        //        intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
        //        startActivity(intent);

        //发送photo消息


    }


    //旋转图片-90度
    @NonNull
    private Bitmap rotateBitmap(Bitmap bitmap) {
        return rotateBitmap2(bitmap, 0 - 90);
    }

    @NonNull
    private Bitmap rotateBitmap(Bitmap bitmap, float degree) {
        LoggerWrapper.d("rotateBitmap:start");
        Bitmap cameraBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(cameraBitmap);
        Matrix matrix = new Matrix();
        matrix.setTranslate(bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth() / 2 - bitmap.getHeight() / 2);
        matrix.postRotate(degree, bitmap.getHeight() / 2, bitmap.getWidth() / 2);

        canvas.drawBitmap(bitmap, matrix, new Paint());
        LoggerWrapper.d("rotateBitmap:end");
        return cameraBitmap;
    }

    private Bitmap rotateBitmap2(Bitmap bitmap,float degree){
        LoggerWrapper.d("rotateBitmap2:start");
        Matrix m = new Matrix();
        m.setRotate(degree,bitmap.getWidth()/2,bitmap.getHeight()/2);
        SoftReference<Bitmap> bitmapSoftReference = new SoftReference<Bitmap>(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true));
        Bitmap bitmap1 = bitmapSoftReference.get();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
        LoggerWrapper.d("rotateBitmap2:end");
        return bitmap1;
    }
}

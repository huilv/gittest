package com.daunkredit.program.sulu.view.certification;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.PhotoInfo;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.camera.TakePhotoActivity;
import com.daunkredit.program.sulu.view.certification.presenter.UploadPhotoActPreImp;
import com.daunkredit.program.sulu.view.certification.presenter.UploadPhotoActPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import org.afinal.simplecache.ACache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import butterknife.BindView;
import butterknife.OnClick;

import static com.daunkredit.program.sulu.R.id.id_button_upload_photo_submit;
import static com.daunkredit.program.sulu.view.certification.presenter.UploadPhotoActPreImp.KTP_TYPE;
import static com.daunkredit.program.sulu.view.certification.presenter.UploadPhotoActPreImp.WORK_TYPE;

/**
 * Created by Miaoke on 2017/2/27.
 */

public class UploadPhotoActivity extends BaseActivity<UploadPhotoActPresenter> implements UploadPhotoActView{

    @BindView(R.id.id_imagebutton_back)
    ImageButton    idImagebuttonBack;
    @BindView(R.id.id_main_top)
    RelativeLayout idMainTop;
    @BindView(R.id.ktp)
    ImageButton    ktp;
    @BindView(R.id.work_card)
    ImageButton    workCard;
    @BindView(id_button_upload_photo_submit)
    Button         idButtonUploadPhotoSubmit;
    @BindView(R.id.id_textview_title)
    TextView       title;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    infoList;

    ACache aCache;

    public static final int    RECORD_FILE_EXPIRE_TIME = 3600;
    public static final String RECORD_FILE_CACHE_KEY   = "record_file_cache_key";

    private Button                              mSubmitButton;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_upload_photo;
    }

    @Override
    protected void init() {
        RxBus.get().register(this);
        infoList.setVisibility(View.INVISIBLE);
        title.setText(getResources().getString(R.string.textview_field_Upload_photos));
        idButtonUploadPhotoSubmit.setEnabled(false);
        idButtonUploadPhotoSubmit.setAlpha(0.3f);


        if (TokenManager.getInstance().hasLogin()) {
          mPresenter.loadImageFromNetOrCache();
        }


        //        RxBus.getSingletonInstance().toObservable(PhotoInfo.class)
        //                .subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Action1<PhotoInfo>() {
        //                    @Override
        //                    public void call(PhotoInfo photoInfo) {
        //                        if (photoInfo.isKTP) {
        //                            changeImage(ktp, photoInfo.mFile);
        //                            mKTPImg = photoInfo.mFile;
        //                            mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.FILE_ADDED);
        //                            setButtonClickableState();
        //                        } else {
        //                            changeImage(workCard, photoInfo.mFile);
        //                            mWorkCardImg = photoInfo.mFile;
        //                            mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.FILE_ADDED);
        //                            setButtonClickableState();
        //                        }
        //                    }
        //                });

    }

    @Override
    protected UploadPhotoActPresenter initPresenterImpl() {
        return new UploadPhotoActPreImp();
    }


    //接受bus发送的photo信息
    @Subscribe
    public void onPhotoTaken(PhotoInfo photoInfo) {
        if (photoInfo.isKTP) {
            changeImage(KTP_TYPE, photoInfo.mFile);
            mPresenter.onPhotoTaken(photoInfo);
            setButtonClickableState(mPresenter.updateClickableState());
        } else {
            changeImage(WORK_TYPE, photoInfo.mFile);
            mPresenter.onPhotoTaken(photoInfo);
            setButtonClickableState(mPresenter.updateClickableState());
        }
    }

    //将拍摄好的图片在界面上显示出来
    @Override
    public void changeImage(int ktp, File file) {
        Bitmap showImg = injustImg(ktp, file);
        setImage(ktp, showImg);
    }

    public void setImage(int ktp, Bitmap showImg) {
        if (ktp == 0) {
            this.ktp.setImageBitmap(showImg);
        }else{
            this.workCard.setImageBitmap(showImg);
        }
    }


    //图片压缩调整
    @Override
    public Bitmap injustImg(int ktp, File file) {
        LoggerWrapper.d("injustImg:started");
        int width ;
        int height ;
        if (ktp == 0) {
            width = this.ktp.getWidth();
            height = this.ktp.getHeight();
        }else{
            width = this.workCard.getWidth();
            height = this.workCard.getHeight();
        }
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        ops.inPurgeable = true;
        ops.inInputShareable = true;
        ops.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap showImg = BitmapFactory.decodeFile(file.getPath(), ops);
        ops.inJustDecodeBounds = false;
        ops.inPreferredConfig = Bitmap.Config.RGB_565;
//        ops.inScaled = true;
//        ops.inDensity = DENSITY_XHIGH;
        int wScale = ops.outWidth / width;
        int hScale = ops.outHeight / height;
        ops.inSampleSize = wScale > hScale ? wScale : hScale;
        InputStream is = null;

        try {
            is = new FileInputStream(file);
            SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(BitmapFactory.decodeStream(is, new Rect(0, 0, ops.outWidth / ops.inSampleSize, ops.outHeight / ops.inSampleSize), ops));
            showImg = softBitmap.get();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LoggerWrapper.d("injustImg:end");
        return showImg;
    }



    @OnClick({R.id.ktp, R.id.work_card, id_button_upload_photo_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ktp:
                UserEventQueue.add(new ClickEvent(ktp.toString(), ActionType.CLICK, "Upload KTP Button"));
                takePhotoKTP();
                break;
            case R.id.work_card:
                UserEventQueue.add(new ClickEvent(ktp.toString(), ActionType.CLICK, "Upload Work Card Button"));
                takePhotoWorkCard();
                break;
            case R.id.id_button_upload_photo_submit:
                UserEventQueue.add(new ClickEvent(idButtonUploadPhotoSubmit.toString(), ActionType.CLICK, "Photo Upload Button"));
                mPresenter.uploadImages();
                break;
        }
    }

    //    private void updateSubmitButtonState(){
    //        if((mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.FILE_ADDED  || mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.DOWNLOADED ) &&
    //        (mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.FILE_ADDED  || mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.DOWNLOADED )){
    //            idButtonUploadPhotoSubmit.setClickable(true);
    //            idButtonUploadPhotoSubmit.setAlpha(0.8f);
    //        }else{
    //            idButtonUploadPhotoSubmit.setClickable(false);
    //            idButtonUploadPhotoSubmit.setAlpha(0.3f);
    //        }
    //    }



    private void takePhotoWorkCard() {
        View KTPAlertView = View.inflate(this, R.layout.view_ktp_alert, null);
        ImageView image = (ImageView) KTPAlertView.findViewById(R.id.iv_ktp_alert);
        image.setImageResource(R.drawable.alertktp);
        TextView text = (TextView) KTPAlertView.findViewById(R.id.tv_ktp_alert);
        text.setText(getResources().getText(R.string.text_shoot_work_card_front));
        final Dialog dialog = DialogManager.newDialog(this, KTPAlertView, false);
        KTPAlertView.findViewById(R.id.btn_ktp_shoot_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                changeTo(TakePhotoActivity.class, false);
            }
        });
    }


    private void takePhotoKTP() {
        View KTPAlertView = View.inflate(this, R.layout.view_ktp_alert, null);

        final Dialog dialog = DialogManager.newDialog(this, KTPAlertView, false);
        KTPAlertView.findViewById(R.id.btn_ktp_shoot_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                changeTo(TakePhotoActivity.class, true);
            }
        });

    }

    @Override
    public ImageView getViewByType(int viewType) {
        return viewType == 0 ? ktp : workCard;
    }

    public void setButtonClickableState(boolean state) {
        if (!idButtonUploadPhotoSubmit.isEnabled() && state) {
            idButtonUploadPhotoSubmit.setEnabled(true);
            idButtonUploadPhotoSubmit.setAlpha(1.0f);
        }
    }

    @Override
    public int getViewHeightByType(int viewType) {
        return viewType == 0 ? ktp.getHeight() : workCard.getHeight();
    }

    @Override
    public int getViewWidthByType(int viewType) {
        return viewType == 0 ? ktp.getWidth() : workCard.getWidth();
    }

    public void changeTo(Class clazz, boolean isKTP) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(FieldParams.PHOTO_TYPE, isKTP);
        startActivity(intent);
    }

    @OnClick(R.id.id_imagebutton_back)
    public void back() {
        UserEventQueue.add(new ClickEvent(findViewById(R.id.id_imagebutton_back).toString(), ActionType.CLICK, "Back"));
        finish();
    }

    @Override
    protected void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
        //重新打开时会出错 try to use recycled bitmap
//        if (ktp != null && ktp.getDrawable() != null) {
//            Bitmap bitmap = ((BitmapDrawable) ktp.getDrawable()).getBitmap();
//            ktp.setImageDrawable(null);
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
//
//        if (workCard != null && workCard.getDrawable() != null) {
//            Bitmap bitmap = ((BitmapDrawable) workCard.getDrawable()).getBitmap();
//            workCard.setImageDrawable(null);
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
    }
}

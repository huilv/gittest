package com.daunkredit.program.sulu.view.certification.presenter;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.util.Pair;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.daunkredit.program.sulu.bean.PhotoInfo;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.FileUploadApi;
import com.daunkredit.program.sulu.common.network.FileUploadType;
import com.daunkredit.program.sulu.common.network.FileUploadUtil;
import com.daunkredit.program.sulu.common.network.RecordFilesResponse;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.view.certification.UploadPhotoActView;
import com.daunkredit.program.sulu.view.certification.status.FileStatus;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.google.gson.Gson;

import org.afinal.simplecache.ACache;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.alibaba.mobileim.YWChannel.getResources;
import static com.daunkredit.program.sulu.view.certification.UploadPhotoActivity.RECORD_FILE_CACHE_KEY;
import static com.daunkredit.program.sulu.view.certification.UploadPhotoActivity.RECORD_FILE_EXPIRE_TIME;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class UploadPhotoActPreImp extends BaseActivityPresenterImpl implements UploadPhotoActPresenter {
    UploadPhotoActView view;
    ACache             aCache;
    @IntDef({KTP_TYPE,WORK_TYPE})
    public @interface PhotoType{}
    public static final int KTP_TYPE = 0;
    public static final int WORK_TYPE = 1;

    private File                                mKTPImg;
    private File                                mWorkCardImg;


    private HashMap<FileUploadType, FileStatus> mFileStatus;

    public UploadPhotoActPreImp(){
        mFileStatus = new HashMap<>(2);
        mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.INIT);
        mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.INIT);
    }

    @Override
    public void disAttach() {
        super.disAttach();
        mFileStatus = null;
        mKTPImg = null;
        mWorkCardImg = null;
        view = null;
    }

    @Override
    public void loadImageFromNetOrCache() {
        view = (UploadPhotoActView) mView;
        aCache = ACache.get(mView.getBaseActivity());
        //本地图片地址，直接使用不安全
        //            mKTPImg = StringFormatUtils.getFileByFileName(this, FieldParams.KTP_IMAGE);
        //mWorkCardImg = StringFormatUtils.getFileByFileName(this, FieldParams.WORK_IMAGE);
        //            if (mKTPImg.exists() && mWorkCardImg.exists() && mKTPImg.length() > 0 && mWorkCardImg.length() >0) {
        //                mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.DOWNLOADED);
        //                mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.DOWNLOADED);
        //                ktp.post(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        setImage(ktp, injustImg(ktp,mKTPImg));
        //                    }
        //                });
        //               workCard.post(new Runnable() {
        //                   @Override
        //                   public void run() {
        //                       setImage(workCard, injustImg(workCard,mWorkCardImg));
        //                   }
        //               });
        //            }else{
        loadAndSetPhoto(TokenManager.getInstance().getToken());
        //            }
    }

    /**
     * 如果本地缓存不存在，则请求网络的，如果存在则用本地的
     */
    private void loadAndSetPhoto(String token) {

        cacheRecord(token).switchIfEmpty(netRecord())
                .compose(this.<RecordFilesResponse>applySchedulers())
                .subscribe(new Subscriber<RecordFilesResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //                        StringBuffer sb = new StringBuffer();
                        //                        StackTraceElement[] stackTrace = e.getStackTrace();
                        //                        for (int i = 0; i < stackTrace.length; i++) {
                        //                            sb.append(stackTrace[i].toString() + "\n");
                        //                        }
                        //                        XLeoToast.showMessage(sb.toString());
                        XLeoToast.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(RecordFilesResponse response) {

                        if (!isAttached()) {
                            return;
                        }

                        //Logger.d(response.getFiles());
                        List<RecordFilesResponse.FilesBean> beanList = response.getFiles();
                        if (beanList == null) {
                            return;
                        }
                        for (int i = 0; i < beanList.size(); i++) {

                            if (beanList.get(i).getFileType().equals(FileUploadType.KTP_PHOTO.name()) && mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.INIT) {
                                setImgsNet(KTP_TYPE, beanList.get(i).getUrl());

                            } else if (beanList.get(i).getFileType().equals(FileUploadType.EMPLOYMENT_PHOTO.name()) && mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.INIT) {
                                setImgsNet(WORK_TYPE, beanList.get(i).getUrl());
                            }

                        }
                    }
                });
    }

    Observable<RecordFilesResponse> cacheRecord(final String token) {
        return Observable.create(new Observable.OnSubscribe<RecordFilesResponse>() {
            @Override
            public void call(Subscriber<? super RecordFilesResponse> subscriber) {
                String cacheStr = aCache.getAsString(RECORD_FILE_CACHE_KEY + token);

                if (cacheStr == null || cacheStr.length() == 0) {
                    subscriber.onCompleted();
                } else {
                    Gson gson = new Gson();

                    RecordFilesResponse response = gson.fromJson(cacheStr, RecordFilesResponse.class);
                    if (response == null) {
                        subscriber.onCompleted();
                    } else {
                        List<RecordFilesResponse.FilesBean> beanList = response.getFiles();
                        if (beanList == null || beanList.size() < 2) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onNext(response);
                        }
                    }
                }

            }
        });
    }

    Observable<RecordFilesResponse> netRecord() {
        final long startTime = System.currentTimeMillis();
        FileUploadApi api = ServiceGenerator.createService(FileUploadApi.class);

        final String token = TokenManager.getInstance().getToken();
        Observable<RecordFilesResponse> netRecord = api.recordFiles(token)
                .doOnNext(new Action1<RecordFilesResponse>() {
                    @Override
                    public void call(RecordFilesResponse response) {
                        Gson gson = new Gson();
                        String str = gson.toJson(response, RecordFilesResponse.class);

                        long endTime = System.currentTimeMillis();

                        long dualTime = endTime - startTime;

                        long stillValidLife = (RECORD_FILE_EXPIRE_TIME * 1000 - dualTime) / 1000;

                        aCache.put(RECORD_FILE_CACHE_KEY + token, str, (int) stillValidLife);
                    }
                });

        return netRecord;
    }

    public boolean updateClickableState(){
        return (mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.DOWNLOADED || mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.FILE_ADDED)
                && (mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.FILE_ADDED || mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.DOWNLOADED);
    }

    @Override
    public void onPhotoTaken(PhotoInfo photoInfo) {
        if (photoInfo.isKTP) {
            mKTPImg = photoInfo.mFile;
            mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.FILE_ADDED);
        }else{
            mWorkCardImg = photoInfo.mFile;
            mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.FILE_ADDED);
        }
    }

    private void setImgsNet(int viewType, String url) {
        //        Logger.d("url "+url);
        if (viewType >= 2 || url == null) {
            return;
        }
        obtainImageFileNet(viewType, url);
        //glideWay(viewType, url);
    }

    private void glideWay(int viewType, String url) {
        Glide.with(mView.getBaseActivity())
                .load(url)
                .placeholder(R.drawable.ic_logo)
                .fallback(R.drawable.ic_photograph_x)
                .error(R.drawable.ic_photograph_x)
                .into(view.getViewByType(viewType));
        mFileStatus.put(viewType == KTP_TYPE ? FileUploadType.KTP_PHOTO : FileUploadType.EMPLOYMENT_PHOTO, FileStatus.DOWNLOADED);
        view.setButtonClickableState(updateClickableState());
    }

    //由glide获取图片文件，设置图片
    private void obtainImageFileNet(final int viewType, final String url) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                RequestManager requestManager = Glide.with(mView.getBaseActivity());
                DrawableTypeRequest<String> load = requestManager.load(url);
                FutureTarget<File> fileFutureTarget = load.downloadOnly(view.getViewWidthByType(viewType), view.getViewHeightByType(viewType));
                File file = null;
                try {
                    file = fileFutureTarget.get(5, TimeUnit.MINUTES);
                } catch (Exception e) {
                    try {
                        file = fileFutureTarget.get(5, TimeUnit.MINUTES);
                    } catch (Exception e1) {
                        try {
                            file = fileFutureTarget.get(5, TimeUnit.MINUTES);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            subscriber.onError(e2);
                        }
                    }
                }
                if (file == null) {
                    subscriber.onError(new RuntimeException("obtain file failed"));
                } else {
                    subscriber.onNext(file);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //XLeoToast.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(File file) {
                        if (viewType == KTP_TYPE && mFileStatus.get(FileUploadType.KTP_PHOTO) != FileStatus.FILE_ADDED) {
                            mKTPImg = file;
                            mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.DOWNLOADED);
                            view.setButtonClickableState(updateClickableState());
                            //                            XLeoToast.showMessage("load ktp");
                        } else if (viewType == WORK_TYPE && mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) != FileStatus.FILE_ADDED) {
                            mWorkCardImg = file;
                            mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.DOWNLOADED);
                            view.setButtonClickableState(updateClickableState());
                            //                             XLeoToast.showMessage("load workcard");
                        }
                        view.changeImage(viewType, file);
                    }
                });
    }



    public void uploadImages() {

        mView.getBaseActivity().showLoading(getResources().getText(R.string.show_uploading).toString());
        if (mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.FILE_ADDED || mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.UPLOAD_FAILED) {
            mFileStatus.put(FileUploadType.KTP_PHOTO, FileStatus.UPLOADING);
            upload(mKTPImg, FileUploadType.KTP_PHOTO);
        }
        if (mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.FILE_ADDED || mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.UPLOAD_FAILED) {
            mFileStatus.put(FileUploadType.EMPLOYMENT_PHOTO, FileStatus.UPLOADING);
            upload(mWorkCardImg, FileUploadType.EMPLOYMENT_PHOTO);
        }

        if (mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.DOWNLOADED && mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.DOWNLOADED) {
            mView.getBaseActivity().dismissLoading();
            mView.getBaseActivity().finish();
        }
    }

    private void upload(File myImage, final FileUploadType fileUploadType) {

        final String token = TokenManager.getInstance().getToken();
        FileUploadUtil.uploadPhotoFile(
                fileUploadType,
                myImage,
                token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Call<ResponseBody>, Response<ResponseBody>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!isAttached()) {
                            return;
                        }
                        mFileStatus.put(fileUploadType, FileStatus.UPLOAD_FAILED);
                        if (mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) != FileStatus.UPLOADING &&
                                mFileStatus.get(FileUploadType.KTP_PHOTO) != FileStatus.UPLOADING) {
                            mView.getBaseActivity().dismissLoading();
                            XLeoToast.showMessage(getResources().getText(R.string.show_upload_failed).toString());
                        }
                    }

                    @Override
                    public void onNext(Pair<Call<ResponseBody>, Response<ResponseBody>> callResponsePair) {
                        if (!isAttached()) {
                            return;
                        }
                        mFileStatus.put(fileUploadType, FileStatus.UPLOAD_SUCCESS);
                        aCache.remove(RECORD_FILE_CACHE_KEY);
                        if ((mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.DOWNLOADED || mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) == FileStatus.UPLOAD_SUCCESS)
                                && (mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.UPLOAD_SUCCESS || mFileStatus.get(FileUploadType.KTP_PHOTO) == FileStatus.DOWNLOADED)) {
                            XLeoToast.showMessage(getResources().getText(R.string.show_upload_sucess).toString());
                            mView.getBaseActivity().dismissLoading();
                            aCache.remove(RECORD_FILE_CACHE_KEY + token);
                            mView.getBaseActivity().setResult(Activity.RESULT_OK);
                            mView.getBaseActivity().finish();
                        } else if (
                                mFileStatus.get(FileUploadType.EMPLOYMENT_PHOTO) != FileStatus.UPLOADING &&
                                        mFileStatus.get(FileUploadType.KTP_PHOTO) != FileStatus.UPLOADING
                                ) {
                            mView.getBaseActivity().dismissLoading();
                            XLeoToast.showMessage(getResources().getText(R.string.show_upload_failed).toString());
                        }
                    }
                });
    }
}

package com.daunkredit.program.sulu.common.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.bean.VersionBean;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者:My
 * @创建日期: 2017/4/26 16:24
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class UpdateManager {
    private final VersionBean mVersonBean;
    private final Context mContext;

    public UpdateManager(Context context, VersionBean versonBean) {
        mContext = context;
        mVersonBean = versonBean;
    }

    public void start() {
        createUpdateDialog(mVersonBean);
    }

    private void createUpdateDialog(VersionBean versionBean) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_update, null);
        Dialog downloadDialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .setCancelable(false)
                .create();
        initDownloadView(view, downloadDialog, versionBean);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setCancelable(false);
        downloadDialog.show();
    }

    private void initDownloadView(View view, final Dialog downloadDialog, final VersionBean versionBean) {
        ImageView title = (ImageView) view.findViewById(R.id.tv_dialog_update_title);
        TextView message = (TextView) view.findViewById(R.id.tv_dialog_update_message);
        final LinearLayout linearlayout = (LinearLayout) view.findViewById(R.id.ll_dialog_update_message);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb_dialog_update);
        StringBuffer s = new StringBuffer();
        for (String s1 : versionBean.getReleaseNotes()) {
            s.append(s1 + "\n");
        }
        message.setText(s);
        view.findViewById(R.id.btn_dialog_update_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                linearlayout.setVisibility(View.GONE);
                downloadAndInstall(versionBean, downloadDialog, pb);
            }
        });
    }


    private void downloadAndInstall(final VersionBean versionBean, final Dialog downloadDialog, final ProgressBar pb) {
        Observable.create(new Observable.OnSubscribe<File>() {

            @Override
            public void call(final Subscriber<? super File> subscriber) {
                new DownloadManager(mContext, versionBean.getUrl(), new DownloadManager.OnDownloadListener() {
                    @Override
                    public void onProgress(int progress) {
                        if (pb != null) {
                            pb.setProgress(progress);
                        }
                    }

                    @Override
                    public void onComplete(File file) {
                        downloadDialog.dismiss();
                        subscriber.onNext(file);
                    }

                    @Override
                    public void onError(Throwable t) {
                        subscriber.onError(t);
                    }
                }).startDownLoad();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.d(e);
                        XLeoToast.showMessage(mContext.getString(R.string.show_update_on_error) + e.getMessage());
                        android.os.Process.killProcess(android.os.Process.myPid());
                        downloadDialog.dismiss();
                    }

                    @Override
                    public void onNext(File file) {
                        //                        if (versionBean.getApkSize() != file.length()) {
                        //                            XLeoToast.showMessage(R.string.show_download_on_google_player);
                        //                        }else {
                        Intent installIntent = new Intent();
                        installIntent.setAction(Intent.ACTION_VIEW);
                        installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(installIntent);
                    }
                    //                    }
                });
    }

}

package com.daunkredit.program.sulu.common.update;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @作者:My
 * @创建日期: 2017/4/25 13:37
 * @描述:下载apk的类
 * @更新者:
 * @更新时间:
 * @更新描述:${TODO}
 */

public class DownloadManager {
    private final String mUrlPath;
    private final Context mContext;
    private OnDownloadListener mOnDownloadListener;

    public DownloadManager(Context context,String url,OnDownloadListener l){
        if (l == null || TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url and l can't be null");
        }
        mOnDownloadListener = l;
        mUrlPath = url;
        mContext = context;
    }

    public void startDownLoad(){
        downloadNormal(mContext,mUrlPath);
    }

    public void downloadNormal(Context context, String url) {
        File result;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            result = new File(Environment.getExternalStorageDirectory(), "Sulu.apk");

        } else {
            result = new File(context.getFilesDir(), "Sulu.apk");
        }
        if (!result.exists()) {
            File directory = new File(result.getAbsolutePath().replace("/Sulu.apk", ""));
            directory.mkdirs();
        }

        startDownload(url, result, 0);
    }

    private void startDownload(String path, File result, long startPosition) {
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            InputStream inputStream = urlConnection.getInputStream();
            inputStream.skip(startPosition);
            long contentLengthLong = urlConnection.getContentLength();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200 && contentLengthLong > 0) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(result));
                byte[] buffer = new byte[8 * 1024];
                int len = 0;
                long currentDown = 0;
                while((len = inputStream.read(buffer)) > 0){
                    bos.write(buffer,0,len);
                    currentDown += len;
                    if (mOnDownloadListener != null) {
                        mOnDownloadListener.onProgress((int) (currentDown * 100 / contentLengthLong + 0.5f));
                    }
                }
                bos.close();
                inputStream.close();
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onComplete(result);
                }
            }else{
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onError(new IllegalAccessException("refused"));
                }
            }

        }catch (Exception e){
            if (mOnDownloadListener != null) {
                mOnDownloadListener.onError(new IllegalAccessException("refused"));
            }
        }
    }

    public void setOnDownloadListener(OnDownloadListener l) {
        mOnDownloadListener = l;
    }

    public interface OnDownloadListener {
        void onProgress(int progress);

        void onComplete(File file);

        void onError(Throwable t);
    }
}

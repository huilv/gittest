package com.daunkredit.program.sulu.view.certification;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.daunkredit.program.sulu.app.base.BaseActivityView;

import java.io.File;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:38
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface UploadPhotoActView extends BaseActivityView {
    ImageView getViewByType(int viewType);

    void setButtonClickableState(boolean state);

    int getViewHeightByType(int viewType);

    int getViewWidthByType(int viewType);

    Bitmap injustImg(int viewType, File file);

    void setImage(int viewType, Bitmap bitmap);

    void changeImage(int ktpType, File file);
}

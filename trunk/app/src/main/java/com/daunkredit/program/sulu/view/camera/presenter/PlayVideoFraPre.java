package com.daunkredit.program.sulu.view.camera.presenter;

import android.widget.Button;

import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter;

/**
 * @作者:My
 * @创建日期: 2017/6/21 11:37
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface PlayVideoFraPre extends BaseFragmentPresenter {
    void uploadVideo(Button idButtonVideoUpload, String videoPath);
}

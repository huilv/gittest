package com.daunkredit.program.sulu.view.camera;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.enums.CameraState;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.camera.presenter.TakeVideoFraPreImp;
import com.daunkredit.program.sulu.view.camera.presenter.TakeVideoFraPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.daunkredit.program.sulu.widget.xleovideoview.SelfDefVideoView;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.utils.SpanBuilder;
import com.x.leo.circles.CircleProgressButton;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Created by Miaoke on 2017/3/16.
 */

public class TakeVideoFragment extends BaseFragment<TakeVideoFraPresenter> implements TakeVideoFraView {

    @BindView(R.id.id_cameraview_video)
    CameraView idCameraviewVideo;
    @BindView(R.id.id_textview_video_toneContent)
    TextView idTextviewVideoToneContent;
    @BindView(R.id.id_textview_timer_tips)
    TextView idTextviewTimerTips;
    @BindView(R.id.id_imageview_outline)
    ImageView idImageviewOutline;
    @BindView(R.id.id_textview_video_statement)
    TextView idTextviewVideoStatement;
    @BindView(R.id.id_button_video_re_record)
    Button idButtonVideoReRecord;
    @BindView(R.id.id_button_video_complete_preview)
    Button idButtonVideoCompletePreview;
    @BindView(R.id.id_linearlayout_video_button)
    LinearLayout idLinearlayoutVideoButton;
    @BindView(R.id.id_button_start_pause_continue)
    Button idButtonStartPauseContinue;
    @BindView(R.id.id_button_start_pause_continue_three)
    Button btnThree;
    @BindView(R.id.cpb_video_taken)
    CircleProgressButton cpbVideoTaken;
    @BindView(R.id.cpb_video_play)
    CircleProgressButton cpbVideoPlay;
    @BindView(R.id.cpb_submit)
    CircleProgressButton cpbSubmit;
    @BindView(R.id.sdvv_video)
    SelfDefVideoView mVideoView;
    @BindView(R.id.iv_play_video_thumb)
    ImageView idImageviewVideo;
    private boolean isWaitToStart = false;
    private boolean isWaitCloseToStart = false;

    public enum VideoStatus {
        STOP,
        PAUSE,
        RECORDING
    }

    CameraState mCameraState = CameraState.CLOSED;
    VideoStatus videoStatus = VideoStatus.STOP;
    String videoPath;

    @Override
    protected TakeVideoFraPresenter initPresenter() {
        return new TakeVideoFraPreImp();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_taking;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        if (Build.VERSION.SDK_INT >= 23)
            checkPermission();
        showTipsDialog();
        initLegalText();
        initCpButton();

        idCameraviewVideo.setCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                videoPath = video.getAbsolutePath();
                mActivity.dismissLoading();
                Logger.d("Video Path:%s Size: %s ", video.getAbsolutePath(), video.getAbsoluteFile().length());
                resetButton();
                changeToPreview();
                showVideoController();
            }

            @Override
            public void onCameraError(final String message, final Exception e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.dismissLoading();
                        XLeoToast.showMessage(message + e.getLocalizedMessage());
                        resetButton();
                    }
                });
            }

            @Override
            public void onCameraOpened() {
                mCameraState = CameraState.OPENED;
                if (isWaitToStart) {
                    idCameraviewVideo.startRecordingVideo();
                    isWaitToStart = false;
                }
            }

            @Override
            public void onCameraClosed() {
                mCameraState = CameraState.CLOSED;
                if (isWaitCloseToStart) {
                    idCameraviewVideo.start();
                    isWaitCloseToStart = false;
                }
            }

            @Override
            public void onRecordStart() {
                cpbVideoTaken.startProgressAnimation();
            }

            @Override
            public void onRecordPause() {
                super.onRecordPause();
            }
        });


    }

    private void changeToPreview() {
        if (mVideoView.getVisibility() != View.VISIBLE) {
            if (mCameraState == CameraState.OPENED) {
//                idCameraviewVideo.stop();
//                mCameraState = CameraState.CLOSING;
            }
            mVideoView.setVisibility(View.VISIBLE);
            takeThumbImg();
        }
    }

    private void takeThumbImg() {
        mActivity.showLoading(getString(R.string.show_preparing));
        new Thread(new Runnable() {
            @Override
            public void run() {
                createVideoThumbnail(videoPath);

            }
        }).start();
    }

    private void createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.d("file not exits");
                throw new IllegalArgumentException();
            }
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0);
            final Bitmap bt = bitmap;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bt != null) {
                        idImageviewVideo.setVisibility(View.VISIBLE);
                        idImageviewVideo.setBackground(new BitmapDrawable(mActivity.getResources(), bt));
                    }
                }
            });
            Logger.d(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        } catch (IllegalArgumentException ex) {
            Logger.d("Illegal argument exception: " + ex.getMessage());
        } catch (RuntimeException ex) {
            Logger.d("Runtime Exception " + ex.getMessage());
        } finally {
            mActivity.dismissLoading();
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
    }

    private void resetButton() {
        cpbVideoTaken.setClickable(true);
        cpbVideoTaken.setAlpha(1.0f);
        cpbVideoTaken.resetProgress();
    }

    private void initCpButton() {
        cpbVideoTaken.setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                stopVideoRecord();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        idCameraviewVideo.setCameraListener(null);
        cpbVideoTaken.cancelAnimation();
        cpbVideoPlay.cancelAnimation();
        cpbSubmit.cancelAnimation();
        super.onDestroyView();
    }

    private void showVideoController() {
        cpbSubmit.setVisibility(View.VISIBLE);
        cpbVideoPlay.setVisibility(View.VISIBLE);
    }

    private void hideVideoController() {
        cpbSubmit.setVisibility(View.GONE);
        cpbVideoPlay.setVisibility(View.GONE);
        idImageviewVideo.setVisibility(View.GONE);
    }

    @OnClick(R.id.cpb_submit)
    public void onSubmitClicked() {
        mPresenter.submitVideo(videoPath);
        cpbSubmit.startProgressAnimation();
    }

    @Override
    public void onVideoSubmitted() {
        mActivity.finish();
    }

    @OnClick(R.id.cpb_video_play)
    public void onVideoPlay() {
        changeToPreview();
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
            cpbVideoPlay.resetProgress();
        }
        mVideoView.setVideoPath(videoPath);

        cpbVideoPlay.setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cpbVideoPlay.resetProgress();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mVideoView.start();
        cpbVideoPlay.startProgressAnimation();
        idImageviewVideo.setVisibility(View.GONE);
    }

    private void initLegalText() {
        String statement = getString(R.string.textview_video_statement);
        String agree = getString(R.string.text_loan_agreement_title);
        SpannableStringBuilder result = SpanBuilder.INSTANCE.init(statement)
                .setLinkSpan(statement.indexOf(agree), statement.indexOf(agree) + agree.length(), R.color.color_link_color, new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        String statement_file = "needcopy/loan_agreement.html";
                        DialogManager.showHtmlDialogWithCheck(statement_file, getContext(), getString(R.string.title_loan_agreement_title));
                        return null;
                    }
                })
                .result();
        idTextviewVideoStatement.setText(result);
        idTextviewVideoStatement.setMovementMethod(new LinkMovementMethod());
    }

    private void showTipsDialog() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_video_take_tips, null);
        final Dialog dialog = DialogManager.newDialog(mActivity, view, false);
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= 23)
                    checkPermission();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (mActivity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA},100121);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
        }
    }


    @Override
    protected void initData() {
        idTextviewVideoToneContent.setText(getString(R.string.textview_video_toneContent));
    }


    @OnClick(R.id.cpb_video_taken)
    public void onCircleButtonClicked() {
        UserEventQueue.add(new ClickEvent(cpbVideoTaken.toString(), ActionType.CLICK, cpbVideoTaken.getText().toString()));
        cpbVideoTaken.setClickable(false);
        cpbVideoTaken.setAlpha(0.3f);
        hideVideoController();
        if (mVideoView.getVisibility() == View.VISIBLE) {
            if (mVideoView.isPlaying()) {
                mVideoView.stopPlayback();
            }
            mVideoView.setVisibility(View.GONE);
            if (mCameraState == CameraState.CLOSED) {
                idCameraviewVideo.start();
                mCameraState = CameraState.OPENING;
            } else {
                isWaitCloseToStart = true;
            }
        }
        if (mCameraState != CameraState.OPENED) {
            isWaitToStart = true;
            return;
        }
        idCameraviewVideo.startRecordingVideo();
    }

    private void stopVideoRecord() {
        if (mActivity != null) {
            mActivity.showLoading(null);
        }

        idCameraviewVideo.stopRecordingVideo();
        videoStatus = VideoStatus.STOP;
    }

    @Override
    public void onResume() {
        super.onResume();
        idCameraviewVideo.start();
    }

    @Override
    public void onPause() {
        idCameraviewVideo.stop();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }
}

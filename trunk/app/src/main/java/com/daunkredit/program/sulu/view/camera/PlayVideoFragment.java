package com.daunkredit.program.sulu.view.camera;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.common.StringUtil;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.camera.presenter.PlayVideoFraPre;
import com.daunkredit.program.sulu.view.camera.presenter.PlayVideoFraPreImpl;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Miaoke on 2017/3/16.
 */

public class PlayVideoFragment extends BaseFragment<PlayVideoFraPre> implements PlayVideoView {
    @BindView(R.id.id_videoview)
    VideoView idVideoview;
    @BindView(R.id.id_imageview_start_play)
    ImageView idImageviewStartPlay;
    @BindView(R.id.id_textview_video_statement)
    TextView idTextviewVideoStatement;
    @BindView(R.id.id_button_video_re_record)
    Button idButtonVideoReRecord;
    @BindView(R.id.id_button_video_upload)
    Button idButtonVideoUpload;
    @BindView(R.id.id_linearlayout_video_button)
    LinearLayout idLinearlayoutVideoButton;
    @BindView(R.id.id_framelayout_re_preview)
    FrameLayout idFramelayoutRePreview;
    @BindView(R.id.id_imageview_video)
    ImageView idImageviewVideo;



    private String videoPath;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_player;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        videoPath = (String) TokenManager.removeMessage(FieldParams.VIDEOPATH);
        setVideoBg();
        idVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                idImageviewStartPlay.setVisibility(View.VISIBLE);
            }
        });
        initStatement();
    }

    private void initStatement() {
        String statement = getString(R.string.textview_video_statement);
        String agree = getString(R.string.text_loan_agreement_title);
        SpannableStringBuilder ssb = new SpannableStringBuilder(statement);
        final String finalAgree = agree;
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                DialogManager.showStatementDialog(getContext(),finalAgree.replace("《","").replace("》",""),getResources().getStringArray(R.array.loan_agreement));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setDither(false);
            }
        },statement.indexOf(agree),statement.indexOf(agree) + agree.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        idTextviewVideoStatement.setText(ssb);
        idTextviewVideoStatement.setMovementMethod(new LinkMovementMethod());
    }

    @Override
    public void initData() {
        if (videoPath == null) {
            return;
        }
        idVideoview.setVideoPath(videoPath);
    }

    @Override
    protected PlayVideoFraPre initPresenter() {
        return new PlayVideoFraPreImpl();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            videoPath = (String) TokenManager.removeMessage(FieldParams.VIDEOPATH);
            if (videoPath == null) {
               return;
            }
            idVideoview.setVideoPath(videoPath);
            setVideoBg();
            if (idImageviewStartPlay.getVisibility() != View.VISIBLE) {
                idImageviewStartPlay.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setVideoBg(){
        mActivity.showLoading(getString(R.string.show_preparing));
        new Thread(new Runnable() {
            @Override
            public void run() {
               createVideoThumbnail(videoPath);

            }
        }).start();
    }

    @OnClick(R.id.id_button_video_re_record)
    public void reRecord() {
        UserEventQueue.add(new ClickEvent(idButtonVideoReRecord.toString(), ActionType.CLICK, idButtonVideoReRecord.getText().toString()));
        if(idImageviewVideo.getVisibility() != View.VISIBLE){
            idImageviewVideo.setVisibility(View.VISIBLE);
        }
        idVideoview.stopPlayback();
        if (mActivity instanceof TakeVideoActivity) {
            ((TakeVideoActivity)mActivity).toTakeVideo();
        }
    }


    @OnClick(R.id.rl_video_play)
    public void playVideoRecord() {
        UserEventQueue.add(new ClickEvent(mActivity.findViewById(R.id.rl_video_play).toString(), ActionType.CLICK, "Play Video"));
        if(idImageviewVideo.getVisibility() != View.GONE){
            idImageviewVideo.setVisibility(View.GONE);
        }
        if (StringUtil.isNullOrEmpty(videoPath)) {
            ToastManager.showToast(getString(R.string.show_record_failed), Toast.LENGTH_SHORT);
        } else if (idVideoview.isPlaying()) {
            idVideoview.pause();
            idImageviewStartPlay.setVisibility(View.VISIBLE);
        } else {
            idVideoview.start();
            idImageviewStartPlay.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Get the video first frame
     * @param filePath
     * @return
     */
    private Bitmap createVideoThumbnail(String filePath){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try{
            File file = new File(filePath);
            if(!file.exists()){
                Logger.d("file not exits");
                throw new IllegalArgumentException();
            }
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0);
            final Bitmap bt = bitmap;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(bt != null){
                        idImageviewVideo.setImageBitmap(bt);
                    }
                }
            });
            Logger.d(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        }catch (IllegalArgumentException ex){
            Logger.d("Illegal argument exception: " +ex.getMessage());
        }catch (RuntimeException ex){
            Logger.d("Runtime Exception " + ex.getMessage());
        }finally {
            mActivity.dismissLoading();
            try {
                retriever.release();
            }catch (RuntimeException ex){}
        }

        return bitmap;
    }



    @OnClick(R.id.id_button_video_upload)
    public void uploadVideo() {
        mPresenter.uploadVideo(idButtonVideoUpload,videoPath);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);

    }
}

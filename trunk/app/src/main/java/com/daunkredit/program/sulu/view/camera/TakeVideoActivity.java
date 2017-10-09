package com.daunkredit.program.sulu.view.camera;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.camera.presenter.TakeVideoActPreImpl;
import com.daunkredit.program.sulu.view.camera.presenter.TakeVideoActPresenter;
import com.hwangjr.rxbus.RxBus;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;


/**
 * Created by Miaoke on 2017/3/16.
 */

public class TakeVideoActivity extends BaseActivity<TakeVideoActPresenter> implements TakeVideoActView {

    @BindView(R.id.id_imagebutton_video_back)
    ImageButton idImagebuttonVideoBack;
    @BindView(R.id.id_textview_video_cancel)
    TextView    idTextviewVideoCancel;

    FragmentManager     fragmentManager;
    FragmentTransaction fragmentTransaction;

    TakeVideoFragment takeVideoFragment;
    PlayVideoFragment playVideoFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_take_video2;
    }

    @Override
    protected void init() {
        setVideoFragment(0);
        RxBus.get().register(this);
        RxView.clicks(idTextviewVideoCancel).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserEventQueue.add(new ClickEvent(idTextviewVideoCancel.toString(), ActionType.CLICK, idTextviewVideoCancel.getText().toString()));
                TokenManager tokenManager = TokenManager.getInstance();
                LatestLoanAppBean bean = (LatestLoanAppBean) tokenManager.getMessage(FieldParams.LATESTBEAN);
                if (bean == null) {
                    return;
                }
                mPresenter.cancelLoan(bean);
            }
        });
    }

    @Override
    protected TakeVideoActPresenter initPresenterImpl() {
        return new TakeVideoActPreImpl();
    }


    private void setVideoFragment(int i) {

        // need to update
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (i) {
            case 0:
                hideFragment(fragmentTransaction);
                if (takeVideoFragment == null) {
                    takeVideoFragment = new TakeVideoFragment();
                    fragmentTransaction.add(R.id.id_fragment_video, takeVideoFragment);
                } else {
                    fragmentTransaction.show(takeVideoFragment);
                }
                break;
            case 1:
                hideFragment(fragmentTransaction);
                if (playVideoFragment == null) {
                    playVideoFragment = new PlayVideoFragment();
                    fragmentTransaction.add(R.id.id_fragment_video, playVideoFragment);
                } else {
                    fragmentTransaction.show(playVideoFragment);
                }
                break;
            default:
                break;
        }
        fragmentTransaction.commit();

    }


    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (playVideoFragment != null && !playVideoFragment.isHidden()) {
            fragmentTransaction.hide(playVideoFragment);
        }

        if (takeVideoFragment != null && !takeVideoFragment.isHidden()) {
            fragmentTransaction.hide(takeVideoFragment);
        }
    }

    public void toPlayVideo() {
        setVideoFragment(1);
    }

    public void toTakeVideo() {
        setVideoFragment(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.get().post(new EventCollection.VideoGiveUp());
    }

    @OnClick(R.id.id_imagebutton_video_back)
    public void back() {
        UserEventQueue.add(new ClickEvent(idImagebuttonVideoBack.toString(), ActionType.CLICK));
        RxBus.get().post(new EventCollection.VideoGiveUp());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        System.gc();
    }

}

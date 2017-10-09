package com.daunkredit.program.sulu.view;

import android.view.Window;
import android.view.WindowManager;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.view.presenter.SplashActPresenter;
import com.daunkredit.program.sulu.view.presenter.SplashActPresenterImpl;
import com.daunkredit.program.sulu.view.presenter.SplashActView;

/**
 * Created by milo on 17-2-8.
 */

public class SplashActivity extends BaseActivity<SplashActPresenter> implements SplashActView {
    @Override
    protected void initBeforeSetContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected SplashActPresenter initPresenterImpl() {
        return new SplashActPresenterImpl();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
        mPresenter.checkAndCopyAsserts();
        mPresenter.requestDataAndJump();
    }

}
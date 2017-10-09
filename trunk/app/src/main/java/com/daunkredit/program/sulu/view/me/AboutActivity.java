package com.daunkredit.program.sulu.view.me;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.me.presenter.AboutActPresenter;
import com.daunkredit.program.sulu.view.me.presenter.AboutPreImp;

import butterknife.BindView;

/**
 * Created by Miaoke on 2017/3/13.
 */

public class AboutActivity extends BaseActivity<AboutActPresenter> implements View.OnClickListener,AboutView {
    @BindView(R.id.ib_about_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.tv_about_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.iv_about)
    ImageView      mIvAbout;
    @BindView(R.id.tv_about)
    TextView mTvAbout;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        mIdImagebuttonBack.setOnClickListener(this);

        String topTitle = getResources().getText(R.string.text_title_about).toString();
        mIdTextviewTitle.setText(topTitle);
//        String detail = getResources().getText(R.string.text_about_company_detail).toString();
 //       mTvAbout.setJustedText(detail);
    }

    @Override
    protected AboutActPresenter initPresenterImpl() {
        return new AboutPreImp();
    }


    @Override
    public void onClick(View v) {
        UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,"Back"));
        int id = v.getId();
        switch (id) {
            case R.id.ib_about_back:
                finish();
                break;

        }
    }

}

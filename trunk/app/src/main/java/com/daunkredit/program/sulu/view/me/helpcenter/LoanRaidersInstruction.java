package com.daunkredit.program.sulu.view.me.helpcenter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.view.me.presenter.LoanRaidersInstrPreImp;
import com.daunkredit.program.sulu.view.me.presenter.LoanRaidersInstrPresenter;
import com.x.leo.timelineview.TimeLineView;

import butterknife.BindView;

/**
 * @作者:My
 * @创建日期: 2017/3/17 16:20
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanRaidersInstruction extends BaseActivity<LoanRaidersInstrPresenter> implements View.OnClickListener,LoanRaidersInstrView {
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.tlv_1)
    TimeLineView mTimeMarker1;
    @BindView(R.id.iv_loanraiders_step1)
    ImageView      mIvLoanraidersStep1;
    @BindView(R.id.tlv_2)
    TimeLineView   mTimeMarker2;
    @BindView(R.id.iv_loanraiders_step2)
    ImageView      mIvLoanraidersStep2;
    @BindView(R.id.tlv_3)
    TimeLineView   mTimeMarker3;
    @BindView(R.id.iv_loanraiders_step3)
    ImageView      mIvLoanraidersStep3;
    @BindView(R.id.tlv_4)
    TimeLineView   mTimeMarker4;
    @BindView(R.id.iv_loanraiders_step4)
    ImageView      mIvLoanraidersStep4;
    @BindView(R.id.tlv_5)
    TimeLineView   mTimeMarker5;
    @BindView(R.id.iv_loanraiders_step5)
    ImageView      mIvLoanraidersStep5;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_loanraidersinstruction;
    }

    @Override
    protected void init() {
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText(getResources().getText(R.string.text_title_loanraiders));
    }

    @Override
    protected LoanRaidersInstrPresenter initPresenterImpl() {
        return new LoanRaidersInstrPreImp();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_imagebutton_back:
                finish();
                break;
        }
    }
}

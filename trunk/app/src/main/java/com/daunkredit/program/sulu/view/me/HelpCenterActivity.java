package com.daunkredit.program.sulu.view.me;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.LoanAppHelpCenterTipsBean;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.me.helpcenter.HelpCenterAdapter;
import com.daunkredit.program.sulu.view.me.helpcenter.LoanRaidersInstruction;
import com.daunkredit.program.sulu.view.me.helpcenter.RepaymentRaidersInstruction2;
import com.daunkredit.program.sulu.view.me.presenter.HelpCenterActPreImp;
import com.daunkredit.program.sulu.view.me.presenter.HelpCenterActPresenter;

import java.util.ArrayList;

import butterknife.BindView;

public class HelpCenterActivity extends BaseActivity<HelpCenterActPresenter> implements HelpCenterActView,View.OnClickListener {


    @BindView(R.id.tv_helpcenter_loan_raiders)
    TextView       mTvHelpcenterLoanRaiders;
    @BindView(R.id.tv_helpcenter_repayment_raiders)
    TextView       mTvHelpcenterRepaymentRaiders;
    @BindView(R.id.lv_helpcenter)
    ListView       mLvHelpcenter;
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    private ListAdapter                     adapter;
    private ArrayList<LoanAppHelpCenterTipsBean> mDatas;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_help_center;
    }

    @Override
    protected void init() {
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText(getResources().getText(R.string.text_title_helpcenter));
        mTvHelpcenterLoanRaiders.setOnClickListener(this);
        mTvHelpcenterRepaymentRaiders.setOnClickListener(this);
        initDatas();
        adapter = new HelpCenterAdapter(this,mDatas);
        mLvHelpcenter.setAdapter(adapter);
    }

    @Override
    protected HelpCenterActPresenter initPresenterImpl() {
        return new HelpCenterActPreImp();
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        String[] questions = getResources().getStringArray(R.array.text_helpcenter_questions);
        String[] tips = getResources().getStringArray(R.array.text_helpcenter_questions_tips);
        for (int i = 0; i < questions.length; i++) {
            LoanAppHelpCenterTipsBean bean = new LoanAppHelpCenterTipsBean();
            bean.mTitle = questions[i];
            bean.mDetail = tips[i];
            mDatas.add(bean);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.id_imagebutton_back:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,"Back"));
                finish();
                break;
            case R.id.tv_helpcenter_loan_raiders:
                UserEventQueue.add(new ClickEvent(v.toString(),ActionType.CLICK,((TextView)v).getText().toString()));
                startActivity(new Intent(this,LoanRaidersInstruction.class));
                break;
            case R.id.tv_helpcenter_repayment_raiders:
                UserEventQueue.add(new ClickEvent(v.toString(),ActionType.CLICK,((TextView)v).getText().toString()));
                startActivity(new Intent(this,RepaymentRaidersInstruction2.class));
                break;
            default:
        }
    }
}

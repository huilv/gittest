package com.daunkredit.program.sulu.view.me.helpcenter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.LoanAppHelpCenterTipsBean;
import com.daunkredit.program.sulu.view.me.presenter.RepayRaiderInstrPreImp1;
import com.daunkredit.program.sulu.view.me.presenter.RepaymentRaidersInstrPresenter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @作者:My
 * @创建日期: 2017/3/17 16:20
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class RepaymentRaidersInstruction extends BaseActivity<RepaymentRaidersInstrPresenter> implements View.OnClickListener,RepaymentRaidersInstrView {
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.lv_repaymentraiders_instruction)
    ListView       mLvRepaymentraidersInstruction;
    private ListAdapter                          mAdapter;
    private ArrayList<LoanAppHelpCenterTipsBean> mDatas;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_repaymentraidersinstruction;
    }

    @Override
    protected void init() {
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText(getResources().getText(R.string.text_title_repaymentraiders));
        initDatas();
        mAdapter = new HelpCenterAdapter(this,mDatas);
        mLvRepaymentraidersInstruction.setAdapter(mAdapter);
    }

    @Override
    protected RepaymentRaidersInstrPresenter initPresenterImpl() {
        return new RepayRaiderInstrPreImp1();
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.text_repayment_raiders_instruction_title);
        String[] details = getResources().getStringArray(R.array.text_repayment_raiders_instruction_detail);

        for (int i = 0; i < titles.length; i++) {

            LoanAppHelpCenterTipsBean bean2 = new LoanAppHelpCenterTipsBean();
            bean2.mDetail = details[i];
            bean2.mTitle = titles[i];
            mDatas.add(bean2);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_imagebutton_back:
                finish();
                break;
        }
    }

}

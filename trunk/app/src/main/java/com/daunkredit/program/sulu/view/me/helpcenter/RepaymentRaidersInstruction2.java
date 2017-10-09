package com.daunkredit.program.sulu.view.me.helpcenter;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.utils.DensityUtils;
import com.daunkredit.program.sulu.view.me.presenter.RepayRaiderInstrPreImp2;
import com.daunkredit.program.sulu.view.me.presenter.RepaymentRaidersInstrPresenter;

import butterknife.BindView;

/**
 * @作者:My
 * @创建日期: 2017/3/17 16:20
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class RepaymentRaidersInstruction2 extends BaseActivity<RepaymentRaidersInstrPresenter> implements View.OnClickListener,RepaymentRaidersInstrView {
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.gl_repay_raiders)
    GridLayout mGridLayout;
    @BindView(R.id.fab_repayment_raiders)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.tv_repayment_raiders_details)
    TextView mTvDetail;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_repaymentraidersinstruction2;
    }

    @Override
    protected void init() {
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText(getResources().getText(R.string.text_title_repaymentraiders));
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initYW();
            }
        });
        initDatas();
    }

    @Override
    protected RepaymentRaidersInstrPresenter initPresenterImpl() {
        return new RepayRaiderInstrPreImp2();
    }

    private void initDatas() {

        String[] titles = getResources().getStringArray(R.array.text_repayment_raiders_instruction_title);
        final String[] details = getResources().getStringArray(R.array.text_repayment_raiders_instruction_detail);

        for (int i = 0; i < titles.length; i++) {
            TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.item_repayment_raiders,mGridLayout,false);
            view.setText(titles[i]);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ToastManager.showToast("" + finalI);
                    mTvDetail.setText(details[finalI]);
                }
            });
            GridLayout.LayoutParams  params = new GridLayout.LayoutParams(view.getLayoutParams());
            if (i % 2 == 1) {
                params.leftMargin = DensityUtils.dp2px(this, 20);
            }
            if (i > 1){
                params.topMargin = DensityUtils.dp2px(this, 20);
            }
            params.width = (getResources().getDisplayMetrics().widthPixels - DensityUtils.dp2px(this,48))/2;
            params.height = DensityUtils.dp2px(this, 70);
            view.setLayoutParams(params);
            mGridLayout.addView(view);
        }
        mTvDetail.setText(details[0]);

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

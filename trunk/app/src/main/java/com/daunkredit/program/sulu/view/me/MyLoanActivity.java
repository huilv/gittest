package com.daunkredit.program.sulu.view.me;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.me.presenter.MyLoanActPreImp;
import com.daunkredit.program.sulu.view.me.presenter.MyLoanActPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Miaoke on 2017/3/13.
 */

public class MyLoanActivity extends BaseActivity<MyLoanActPresenter> implements MyLoanActView,View.OnClickListener {
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.rv_loan)
    RecyclerView   mRvLoan;
    @BindView(R.id.fl_myloan)
    FrameLayout    mFlMyloan;
    private RecyclerView.Adapter              adapter;
    private ArrayList<HistoryLoanAppInfoBean> mLoanDatas;
    private UserApi                           mApi;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_myloan;
    }

    @Override
    protected void init() {
        mLoanDatas = new ArrayList<>();
        mIdImagebuttonBack.setOnClickListener(this);
        mIdTextviewTitle.setText(getString(R.string.text_title_myloan));
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mApi = ServiceGenerator.createService(UserApi.class);
        mRvLoan.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected MyLoanActPresenter initPresenterImpl() {
        return new MyLoanActPreImp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.initLoanData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_imagebutton_back:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,"Back"));
                finish();
                break;
            default:
        }
    }

    @Override
    public void initData(List<HistoryLoanAppInfoBean> historyLoanAppInfoBean) {
        mLoanDatas.clear();
        for (HistoryLoanAppInfoBean bean : historyLoanAppInfoBean) {
            if (!TextUtils.equals(bean.getStatus(), FieldParams.LoanStatus.SUBMITTED)) {
                mLoanDatas.add(bean);
            }
        }
        //ToastManager.showToast(mLoanDatas.size() + "");
        if (adapter == null) {
            adapter = new MyLoanAdapter(MyLoanActivity.this, mLoanDatas);
            mRvLoan.setAdapter(adapter);
            dismissLoading();
        } else {
            adapter.notifyDataSetChanged();
            dismissLoading();
        }
    }
}

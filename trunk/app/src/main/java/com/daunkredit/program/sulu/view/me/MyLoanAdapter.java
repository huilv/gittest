package com.daunkredit.program.sulu.view.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者:XJY
 * @创建日期: 2017/3/15 11:47
 * @描述:myloan模块recycleView的适配器
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MyLoanAdapter extends android.support.v7.widget.RecyclerView.Adapter {
    private final Context                     mContext;
    private ArrayList<HistoryLoanAppInfoBean> mLoanInfo;

    public MyLoanAdapter(Context context,List<HistoryLoanAppInfoBean> historyLoanAppInfoBean){
        mContext = context;
        mLoanInfo = (ArrayList<HistoryLoanAppInfoBean>) historyLoanAppInfoBean;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myloan, parent, false);
        MyLoanViewHolder myLoanViewHolder = new MyLoanViewHolder(view);
        return myLoanViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyLoanViewHolder myholder = (MyLoanViewHolder) holder;
        final HistoryLoanAppInfoBean historyLoanAppInfoBean = mLoanInfo.get(position);
        myholder.injectView(mContext,historyLoanAppInfoBean);
    }


    @Override
    public int getItemCount() {
        return mLoanInfo == null ? 0 : mLoanInfo.size();
    }

}

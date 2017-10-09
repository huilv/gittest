package com.daunkredit.program.sulu.view.me.helpcenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.bean.LoanAppHelpCenterTipsBean;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;

import java.util.ArrayList;

/**
 * @作者:My
 * @创建日期: 2017/3/20 11:32
 * @描述:RepaymentRaidersinstruction
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class HelpCenterAdapter extends BaseAdapter {
    private final Context                              mContext;
    private final ArrayList<LoanAppHelpCenterTipsBean> mDatas;

    public HelpCenterAdapter(Context context, ArrayList<LoanAppHelpCenterTipsBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public LoanAppHelpCenterTipsBean getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RepaymentRaidersHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_repaymentraiders_instruction, viewGroup, false);
            holder = new RepaymentRaidersHolder(view);
            view.setTag(holder);
        } else {
            holder = (RepaymentRaidersHolder) view.getTag();
        }
        holder.inject(getItem(i));
        return view;
    }

    static class RepaymentRaidersHolder {

        private  ImageView mIvArc;
        private  TextView  mTvTitle;
        private  TextView      mDetail;
        View itemView;

        public RepaymentRaidersHolder(View view) {
            itemView = view;
            mIvArc = (ImageView) itemView.findViewById(R.id.iv_repaymentraiders_instruction);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_item_repaymentraiders_instruction_title);
            mDetail = (TextView) itemView.findViewById(R.id.tv_item_repaymentraiders_instruction_detail);
        }

        public void inject(LoanAppHelpCenterTipsBean item) {
            mTvTitle.setText(item.mTitle);
            mDetail.setText(item.mDetail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserEventQueue.add(new ClickEvent(view.toString(), ActionType.CLICK,mTvTitle.getText().toString()));
                    if (mIvArc.isSelected()) {
                        mIvArc.setSelected(false);
                        mDetail.setVisibility(View.GONE);
                    }else {
                        mIvArc.setSelected(true);
                        mDetail.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}

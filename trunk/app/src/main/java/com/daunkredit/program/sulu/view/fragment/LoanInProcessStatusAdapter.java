package com.daunkredit.program.sulu.view.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.x.leo.timelineview.TimeLineView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @作者:My
 * @创建日期: 2017/7/31 17:00
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

class LoanInProcessStatusAdapter extends BaseAdapter {
    private final List<LoanAppBeanFather.StatusLogsBean> mDatas;
    private final Context mCtx;

    public LoanInProcessStatusAdapter(List<LoanAppBeanFather.StatusLogsBean> statusLogs, LoanInProcessFragment loanInProcessFragment) {
        mDatas = statusLogs;
        mCtx = loanInProcessFragment.getContext();
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public LoanAppBeanFather.StatusLogsBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InnerStatusHolder holder = InnerStatusHolder.getHolder(convertView, parent);
        holder.injectView(getItem(position),position + 1 == getCount());
        return holder.getView();
    }

    static class InnerStatusHolder {
        @BindView(R.id.tlv_process_status)
        TimeLineView tlvProcessStatus;
        @BindView(R.id.tv_status_name)
        TextView tvStatusName;
        @BindView(R.id.tv_status_time)
        TextView tvStatusTime;
        @BindView(R.id.tv_status_detail)
        TextView tvStatusDetail;
        @BindView(R.id.iv_process_status)
        ImageView ivProcessStatus;
        private View view;

        private InnerStatusHolder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }

        public static InnerStatusHolder getHolder(View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loaninprocess_status, parent, false);
                InnerStatusHolder innerStatusHolder = new InnerStatusHolder(view);
                view.setTag(innerStatusHolder);
                return innerStatusHolder;
            } else {
                return (InnerStatusHolder) view.getTag();
            }
        }

        public View getView() {
            return view;
        }

        public void injectView(LoanAppBeanFather.StatusLogsBean item, boolean isLast) {
            if (isLast) {
                tlvProcessStatus.setMarkPosition(TimeLineView.POINT_END);
//                ivProcessStatus.setImageResource(R.mipmap.cash_icon);
            }else{
//                ivProcessStatus.setImageResource(R.mipmap.timeline_complete);
                tlvProcessStatus.setMarkPosition(TimeLineView.POINT_MIDDLE);
            }
            tvStatusName.setText(item.getStatus());
            tvStatusTime.setText(TimeFormat.convertTime(item.getCreateTime()));
            tvStatusDetail.setText(null);
        }
    }
}

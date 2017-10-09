package com.daunkredit.program.sulu.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.bean.MsgInboxBean;
import com.daunkredit.program.sulu.common.utils.TimeFormat;

import java.util.ArrayList;

/**
 * @作者:My
 * @创建日期: 2017/3/27 15:41
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

class SysInfoAdapter extends BaseAdapter {
    private Context                 mContext;
    private ArrayList<MsgInboxBean> mDatas;
    private OnMsgItemClickListener  mOnClickListener;

    public SysInfoAdapter(Context context, ArrayList<MsgInboxBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public MsgInboxBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SysInfoHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sysinfo, parent, false);
            holder = new SysInfoHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SysInfoHolder) convertView.getTag();
        }
        final MsgInboxBean item = getItem(position);
        holder.timeText.setText(TimeFormat.convertTime(item.getCreateTime()));
        int length = item.getMsgBody().length();
        SpannableString spannableString;
        if (item.isRead()) {
            spannableString = new SpannableString(item.getMsgBody() + "  " + mContext.getString(R.string.text_readed));
            spannableString.setSpan(new ForegroundColorSpan(Color.GREEN),length,spannableString.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            spannableString = new SpannableString(item.getMsgBody() + "  " + mContext.getString(R.string.text_new));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED),length,spannableString.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        holder.detailText.setText(spannableString);

        holder.detailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isRead()) {
                    mOnClickListener.onClick(item);
                }
            }
        });
        return convertView;
    }

    public void setOnClickListener(OnMsgItemClickListener listener) {
        mOnClickListener = listener;
    }

    private static class SysInfoHolder {
        View     itemView;
        TextView timeText;
        TextView detailText;

        public SysInfoHolder(View convertView) {
            itemView = convertView;
            timeText = (TextView) itemView.findViewById(R.id.tv_item_sysinfo_time);
            detailText = (TextView) itemView.findViewById(R.id.tv_item_sysinfo_detail);
        }

    }
}

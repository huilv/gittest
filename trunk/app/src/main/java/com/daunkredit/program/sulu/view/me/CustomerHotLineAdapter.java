package com.daunkredit.program.sulu.view.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.x.leo.circles.CircleImage;

/**
 * @作者:XJY
 * @创建日期: 2017/3/16 17:22
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class CustomerHotLineAdapter extends BaseAdapter {

    Context mContext;
    Object  mDatas;

    public CustomerHotLineAdapter(Context context, Object datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        int resId = itemViewType == 1 ? R.layout.item_im_lr : R.layout.item_im_rl;
        IMViewHolder holder;
        if (convertView == null ) {
            convertView = LayoutInflater.from(mContext).inflate(resId, parent, false);
            holder = new IMViewHolder(convertView);
            holder.viewType = itemViewType;
            convertView.setTag(holder);
        }else{
            holder = (IMViewHolder) convertView.getTag();
            if (holder.viewType != itemViewType) {
                convertView = LayoutInflater.from(mContext).inflate(resId, parent, false);
                holder = new IMViewHolder(convertView);
                holder.viewType = itemViewType;
                convertView.setTag(holder);
            }
        }
        holder.injectDatas();
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    static class IMViewHolder {
        View        mItemView;
        int         viewType;
        CircleImage mimg;
        TextView        mText;

        public IMViewHolder(View convertView) {
            mItemView = convertView;
            mText = (TextView) mItemView.findViewById(R.id.tv_message);
            mimg = (CircleImage) mItemView.findViewById(R.id.ci_item_im);
        }

        public void injectDatas() {

        }
    }
}

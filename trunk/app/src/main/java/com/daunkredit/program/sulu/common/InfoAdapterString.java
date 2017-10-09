package com.daunkredit.program.sulu.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.sulu.kotlin.fragment.OnInfoAdapterItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Miaoke on 2017/3/1.
 */

public class InfoAdapterString extends BaseAdapter implements InfoAdapter {




    private ArrayList<InfoItem> mItemInfo = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnInfoAdapterItemClickListener listener;

    public InfoAdapterString(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final String item, InfoType type){
        mItemInfo.add(new InfoItem(item,type));
        notifyDataSetChanged();
    }

    public void addItem(final String item, InfoType type, int id){
        mItemInfo.add(new InfoItem(item,type,id));
        notifyDataSetChanged();
    }

    public void addItem(final String item, InfoType type, int id,String level){
        mItemInfo.add(new InfoItem(item,type,id,level));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       // Logger.d("getView " + position+ " "+ convertView);
        ViewHolder holder = null;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.bank_list_item,null);
            holder = new ViewHolder();
            holder.panelView = convertView.findViewById(R.id.id_relativelayout_bank_item);
            holder.textView = (TextView) convertView.findViewById(R.id.id_textview_bank_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textView.setText(mItemInfo.get(position).getInfoStr());
        holder.panelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(mItemInfo.get(position));
                }
            }
        });

        return convertView;
    }

    @Override
    public void setOnItemClickListener(@NotNull OnInfoAdapterItemClickListener onInfoAdapterItemClickListener) {
        listener = onInfoAdapterItemClickListener;
    }


    public static class ViewHolder{
        public View panelView;
        public TextView textView;

    }

    public static class InfoItem implements InfoAdapter.InfoItem{

        private String infoStr;
        private InfoType type;
        private int id;
        private String level;


        public InfoItem(String infoStr, InfoType type){
            this.infoStr = infoStr;
            this.type = type;
        }

        public InfoItem(String infoStr, InfoType type, int id) {
            this.infoStr = infoStr;
            this.type = type;
            this.id = id;
        }

        public InfoItem(String infoStr, InfoType type, int id, String level) {
            this.infoStr = infoStr;
            this.type = type;
            this.id = id;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getInfoStr() {
            return infoStr;
        }

        @Override
        public String getValueStr() {
            return infoStr;
        }

        public void setInfoStr(String infoStr) {
            this.infoStr = infoStr;
        }

        public InfoType getType() {
            return type;
        }

        public void setType(InfoType type) {
            this.type = type;
        }
    }

    public static class ItemSelectedEvent<T>{
        public T data;
        public int pos;

        public ItemSelectedEvent(int pos, T data){
            this.pos = pos;
            this.data = data;
        }

    }

}

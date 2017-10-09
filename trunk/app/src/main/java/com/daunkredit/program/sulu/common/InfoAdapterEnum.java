package com.daunkredit.program.sulu.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.certification.status.InfoValueType;
import com.sulu.kotlin.fragment.OnInfoAdapterItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Miaoke on 2017/3/1.
 */

public class InfoAdapterEnum extends BaseAdapter implements InfoAdapter {

    private final Context mContext;




    private ArrayList<InfoItem> mItemInfo = new ArrayList<>();
    private LayoutInflater mInflater;

    public InfoAdapterEnum(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addItem(final InfoValueType item, InfoType type){
        mItemInfo.add(new InfoItem(item,type));
        notifyDataSetChanged();
    }

    public void addItem(final InfoValueType item, InfoType type, int id){
        mItemInfo.add(new InfoItem(item,type,id));
        notifyDataSetChanged();
    }

    public void addItem(final InfoValueType item, InfoType type, int id,String level){
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
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, mItemInfo.get(position).getInfoStr()));
            }
        });

        return convertView;
    }
    private OnInfoAdapterItemClickListener listener;
    @Override
    public void setOnItemClickListener(@NotNull OnInfoAdapterItemClickListener onInfoAdapterItemClickListener) {
        listener = onInfoAdapterItemClickListener;
    }


    public static class ViewHolder{
        public View panelView;
        public TextView textView;

    }

    public  class InfoItem implements InfoAdapter.InfoItem{

        private InfoValueType infoStr;
        private InfoType type;
        private int id;
        private String level;


        public InfoItem(InfoValueType infoStr, InfoType type){
            this.infoStr = infoStr;
            this.type = type;
        }

        public InfoItem(InfoValueType infoStr, InfoType type, int id) {
            this.infoStr = infoStr;
            this.type = type;
            this.id = id;
        }

        public InfoItem(InfoValueType infoStr, InfoType type, int id, String level) {
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
            return InfoAdapterEnum.this.mContext.getResources().getText(infoStr.getShowString()).toString();
        }

        @Override
        public String getValueStr() {
            return infoStr.getValue();
        }

        public void setInfoStr(InfoValueType infoStr) {
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

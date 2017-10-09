package com.daunkredit.program.sulu.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;


/**
 * Created by Miaoke on 2017/2/23.
 */

public class BankAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;

    public BankAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View view = convertView;
        if(view == null){

            view = layoutInflater.inflate(R.layout.bank_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.id_textview_bank_item);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }




        switch(position){
            case 0:
                viewHolder.textView.setText("ICBC");
                break;
            case 1:
                viewHolder.textView.setText("ATM");
                break;
            case 2:
                viewHolder.textView.setText("ABC");
                break;
            default:
                viewHolder.textView.setText("CCB");
                break;
        }

        return view;
    }

    static class ViewHolder{
        TextView textView;
    }
}

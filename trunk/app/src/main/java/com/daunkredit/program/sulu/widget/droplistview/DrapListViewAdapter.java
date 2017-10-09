package com.daunkredit.program.sulu.widget.droplistview;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/4/13 11:14
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class DrapListViewAdapter {

    private final String[]         mStrings;
    private final XLeoDropListView mParentView;
    private final int              mLayoutId;
    private final Context          mContext;
    private       ListPopupWindow  mView;
//    private       AlertDialog                mAlertDialog;
    private       ListAdapter      mAdapter;
    private       int              mSelectedPosition;
//    private       WindowManager              mWindowManager;
//    private       WindowManager.LayoutParams mParams;

    public DrapListViewAdapter(final XLeoDropListView view, int layoutId, String[] strs){
        if (view == null) {
            throw new IllegalArgumentException("view cann't be null");
        }
        mContext = view.getContext();
        mSelectedPosition = -1;
        mStrings = strs;
        mParentView = view;
        mLayoutId = layoutId;
        //mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = new ListPopupWindow(mContext);
        mView.setAnchorView(mParentView);
        mView.setDropDownGravity(Gravity.BOTTOM);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mParentView.setText(mStrings[position]);
                mView.dismiss();
            }
        });
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mStrings == null ? 0:mStrings.length;
            }

            @Override
            public String getItem(int position) {
                return mStrings[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(mLayoutId == 0 ? R.layout.item_default_xleo_drop_list : mLayoutId, parent,false);
                }
                ((TextView)convertView).setText(mStrings[position]);
                return convertView;
            }
        };
        mView.setAdapter(mAdapter);
    }
    public void show() {
        mView.show();
//        if (mParams == null) {
//            mParams = new WindowManager.LayoutParams();
//            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            mParams.width = (int) (mContext.getResources().getDisplayMetrics().density * 122);
//            mParams.x = (int) mParentView.getX();
//            mParams.gravity = Gravity.LEFT;
//            mParams.y = (int) (mParentView.getY() + mParentView.getHeight());
//        }
//        mWindowManager.addView(mView, mParams);

//                mAlertDialog = new AlertDialog.Builder(mParentView.getContext())
//                .setCancelable(true)
//                .setAdapter(mAdapter, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mParentView.setText((String)mAdapter.getItem(which));
//                        mSelectedPosition = which;
//                        mAlertDialog.dismiss();
//                    }
//                }).create();
//
//        mAlertDialog.show();
    }

    public void setSeletedItem(int seletedItem) {
        if(seletedItem < mStrings.length){
            mSelectedPosition = seletedItem;
            mParentView.setText(mStrings[seletedItem]);
        }
    }

    public String getSelectedItem() {
        if (mSelectedPosition != -1) {
            return mStrings[mSelectedPosition];
        }
        return null;
    }
}

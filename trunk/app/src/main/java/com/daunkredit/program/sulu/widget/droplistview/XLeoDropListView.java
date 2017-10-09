package com.daunkredit.program.sulu.widget.droplistview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @作者:My
 * @创建日期: 2017/4/13 11:10
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XLeoDropListView extends AppCompatTextView {
    private DrapListViewAdapter mAdapter;

    public XLeoDropListView(Context context) {
        this(context, null);
    }

    public XLeoDropListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }


    public XLeoDropListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public XLeoDropListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.show();
                }
            }
        });

    }


    public void setAdapter(DrapListViewAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter can't be null");
        }
        mAdapter = adapter;
    }

    public void setSelectedItem(int position) {
        if (mAdapter != null) {
            mAdapter.setSeletedItem(position);
        }
    }

    public String getSelectedItem() {
        if (mAdapter != null) {
            return mAdapter.getSelectedItem();
        }
        return null;
    }
}

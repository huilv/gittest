package com.daunkredit.program.sulu.widget.xleovideoview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @作者:My
 * @创建日期: 2017/4/14 14:21
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SelfDefVideoView extends VideoView {
    public SelfDefVideoView(Context context) {
        this(context,null);
    }

    public SelfDefVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelfDefVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SelfDefVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0,widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
}

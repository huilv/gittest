package com.daunkredit.program.sulu.widget.xleotoast;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/4/21 12:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XLeoToast extends Toast {

    private static Context   sContext;
    private        Context   mContext;
    private static XLeoToast sXLeoToast;

    /**
     *
     * @param context 使用全局的Context
     */
    public static void initXLeoToast(Context context){
        if (sContext == null) {
            sContext = context;
            sXLeoToast = new XLeoToast(sContext);
        }
    }
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context
     */
    public XLeoToast(Context context) {
        super(context);
        mContext = context;
    }

    public static void showMessage(String message){
        if (sXLeoToast == null) {
            throw new IllegalStateException("not init toast yet");
        }
        View view = sXLeoToast.getView();
        if (view == null) {
            View view1 = sXLeoToast.initView();
            sXLeoToast.setView(view1);
            view = view1;
        }
        TextView toast = (TextView) view.findViewById(R.id.tv_toast);
        toast.setText(message);
        sXLeoToast.setDuration(Toast.LENGTH_SHORT);
        sXLeoToast.show();
    }

    public static void showMessage(int strId){
        showMessage(sXLeoToast.mContext.getString(strId));
    }

    private  View initView() {
        if (sContext == null) {
            throw new IllegalStateException("not init toast yet");
        }
        View view = View.inflate(mContext, R.layout.xleo_toast, null);
        return view;
    }

    @Override
    public void show() {
        if (getView() == null) {
            return;
        }
        super.show();
    }
}

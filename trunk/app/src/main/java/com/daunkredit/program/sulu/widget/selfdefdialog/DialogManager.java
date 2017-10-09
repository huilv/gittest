package com.daunkredit.program.sulu.widget.selfdefdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.common.utils.DensityUtils;
import com.sulu.kotlin.utils.AssertsCopyer;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static com.alibaba.mobileim.YWChannel.getResources;

/**
 * @作者:My
 * @创建日期: 2017/3/21 18:50
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class DialogManager {
    public static Dialog newDialog(Context context, View view, boolean b) {
        Dialog dialog = new AlertDialog.Builder(context,R.style.style_bg_transparent_dialog)
                .setView(view)
                .setCancelable(b)
                .create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
        return dialog;
    }

    public static void showStatementDialog(Context context, String titleText, final String[] detailText) {
        if (!(context instanceof Activity)) {
            return;
        }
        View view = View.inflate(context, R.layout.dialog_show_statement, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().widthPixels * 0.8), (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        TextView title = (TextView) view.findViewById(R.id.tv_show_statement_title);
        title.setText(titleText);
        LinearLayout detail = (LinearLayout) view.findViewById(R.id.ll_show_statement_detail);
        detail.setPadding(DensityUtils.dp2px(context, 14), 0, DensityUtils.dp2px(context, 14), 0);
        ListView listView = new ListView(context);
        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.setDividerHeight(0);
        detail.addView(listView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.6)));
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return detailText == null ? 0 : detailText.length;
            }

            @Override
            public String getItem(int position) {
                return detailText[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    TextView textView = new TextView(parent.getContext());
                    textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.LEFT);
                    textView.setTextColor(getResources().getColor(R.color.color_text_gray));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    convertView = textView;
                }
                TextView tv = (TextView) convertView;
                StringToFormattedSpannable stt = new StringToFormattedSpannable(getItem(position), true);
                tv.setText(stt.getSsb());
                tv.setPadding(stt.getPaddingLeft(), 0, 0, 0);
                return convertView;
            }
        });
        DialogManager.newDialog(context, view, true);
    }

    public static DialogBuilder newListViewDialog(Context context) {
        return new DialogBuilder(context);
    }

    public static PopupWindow newAlertDialog(final Context context, int dialog_ensure_logout, final AlertDialogListener alertDialogListener) {
        View view = View.inflate(context, R.layout.dialog_logout, null);
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(view);
        popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
        view.measure(0,0);
        popupWindow.setHeight(view.getMeasuredHeight());
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        view.findViewById(R.id.btn_dialog_check_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                alertDialogListener.onCancel();
            }
        });
        view.findViewById(R.id.btn_dialog_check_ensurn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                alertDialogListener.onEnsure();
            }
        });
        TextView textView = (TextView) view.findViewById(R.id.tv_dialog_check_text);
        textView.setText(context.getString(dialog_ensure_logout));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
                    attributes.alpha = 1.0f;
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    activity.getWindow().setAttributes(attributes);
                }
            }
        });
        return popupWindow;
    }


    public static void show(Context context,PopupWindow popupWindow,View token){
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.alpha = 0.6f;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            activity.getWindow().setAttributes(attributes);
        }
        popupWindow.showAtLocation(token, Gravity.BOTTOM,0,0);
        popupWindow.update();
    }

    public static Dialog showHtmlDialog(Context context, @NotNull String url, @NotNull String title, boolean b) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_html_agreement,null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        WebView wb = (WebView) view.findViewById(R.id.wv);
        tvTitle.setText(title);
        wb.setWebViewClient(new WebViewClient());
        wb.setWebChromeClient(new WebChromeClient());
        wb.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        wb.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        wb.loadUrl(url);
        wb.getSettings().setJavaScriptEnabled(false);
        wb.getSettings().setSupportZoom(true);
        wb.getSettings().setDefaultTextEncodingName("UTF-8");
        return newDialog(context,view,b);
    }

    public static void showHtmlDialogWithCheck(final String statement_file, final Context ctx, final String title) {
        if (AssertsCopyer.INSTANCE.checkFile(statement_file,ctx)) {
            DialogManager.showHtmlDialog(ctx, "file:" + ctx.getFilesDir() + File.separator + statement_file, title, true);
        }else{
            AssertsCopyer.INSTANCE.copyfile(statement_file, ctx.getAssets(),ctx, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    DialogManager.showHtmlDialog(ctx, "file:" + ctx.getFilesDir() + File.separator + statement_file, title, true);
                    return null;
                }
            });
        }
    }

    public static class DialogBuilder {
        private Context     mContext;
        private ListView    mListView;
        private View        mView;
        private boolean     isExpanded;
        private int         mGravity;
        private BaseAdapter mAdapter;
        private int         mHeader;

        public DialogBuilder(Context context) {
            mContext = context;
            mView = LayoutInflater.from(context).inflate(R.layout.dialog_list_view, null, false);
            mListView = (ListView) mView.findViewById(R.id.lv_dialog_view);
        }

        public DialogBuilder setContentHolder(Holder holder) {

            return this;
        }

        public DialogBuilder setExpanded(boolean isExpanded) {
            this.isExpanded = isExpanded;
            return this;
        }

        public DialogBuilder setAdapter(BaseAdapter adapter) {
            if (adapter == null) {
                throw new IllegalArgumentException("adapter cann't be null");
            }
            mAdapter = adapter;
            mListView.setAdapter(adapter);
            return this;
        }

        public DialogBuilder setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        public Dialog create() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
            if (mAdapter.getCount() <= 0) {
                params.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.6f);
                params.height = 1;
                params.gravity = mGravity;
            } else {
                View view = mAdapter.getView(0, null, mListView);
                view.measure(0, 0);
                params.width = view.getMeasuredWidth();
                int height = (view.getMeasuredHeight() + mListView.getDividerHeight()) * mAdapter.getCount();
                if (height > (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6f) && isExpanded) {
                    params.height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6f);
                } else {
                    params.height = height;
                }
                params.gravity = mGravity;
            }
            mView.setLayoutParams(params);
            if (mHeader != 0) {
                View header = LayoutInflater.from(mContext).inflate(mHeader, mListView, false);
                mListView.addHeaderView(header);
            }
            Dialog dialog = new AlertDialog.Builder(mContext)
                    .setView(mView)
                    .setCancelable(true)
                    .create();
            return dialog;
        }

        public DialogBuilder setHeader(int header) {
            mHeader = header;
            return this;
        }
    }


}

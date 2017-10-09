package com.daunkredit.program.sulu.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.x.leo.circles.CircleProgressButton;
import com.x.leo.listexpend.AutoRollListView;
import com.x.leo.listexpend.CycleAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * @作者:My
 * @创建日期: 2017/3/29 14:20
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class DemoActivity extends Activity{
    private CircleProgressButton ci;
    //    //>=48bit
//    public static final String KEY = "hello eu adfaf sfdsfdsfdsfdsf dsfsfsfsf asssssfsddf erwrwd dt  eetet et3 5522 fdgdfg";
//    //==8bit
//    public static final String KEYEV = "secondke";
//
//    private EditText mInput;
//    private TextView mResult;
//    private byte[] encodeCBC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        AutoRollListView list = (AutoRollListView) findViewById(R.id.arl);
        list.setAdapter(new CycleAdapter() {
            @NotNull
            @Override
            public View getRealView(int i, View convertView, ViewGroup parent) {
                TextView textView = new TextView(DemoActivity.this);
                textView.setText("current position:" + i );
                return textView;
            }

            @NotNull
            @Override
            public Object getRealItem(int position) {
                return position;
            }

            @Override
            public int getRealCount() {
                return 20;
            }
        });
//        initListVIew();
        //initCircleProgressBar();
//        mInput = (EditText) findViewById(R.id.et_input);
//        mResult = (TextView) findViewById(R.id.tv_result);
    }

    private void initListVIew() {
//        ListView lv = (ListView) findViewById(R.id.lv_main);
//        lv.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return 100;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return position;
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                TextView tv = new TextView(parent.getContext());
//                tv.setText("" + position);
//                tv.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                return tv;
//            }
//        });
    }

    private void initCircleProgressBar() {
//        ci = (CircleProgressButton) findViewById(R.id.cpb);
//        ci.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                XLeoToast.showMessage("start");
//            }
//        });
//        ci.setAnimationListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                XLeoToast.showMessage("animator start");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                XLeoToast.showMessage("animator end");
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
    }

//    public void encype(View view) {
//        Editable text = mInput.getText();
//        if (text != null) {
//            try {
//                encodeCBC = EncodeUtils.des3EncodeCBC(KEY.getBytes(), KEYEV.getBytes(), text.toString().getBytes());
//                mResult.setText(new String(encodeCBC));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void decype(View view) {
//        CharSequence text = mResult.getText();
//        if (text != null) {
//            try {
//                byte[] bytes = EncodeUtils.des3DecodeCBC(KEY.getBytes(), KEYEV.getBytes(), encodeCBC);
//                mResult.setText(new String(bytes));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

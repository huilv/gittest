package com.daunkredit.program.sulu.view.me;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.me.presenter.CustomerHotLinActPreImp;
import com.daunkredit.program.sulu.view.me.presenter.CustomerHotLinActPresenter;

import butterknife.BindView;

public class CustomerHotLineActivity extends BaseActivity<CustomerHotLinActPresenter> implements View.OnClickListener,CustomerHotLineActView {


    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;

    @BindView(R.id.fl_customer_hotline_container)
    FrameLayout mFlCustomerHotlineContainer;
    @BindView(R.id.lv_customer_hotline)
    ListView    mLvCustomerHotline;
    @BindView(R.id.iv_customer_hotline_plus)
    ImageView   mIvCustomerHotlinePlus;
    @BindView(R.id.et_customer_hotline)
    EditText    mEtCustomerHotline;
    @BindView(R.id.iv_customer_hotline_emoji)
    ImageView   mIvCustomerHotlineEmoji;

    private ListAdapter mAdapter;
    private Object      mDatas;
    private View        mEmojiView;
    private View        mPlusView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_customer_hotline;
    }

    @Override
    protected void init() {
        initFunction();
//        initYW();
        //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mEtCustomerHotline.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == 0 || i == EditorInfo.IME_ACTION_GO) && keyEvent != null) {
                    //TODO:发送对话
                    String trim = mEtCustomerHotline.getText().toString().trim();
                    ToastManager.showToast(trim);
                    mEtCustomerHotline.setText("");
                    return true;
                }
                return false;
            }
        });
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText(getResources().getText(R.string.text_title_customer_hot_line));


        mIvCustomerHotlineEmoji.setOnClickListener(this);
        mIvCustomerHotlinePlus.setOnClickListener(this);

        mAdapter = new CustomerHotLineAdapter(this, mDatas);
        mLvCustomerHotline.setAdapter(mAdapter);

    }

    @Override
    protected CustomerHotLinActPresenter initPresenterImpl() {
        return new CustomerHotLinActPreImp();
    }


    private void initFunction() {
        TextView textView = new TextView(this);
        textView.setText("Emoji");
        mEmojiView = textView;
        TextView textView1 = new TextView(this);
        textView1.setText("plus");
        mPlusView = textView1;

    }

    boolean isShowingEmoji = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_imagebutton_back:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,"Back"));
                finish();
                break;
            case R.id.iv_customer_hotline_emoji:

                if (mFlCustomerHotlineContainer.getVisibility() == View.VISIBLE && isShowingEmoji) {
                    mFlCustomerHotlineContainer.setVisibility(View.GONE);
                } else {
                    mFlCustomerHotlineContainer.setVisibility(View.VISIBLE);
                    isShowingEmoji = true;
                    mFlCustomerHotlineContainer.removeAllViews();
                    mFlCustomerHotlineContainer.addView(mEmojiView);
                }
                break;
            case R.id.iv_customer_hotline_plus:
                if (mFlCustomerHotlineContainer.getVisibility() == View.VISIBLE && !isShowingEmoji) {
                    mFlCustomerHotlineContainer.setVisibility(View.GONE);
                } else {
                    mFlCustomerHotlineContainer.setVisibility(View.VISIBLE);
                    isShowingEmoji = false;
                    mFlCustomerHotlineContainer.removeAllViews();
                    mFlCustomerHotlineContainer.addView(mPlusView);
                }
                break;
        }
    }

}

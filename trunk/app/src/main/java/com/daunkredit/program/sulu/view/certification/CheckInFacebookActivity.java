package com.daunkredit.program.sulu.view.certification;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.view.certification.presenter.CheckInFacActPresenter;
import com.daunkredit.program.sulu.widget.selfdefedittext.XLeoEditText;

import butterknife.BindView;

/**
 * @作者:My
 * @创建日期: 2017/4/17 19:47
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class CheckInFacebookActivity extends BaseActivity<CheckInFacActPresenter> implements View.OnClickListener, CheckInFacActView{
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.bet_check_in_facebook_input)
    XLeoEditText   mBetCheckInFacebookInput;
    @BindView(R.id.ib_check_in_facebook_delete)
    ImageButton    mIbCheckInFacebookDelete;
    @BindView(R.id.btn_check_in_facebook_submit)
    Button         mBtnCheckInFacebookSubmit;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_check_in_facebook;
    }

    @Override
    protected void init() {
        mIdTextviewTitle.setText(getString(R.string.text_title_checkin_facebook));
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIbCheckInFacebookDelete.setOnClickListener(this);
        mBtnCheckInFacebookSubmit.setOnClickListener(this);
    }

    @Override
    protected CheckInFacActPresenter initPresenterImpl() {
        return new CheckInFacActPreImp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_imagebutton_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_check_in_facebook_submit:
                Intent intent = new Intent();
                Editable text = mBetCheckInFacebookInput.getText();
                if(text != null && text.length() > 0) {
                    String result = text.toString();
                    intent.putExtra(FieldParams.FACEBOOK_ID, result);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    ToastManager.showToast(getString(R.string.show_facebookid_null));
                }
                break;
            case R.id.ib_check_in_facebook_delete:
                mBetCheckInFacebookInput.setText(null);
                if (!mBetCheckInFacebookInput.isFocused()) {
                   mBetCheckInFacebookInput.requestFocus();
                }
                break;
        }
    }
}

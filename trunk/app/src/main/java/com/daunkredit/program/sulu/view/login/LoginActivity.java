package com.daunkredit.program.sulu.view.login;

import android.Manifest;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.LoginRequestBean;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.common.network.CaptchaApi;
import com.daunkredit.program.sulu.harvester.HarvestInfoManager;
import com.daunkredit.program.sulu.harvester.PermissionManager;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.login.presenter.LoginActPresenter;
import com.daunkredit.program.sulu.view.login.presenter.LoginactPreImpl;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.selfdefedittext.OnCheckInputResult;
import com.daunkredit.program.sulu.widget.selfdefedittext.OnCheckInputResultAdapter;
import com.daunkredit.program.sulu.widget.selfdefedittext.XLeoEditText;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.utils.SpanBuilder;

import org.apache.commons.lang3.RandomStringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by Miaoke on 2017/2/28.
 */

public class LoginActivity extends BaseActivity<LoginActPresenter> implements LoginActView {


    @BindView(R.id.id_button_login)
    Button       login;
    @BindView(R.id.id_edittext_phone_number)
    XLeoEditText editTextPhone;
    @BindView(R.id.id_edittext_SMS_code)
    XLeoEditText editTextSMSCode;
    @BindView(R.id.id_login_statement)
    TextView     mStatementNotify;
    @BindView(R.id.id_imageview_code)
    ImageView    imageViewCode;
    @BindView(R.id.id_imagebutton_refresh)
    ImageButton  imButtonRefresh;
    @BindView(R.id.id_button_obtain_code)
    Button       buttonObtain;
    @BindView(R.id.id_edittext_graphical_code)
    XLeoEditText editTextGraphicalCode;
    @BindView(R.id.id_linearlayout_graphical_code)
    LinearLayout linearLayoutGraphical;
    @BindView(R.id.id_edittext_login_invite_code)
    XLeoEditText idEdittextLoginInviteCode;
    @BindView(R.id.ll_phone_number)
    LinearLayout mLlPhone;


    String phoneStr;

    String sid;
    String smsCode;
    String captcha;
    int loginCount = 0;

    LoginRequestBean loginRequestBean;
    private int defaultTextColor;
    private int mErrorColor;
    private String statement_file = "needcopy/statement_agreement.html";;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        mErrorColor = getResources().getColor(R.color.colorAlerm_red);
        //      refreshCaptcha();
        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextPhone.setCursorVisible(true);
                    mLlPhone.setActivated(true);
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(editTextPhone, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mLlPhone.setActivated(false);
                    editTextPhone.setCursorVisible(false);
                }
            }
        });
        mLlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPhone.requestFocus();
            }
        });
        loginRequestBean = new LoginRequestBean();
        initWrongInputDetect2();
        initLegal();

        try {
            new PermissionManager().fetchPersonInfo(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS);
        } catch (Exception e) {
            Logger.d("Exception: " + e.getMessage());
        }


        if (!HarvestInfoManager.getInstance().hasInstallAppInfo()) {
            Logger.d("begin get install app info");
            HarvestInfoManager.getInstance().setInstallAppStatus("0");
            HarvestInfoManager.getInstance().setInstallAppJsonobject(PermissionManager.getInstallApp(LoginActivity.this));
        }
        if (!HarvestInfoManager.getInstance().hasMachineType()) {
            HarvestInfoManager.getInstance().setMachineTypeStatus("0");
            HarvestInfoManager.getInstance().setMachineTypeJsonobject(PermissionManager.getMachineType(LoginActivity.this));
        }

    }

    @Override
    protected LoginActPresenter initPresenterImpl() {
        return new LoginactPreImpl();
    }

    private void initLegal() {
        try {


        String text = getString(R.string.login_statement);
        final String legal = getString(R.string.login_statement_title);
        SpannableStringBuilder result = SpanBuilder.INSTANCE.init(text)
                .setLinkSpan(text.indexOf(legal) - 1, text.indexOf(legal) + legal.length() + 1, getResources().getColor(R.color.colorProgress_blue),
                        new Function0<Unit>() {
            @Override
            public Unit invoke() {
                DialogManager.showHtmlDialogWithCheck(statement_file,LoginActivity.this,legal);
                return null;
            }
        }).result();
        mStatementNotify.setMovementMethod(LinkMovementMethod.getInstance());
        mStatementNotify.setText(result);
        }catch (Exception e){}
    }
    private void initWrongInputDetect2() {
        OnCheckInputResult onCheckInputResult = new OnCheckInputResultAdapter() {
            @Override
            public boolean onCheckResult(EditText v) {
                return checkLegalState(v);
            }

            @Override
            public void onTextChanged(EditText v, Editable s) {
                super.onTextChanged(v, s);
                checkInputState(v, s);
            }
        };
        editTextGraphicalCode.setOnCheckInputResult(onCheckInputResult);
        defaultTextColor = editTextPhone.getCurrentTextColor();
        editTextSMSCode.setOnCheckInputResult(onCheckInputResult);
        editTextPhone.setOnCheckInputResult(onCheckInputResult);
        idEdittextLoginInviteCode.setOnCheckInputResult(onCheckInputResult);

    }

    public void checkInputState(EditText v, Editable s) {
        if (v == editTextPhone) {
            if (!TextUtils.isEmpty(s)) {
                String currentString = s.toString();
                if ((currentString.startsWith("0") && currentString.length() == 1) || (currentString.startsWith("08") && currentString.length() <= 13) || (currentString.startsWith("8") && currentString.length() <= 12)) {
                    v.setTextColor(defaultTextColor);
                } else {
                    v.setTextColor(mErrorColor);
                }

            }
        } else if (v == editTextSMSCode) {
            if (!TextUtils.isEmpty(s)) {
                if (s.length() <= 6) {
                    v.setTextColor(defaultTextColor);
                } else {
                    v.setTextColor(mErrorColor);
                }

            }
        }
    }

    @Override
    public boolean checkLegalState(EditText v) {
        //检查输入是否正确
        String s = v.getText().toString();
        //        if (v == editTextPhone.getEditText() && (TextUtils.isEmpty(s)  || (!s.matches("08\\d{8,10}") && !s.matches("8\\d{7,9}")))) {
        if (v == editTextPhone && (TextUtils.isEmpty(s) || (!s.matches("8\\d{8,11}") && !s.matches("08\\d{8,11}")))) {
            v.setTextColor(mErrorColor);
            return false;
        }
        if (v == editTextSMSCode && (TextUtils.isEmpty(s) || s.length() != 6)) {
            v.setTextColor(mErrorColor);
            return false;
        }
        return true;

    }


    /**
     * RefreshCaptcha
     */
    public void refreshCaptcha() {
        sid = RandomStringUtils.randomAlphanumeric(5);

        Glide.with(this)
                .load(CaptchaApi.formatCaptchaUrl("" + sid))
                .signature(new StringSignature("" + System.currentTimeMillis()))
                .into(imageViewCode);
        Logger.d("sid:  " + sid);
        //        imageViewCode.setImageURI(Uri.parse(CaptchaApi.formatCaptchaUrl(""+sid)));

    }

    @Override
    public void onLoginError() {
        linearLayoutGraphical.setVisibility(View.VISIBLE);
        refreshCaptcha();
    }

    /**
     * Obtain SMS CODE
     */
    @OnClick(R.id.id_button_obtain_code)
    public void getSMSCode() {
        mPresenter.obtainSmsCode(buttonObtain,editTextPhone);
    }

    /**
     * Obtain Captcha
     */
    @OnClick(R.id.id_imagebutton_refresh)
    public void getCaptcha() {
        UserEventQueue.add(new ClickEvent(imButtonRefresh.toString(), ActionType.CLICK));
        refreshCaptcha();
    }

    /**
     * Login
     */
    @OnClick(R.id.id_button_login)
    public void login() {
        UserEventQueue.add(new ClickEvent(login.toString(), ActionType.CLICK, login.getText().toString()));
        if (checkLegalState(editTextPhone) & checkLegalState(editTextSMSCode)
                & ((linearLayoutGraphical.getVisibility() != View.VISIBLE) || checkLegalState(editTextGraphicalCode))) {

            smsCode = editTextSMSCode.getText().toString().trim();
            captcha = editTextGraphicalCode.getText().toString().trim();
            phoneStr = editTextPhone.getText().toString().trim();


            loginRequestBean.smsCode = new LoginRequestBean.SMSCode("" + smsCode);
            loginRequestBean.mobile = new LoginRequestBean.Mobile("" + phoneStr);
            loginRequestBean.captchaSid = new LoginRequestBean.CaptchaSID("" + sid);
            loginRequestBean.captcha = new LoginRequestBean.Captcha("" + captcha);
            loginRequestBean.inviteCode = idEdittextLoginInviteCode.getText() == null ? "" : idEdittextLoginInviteCode.getText().toString().trim();

            //            if (linearLayoutGraphical.getVisibility() != View.VISIBLE) {
            //                if (StringUtil.isNullOrEmpty(smsCode) || StringUtil.isNullOrEmpty(phoneStr)) {
            //                    Toast.makeText(LoginActivity.this, "Please input the required data", Toast.LENGTH_SHORT).show();
            //                } else {
            //                    doLogin();
            //                }
            //            } else if (StringUtil.isNullOrEmpty(smsCode) || StringUtil.isNullOrEmpty(phoneStr) || StringUtil.isNullOrEmpty(captcha)) {
            //                Toast.makeText(LoginActivity.this, "Please input the required data", Toast.LENGTH_SHORT).show();
            //            } else {
            mPresenter.doLogin(loginRequestBean);
            //            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.get().post(new EventCollection.GiveUpLogin());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}

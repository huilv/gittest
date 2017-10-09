package com.daunkredit.program.sulu.view.me;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.me.presenter.MySettingActPreImp;
import com.daunkredit.program.sulu.view.me.presenter.MySettingActPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.AlertDialogListener;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.hwangjr.rxbus.RxBus;

import butterknife.BindView;

/**
 * @作者:XJY
 * @创建日期: 2017/3/16 10:57
 * @描述:SecuritySetting的实现activity
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MySettingActivity extends BaseActivity<MySettingActPresenter> implements MySettingActView,View.OnClickListener {
    @BindView(R.id.id_imagebutton_back)
    ImageButton    mIdImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView       mIdTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton    mIdImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    RelativeLayout mIdMainTop;
    @BindView(R.id.btn_mysetting_logout)
    Button         mBtnMysettingLogout;
    private UserApi mMeApi;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mysetting;
    }

    @Override
    protected void init() {
        mMeApi = ServiceGenerator.createService(UserApi.class);
        mIdImagebuttonBack.setOnClickListener(this);
        mIdImagebuttonInfoList.setVisibility(View.GONE);
        mIdTextviewTitle.setText("Help Setting");
        mBtnMysettingLogout.setOnClickListener(this);

        RxBus.get().register(this);
    }

    @Override
    protected MySettingActPresenter initPresenterImpl() {
        return new MySettingActPreImp();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_imagebutton_back:
                UserEventQueue.add(new ClickEvent(mIdImagebuttonBack.toString(), ActionType.CLICK, "Back"));
                finish();
                break;
            case R.id.btn_mysetting_logout:
                UserEventQueue.add(new ClickEvent(mBtnMysettingLogout.toString(), ActionType.CLICK, "Logout"));
                doLogoutOrNot();
                break;
            default:
        }
    }

    private void doLogoutOrNot() {
        if (!TokenManager.getInstance().hasLogin()) {
            ToastManager.showToast(getString(R.string.show_not_login_yet));
            finish();
            return;
        }
        PopupWindow dialog = DialogManager.newAlertDialog(this,R.string.dialog_ensure_logout,new AlertDialogListener(){
            @Override
            public void onEnsure() {
                mPresenter.logout();
            }
            @Override
            public void onCancel() {

            }
        });
        DialogManager.show(this,dialog,mBtnMysettingLogout);
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}

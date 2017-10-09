package com.daunkredit.program.sulu.view;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.view.presenter.ContactActPreImp;
import com.daunkredit.program.sulu.view.presenter.ContactActPresenter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Miaoke on 2017/2/22.
 *
 * 搜集信息：
 * 1 联系人信息
 * 2 机型信息即Build信息
 * 3 基站信息
 * 4 ip地址
 * 这些信息聚集在DeviceUuidFactory生成的id下面
 *
 *
 TCP PB的Socket接口是 52.221.217.59:9696
 package com.sulu.harvester.message;

 option java_package = "com.sulu.harvester.message";
 option java_outer_classname = "ImeiMessageProto";

 message Message {

 required string imei = 1;
 required int64 c_timestamp = 2;

 enum Type {
 UNKNOWN = 0;
 TRACE = 1;
 }
 required Type type = 3 [default = TRACE];

 required string body = 4;

 }
 *
 */

public class ContactActivity extends BaseActivity<ContactActPresenter> implements ContactActView /*implements EasyPermissions.PermissionCallbacks*/{
    private static final int RC_CONTACT_PERM = 123;

    @BindView(R.id.id_textview_title) TextView title;
    @BindView(R.id.id_imagebutton_info_list)  ImageButton infoListButton;
    @BindView(R.id.id_edittext_msg)EditText edit;
    @BindString(R.string.textview_customer_service_hotline) String titleStr;



    @Override
    protected int getLayoutResId() {
        return R.layout.activity_service_hotline;
    }

    @Override
    protected void init() {

        edit.setVisibility(View.INVISIBLE);
       mPresenter.requestPermission();
    }

    @Override
    protected ContactActPresenter initPresenterImpl() {
        return new ContactActPreImp();
    }

    public void initView(){
        title.setText(titleStr);
        infoListButton.setVisibility(View.INVISIBLE);

    }

    @OnClick(R.id.id_imagebutton_back)
    public void hotlineBack(){
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        unbindService(conn);
    }
}
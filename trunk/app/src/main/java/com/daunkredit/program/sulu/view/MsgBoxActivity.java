package com.daunkredit.program.sulu.view;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.MsgInboxBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.common.utils.XLeoSp;
import com.daunkredit.program.sulu.view.presenter.MsgBoxActPreImp;
import com.daunkredit.program.sulu.view.presenter.MsgBoxActPresenter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Miaoke on 2017/2/22.
 */

public class MsgBoxActivity extends BaseActivity<MsgBoxActPresenter> implements MsgBoxActView {
    private static final String TAG = "MsgBoxActivity";


    @BindView(R.id.id_listview_system_info)
            ListView          infoListView;
    @BindView(R.id.id_imagebutton_info_list)
            ImageButton       infoListButton;
    @BindView(R.id.id_textview_title)
            TextView          title;
    @BindString(R.string.textview_title_system_message)
            String            titleStr;
    private SysInfoAdapter       adapter;
    private ArrayList<MsgInboxBean> mDatas;
    private String            mToken;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_system_message;
    }

    @Override
    protected void init() {
        initVIew();

        mToken = TokenManager.getInstance().getToken();
        if (TextUtils.isEmpty(mToken)) {
            ToastManager.showToast(getString(R.string.show_not_login_yet));
            return;
        }
        mDatas = new ArrayList<>();
        mPresenter.initData();

    }

    @Override
    protected MsgBoxActPresenter initPresenterImpl() {
        return new MsgBoxActPreImp();
    }


    public void initVIew() {
        title.setText(titleStr);
        infoListButton.setVisibility(View.INVISIBLE);
    }

    private List<Map<String, Object>> getMsg() {
        List<Map<String, Object>> itemList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        // Get data from server side;

        map.put("id_textview_time_text", "2017-01-09 22:22:03");
        map.put("id_textview_info_text", "Hello world test 2...");

        itemList.add(map);
        Logger.d("getMsg: runing ->> getMsg");
        return itemList;
    }

    @OnClick(R.id.id_imagebutton_back)
    public void SysInfoBack() {
        Log.d(TAG, "SysInfoBack: click back.");
        finish();
    }

    public void setMsgChecked(final MsgInboxBean msgChecked) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }else{
//            mDatas
        }
    }

    @Override
    public void initMsgDatas(List<MsgInboxBean> msgInboxBeen) {
        mDatas.clear();
        mDatas.addAll(msgInboxBeen);
        if (mDatas != null && mDatas.size() > 1) {
            String createTime = mDatas.get(0).getCreateTime();
            try {
                XLeoSp.getInstance(MsgBoxActivity.this).putLong(FieldParams.MESSAGE_NUMBER, Long.valueOf(createTime));
            }catch (Exception e){
                LoggerWrapper.e("MsgBoxActivity",e);
            }
        }
        if (adapter == null) {
            adapter = new SysInfoAdapter(MsgBoxActivity.this, mDatas);
            adapter.setOnClickListener(new OnMsgItemClickListener(){

                @Override
                public void onClick(MsgInboxBean msgId) {
                    setMsgChecked(msgId);
                    mPresenter.setMsgChecked(msgId);
                }
            });
            infoListView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateList() {
        adapter.notifyDataSetChanged();
    }
}

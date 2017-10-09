package com.daunkredit.program.sulu.view.me;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.MsgBoxActivity;
import com.daunkredit.program.sulu.view.me.presenter.MyInvitedInfoActActPreImpl;
import com.daunkredit.program.sulu.view.me.presenter.MyInvitedInfoActPresenter;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.sulu.kotlin.data.InviteResult;
import com.sulu.kotlin.data.InviteePersonBean;
import com.x.leo.listexpend.AutoRollListView;
import com.x.leo.listexpend.CycleAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @作者:My
 * @创建日期: 2017/3/23 15:23
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class MyInvitedInfoActivity extends BaseActivity<MyInvitedInfoActPresenter> implements MyInvitedInfoActView, View.OnClickListener {

    @BindView(R.id.id_imagebutton_back)
    ImageButton idImagebuttonBack;
    @BindView(R.id.id_textview_title)
    TextView idTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton idImagebuttonInfoList;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.btn_copy)
    Button btnCopy;
    @BindView(R.id.tv_invited)
    TextView tvInvited;
    @BindView(R.id.tv_applied)
    TextView tvApplied;
    @BindView(R.id.list_content)
    AutoRollListView listContent;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_invite;
    }

    @Override
    protected void init() {
        idImagebuttonBack.setOnClickListener(this);
        idImagebuttonInfoList.setOnClickListener(this);
        idTextviewTitle.setText(R.string.undang_teman);
        mPresenter.obtainCode();
        btnCopy.setOnClickListener(this);
    }

    @Override
    protected MyInvitedInfoActPresenter initPresenterImpl() {
        return new MyInvitedInfoActActPreImpl();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_imagebutton_back:
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, "Back"));
                finish();
                break;
            case R.id.id_imagebutton_info_list:
                startActivity(new Intent(this, MsgBoxActivity.class));
                break;
            case R.id.btn_copy:
                addToClipBoard(tvCode.getText());
                break;
        }
    }

    private void addToClipBoard(CharSequence s) {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text/plain", s);
        clipBoard.setPrimaryClip(clipData);
        XLeoToast.showMessage(R.string.show_has_copyed_to_clip_board);
    }

    @Override
    public void onCodeObtain(String s) {
        tvCode.setText(s);
    }

    @Override
    public void onInviteResultObtain(InviteResult inviteResult) {
        tvCode.setText(inviteResult.getCode());
        tvApplied.setText("已申请" + inviteResult.getBean().getCompleteLoanApplyCount() + "个");
        tvInvited.setText("已邀请" + inviteResult.getBean().getInviteeCount() + "个");
        initList(inviteResult.getList());
    }

    private void initList(final ArrayList<InviteePersonBean> list) {
        if (list == null) {
            return;
        }
        listContent.setAdapter(new CycleAdapter() {
            @Override
            public int getRealCount() {
                return list.size();
            }

            @NotNull
            @Override
            public InviteePersonBean getRealItem(int position) {
                return list.get(position);
            }

            @NotNull
            @Override
            public View getRealView(int i, View convertView, ViewGroup parent) {
                Holder holder = getHolder(convertView);
                holder.inject(getRealItem(i));
                return holder.itemView;
            }

            private Holder getHolder(View convertView) {
                if (convertView == null) {
                    convertView = View.inflate(MyInvitedInfoActivity.this.getApplicationContext(),R.layout.item_inviteinfo_list,null);
                    Holder holder = new Holder(convertView);
                    convertView.setTag(holder);
                    return holder;
                }else{
                    return (Holder) convertView.getTag();
                }
            }
            class Holder{
                View itemView;
                TextView leftView;
                TextView middleView;
                TextView rightView;
                public Holder(View convertView) {
                   itemView = convertView;
                    leftView = (TextView) convertView.findViewById(R.id.left_text);
                    middleView = (TextView)convertView.findViewById(R.id.middle_text);
                    rightView = (TextView) convertView.findViewById(R.id.right_text);
                }

                public void inject(InviteePersonBean item) {
                    leftView.setText(item.getMobile());
                    middleView.setText(item.getRealName());
                    rightView.setText(TimeFormat.convertTimeDay(item.getRegisterTime()));
                }
            }
        });
    }


}

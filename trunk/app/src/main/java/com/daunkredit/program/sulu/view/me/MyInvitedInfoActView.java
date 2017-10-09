package com.daunkredit.program.sulu.view.me;

import com.daunkredit.program.sulu.app.base.BaseActivityView;
import com.sulu.kotlin.data.InviteResult;

/**
 * @作者:My
 * @创建日期: 2017/6/21 17:40
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface MyInvitedInfoActView extends BaseActivityView{
    void onCodeObtain(String s);

    void onInviteResultObtain(InviteResult inviteResult);
}

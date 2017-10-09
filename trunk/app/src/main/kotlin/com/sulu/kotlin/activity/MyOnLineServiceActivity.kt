package com.sulu.kotlin.activity

import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.utils.tokenInstance

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 17:58
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class MyOnLineServiceActivity:BaseActivity<MyOnlineServicePresenter>(),MyOnlineServiceView{
    override fun getLayoutResId(): Int {
        return R.layout.activity_my_online_service
    }

    override fun init() {
        if (!tokenInstance.hasLogin()) {
            XLeoToast.showMessage(R.string.show_not_login_yet)
            finish()
        }
    }

    override fun initPresenterImpl(): MyOnlineServicePresenter {
        return MyOnlineServicePresenterImpl()
    }

}

class MyOnlineServicePresenterImpl : MyOnlineServicePresenter, BaseActivityPresenterImpl() {

}

interface MyOnlineServiceView {

}

interface MyOnlineServicePresenter:BaseActivityPresenter {

}

package com.sulu.kotlin.activity

import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.view.MsgBoxActivity
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/16 15:03
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class ActivityDetailAcivity:BaseActivity<ActivityDetailPresenter>(),ActivityDetailView{
    override fun getLayoutResId(): Int {
        return R.layout.activity_activity_detail
    }

    override fun init() {
        initHeader()
//        val url = intent.getStringExtra(FieldParams.ACTIVITY_DETAIL_IMAGE_URL)
//        Glide.with(context).load(url).into(find<ImageView>(R.id.iv_activity_detail))
    }

    private fun initHeader() {
        find<ImageButton>(R.id.id_imagebutton_back).onClick {
            finish()
        }
        find<ImageButton>(R.id.id_imagebutton_info_list).onClick {
            startActivity(Intent(applicationContext,MsgBoxActivity::class.java))
        }
        find<TextView>(R.id.id_textview_title).text = getString(R.string.title_activity_detail)
    }

    override fun initPresenterImpl(): ActivityDetailPresenter {
        return ActivityDetailPresenterImpl()
    }

}

class ActivityDetailPresenterImpl : ActivityDetailPresenter,BaseActivityPresenterImpl() {


}

interface ActivityDetailView {

}

interface ActivityDetailPresenter:BaseActivityPresenter {

}

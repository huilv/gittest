package com.sulu.kotlin.activity

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.widget.ImageButton
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.view.MsgBoxActivity
import com.sulu.kotlin.adapter.CouponAdapter
import com.sulu.kotlin.fragment.CouponFragment
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 17:30
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class MyCouponActivity:BaseActivity<MyCouponPresenter>(),MyCouponView{
    lateinit var vp:ViewPager
    override fun getLayoutResId(): Int {
        return R.layout.activity_my_coupon
    }

    override fun init() {
        find<ImageButton>(R.id.id_imagebutton_back).onClick { finish() }
        find<TextView>(R.id.id_textview_title).text = getString(R.string.title_coupon)
        find<ImageButton>(R.id.id_imagebutton_info_list).onClick { startActivity(Intent(applicationContext,MsgBoxActivity::class.java)) }
        vp = find(R.id.vp_coupon)
        val fragments = arrayOf(CouponFragment(1), CouponFragment(2), CouponFragment(3))
        val titles = arrayOf(getString(R.string.page_title_coupon1),getString(R.string.page_title_coupon2),getString(R.string.page_title_coupon3))
        find<TabLayout>(R.id.tl_coupon).setupWithViewPager(vp)
        vp.adapter = CouponAdapter(fragments,titles,supportFragmentManager)
    }

    override fun initPresenterImpl(): MyCouponPresenter{
        return MyCouponPresenterImpl()
    }

}

class MyCouponPresenterImpl : BaseActivityPresenterImpl(),MyCouponPresenter {

}

interface MyCouponView {

}

interface MyCouponPresenter:BaseActivityPresenter {

}

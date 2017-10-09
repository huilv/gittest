package com.sulu.kotlin.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.common.utils.TimeFormat
import com.daunkredit.program.sulu.common.utils.XLeoSp
import com.daunkredit.program.sulu.view.MainActivity
import com.daunkredit.program.sulu.view.MsgBoxActivity
import com.sulu.kotlin.data.CouponBean
import com.sulu.kotlin.utils.tokenInstance
import kotlinx.android.synthetic.main.activity_coupon_detail.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/30 16:01
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class CouponDetailActivity : BaseActivity<CouponDetailPresenter>(), CouponDetailView {
    override fun getLayoutResId(): Int {
        return R.layout.activity_coupon_detail
    }

    override fun init() {
        initHeader()
        val andRemove = tokenInstance.getAndRemove(FieldParams.COUPON_TO_SHOW) as CouponBean
        initView(andRemove)
    }

    private fun initView(andRemove: CouponBean) {
        tv_desc.text = getString(R.string.text_coupon_desc) + andRemove.description
        tv_period.text = ("" + andRemove.dischargeInterestDay + getString(R.string.days))
        tv_title.text = andRemove.title
        tv_source.text = "Kenalkan teman"
        tv_sum.text = "-" + andRemove.dischargeInterestDay * XLeoSp.getInstance(context).getDouble(FieldParams.CURRENT_LOAN_RATE,0.01)
        tv_usable.text = if (andRemove.used) {
            getString(R.string.page_title_coupon2)
        }else{
            getString(R.string.page_title_coupon1)
        }
        tv_usable_data.text = TimeFormat.convertTimeDay(andRemove.validBeginTime) + " " + getString(R.string.to) + " " +
                TimeFormat.convertTimeDay(andRemove.validEndTime)
        if (!andRemove.used) {
            btn_use_coupon.visibility = View.VISIBLE
            btn_use_coupon.onClick {
                val intent = Intent(this@CouponDetailActivity, MainActivity::class.java)
                intent.putExtra(FieldParams.MainActivity.TAB_CHOOSE,0)
                startActivity(intent)
            }
        }else{
            btn_use_coupon.visibility = View.GONE
        }
    }

    private fun initHeader() {
        find<ImageView>(R.id.id_imagebutton_back).onClick {
            finish()
        }
        find<ImageView>(R.id.id_imagebutton_info_list).onClick {
            startActivity(Intent(applicationContext, MsgBoxActivity::class.java))
        }
        find<TextView>(R.id.id_textview_title).text = getString(R.string.title_coupon_detail)
    }

    override fun initPresenterImpl(): CouponDetailPresenter {
        return CouponDetailPreImpl()
    }

}

class CouponDetailPreImpl : CouponDetailPresenter, BaseActivityPresenterImpl() {

}

interface CouponDetailPresenter : BaseActivityPresenter {

}

interface CouponDetailView {

}

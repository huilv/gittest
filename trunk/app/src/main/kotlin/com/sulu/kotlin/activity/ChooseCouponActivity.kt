package com.sulu.kotlin.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.view.MsgBoxActivity
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.adapter.CouponRecycleViewAdapter
import com.sulu.kotlin.adapter.OnItemClickListener
import com.sulu.kotlin.data.CouponBean
import com.sulu.kotlin.utils.tokenInstance
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/16 14:59
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class ChooseCouponActivity:BaseActivity<ChooseCouponPresenter>(),ChooseCouponView{
    override fun onCouponObtain(datas: ArrayList<CouponBean>) {
        val couponRecycleViewAdapter = CouponRecycleViewAdapter(datas)
        couponRecycleViewAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View, couponBean: CouponBean) {
                TokenManager.putMessage(FieldParams.COUPON_CHOOSED_NAME,couponBean)
                setResult(Activity.RESULT_OK)
                finish()
            }

        })
        rv.adapter = couponRecycleViewAdapter
    }

    lateinit var rv:RecyclerView
    override fun getLayoutResId(): Int {
        return R.layout.activity_choose_coupon
    }

    override fun init() {
        initHeader()
        rv = find<RecyclerView>(R.id.rv_choose_coupon)
        rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mPresenter.obtainUseableCoupon()
    }

    fun initHeader() {
        find<ImageView>(R.id.id_imagebutton_back).onClick {
            finish()
        }
        find<ImageView>(R.id.id_imagebutton_info_list).onClick {
            startActivity(Intent(applicationContext,MsgBoxActivity::class.java))
        }
        find<TextView>(R.id.id_textview_title).text = getString(R.string.title_choose_coupon)
    }

    override fun initPresenterImpl(): ChooseCouponPresenter {
        return ChooseCouponPresenterImpl()
    }

}

class ChooseCouponPresenterImpl : ChooseCouponPresenter , BaseActivityPresenterImpl(){
    override fun obtainUseableCoupon() {
        if (isAttached) {
            mView.baseActivity.showLoading(null)
        }
        mUserApi.getAvailableCoupon(tokenInstance.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CouponBean>>() {
                    override fun onNext(t: List<CouponBean>?) {
                        if (isAttached) {
                            mView.baseActivity.dismissLoading()
                            (mView as ChooseCouponView).onCouponObtain(t as ArrayList<CouponBean>)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (isAttached()) {
                            XLeoToast.showMessage(R.string.show_netwok_wrong)
                            mView.baseActivity.dismissLoading()
                        }
                    }

                    override fun onCompleted() {
                    }

                })

    }

}

interface ChooseCouponView {
    fun onCouponObtain(datas: ArrayList<CouponBean>)

}

interface ChooseCouponPresenter:BaseActivityPresenter {
    fun obtainUseableCoupon()

}

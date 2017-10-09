package com.sulu.kotlin.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.activity.CouponDetailActivity
import com.sulu.kotlin.adapter.CouponRecycleViewAdapter
import com.sulu.kotlin.adapter.OnItemClickListener
import com.sulu.kotlin.data.CouponBean
import com.sulu.kotlin.utils.tokenInstance
import org.jetbrains.anko.find
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/15 15:03
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class CouponFragment() : BaseFragment<CouponFragmentPresenter>(), CouponFragmentView {
    override fun onDataObtain(datas: ArrayList<CouponBean>) {
        mdatas.clear()
        mdatas.addAll(datas)
        rv.adapter.notifyDataSetChanged()
    }

    private var type: Int? = null

    constructor(type: Int) : this() {
        this.type = type
    }

    lateinit var rv: RecyclerView
    override fun doPreBuildHeader(): Boolean {
        return false
    }

    override fun initPresenter(): CouponFragmentPresenter {
        return CouponFragmentPresenterImpl()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_coupon
    }

    val mdatas: ArrayList<CouponBean> by lazy {
        ArrayList<CouponBean>()
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        rv = view!!.find(R.id.rv_coupon)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = CouponRecycleViewAdapter(mdatas)
        (rv.adapter as CouponRecycleViewAdapter).setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View, couponBean: CouponBean) {
                tokenInstance.storeMessage(FieldParams.COUPON_TO_SHOW,couponBean)
                startActivity(Intent(context, CouponDetailActivity::class.java))
            }

        })
    }

    override fun initData() {
        mPresenter.obtainDatas(if (type == null) {
            0
        } else {
            type!!
        })
    }

}

class CouponFragmentPresenterImpl : CouponFragmentPresenter, BaseFragmentPresenterImpl() {
    override fun obtainDatas(type: Int) {
        val subscriber = object : Subscriber<List<CouponBean>>() {
            override fun onNext(t: List<CouponBean>?) {
                if (isAttached) {
                    mView.baseActivity.dismissLoading()
                    (mView as CouponFragmentView).onDataObtain(t as ArrayList<CouponBean>)
                }
            }

            override fun onCompleted() {

            }

            override fun onError(e: Throwable?) {
                if (isAttached) {
                    mView.baseActivity.dismissLoading()
                    XLeoToast.showMessage(R.string.show_netwok_wrong)
                }
            }

        }
        when (type) {
            1 -> {
                showLoading(null)
                mUserApi.getAvailableCoupon(tokenInstance.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber)
            }
            2 -> {
                showLoading(null)
                mUserApi.getUsedCoupon(tokenInstance.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber)
            }
            3 -> {
                showLoading(null)
                mUserApi.getOutdatedCoupon(tokenInstance.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber)

            }
            else -> {
                throw IllegalArgumentException("wrong type")
            }
        }

    }

}

interface CouponFragmentView {
    fun onDataObtain(datas: ArrayList<CouponBean>)

}

interface CouponFragmentPresenter : BaseFragmentPresenter {
    fun obtainDatas(type: Int)

}

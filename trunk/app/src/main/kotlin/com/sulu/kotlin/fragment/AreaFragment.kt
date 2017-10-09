package com.sulu.kotlin.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl
import com.daunkredit.program.sulu.bean.RegionBean
import com.daunkredit.program.sulu.view.certification.status.RegionLevel
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.adapter.AreaAdapter
import org.jetbrains.anko.find
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/14 11:16
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class AreaFragment() : BaseFragment<AreaPresenter>(), AreaView {
    lateinit var rvArea: RecyclerView
    lateinit var regionLevel: RegionLevel
    private val mDatas: ArrayList<RegionBean.RegionsBean> by lazy {
        ArrayList<RegionBean.RegionsBean>()
    }

    override fun doPreBuildHeader(): Boolean {
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
    }

    fun obtainData(){
        Log.d(TAG, "initdata:" + regionLevel.toString())
        if (arguments != null || regionLevel == RegionLevel.province) {
            val id: Int = if (regionLevel == RegionLevel.province) {
                1
            } else {
                arguments.getInt(FieldParams.AREA_UPPER_AREA_ID)
            }
            mPresenter.obtainAreaDatas(regionLevel, id)
        }
    }

    override fun initHeader(view: TextView?): Boolean {
        when (regionLevel) {
            RegionLevel.province -> {
                view?.text = getString(R.string.textview_personal_info_province_of_residence)
            }
            RegionLevel.city->{
                view?.text = getString(R.string.textview_personal_info_city_of_residence)
            }
            RegionLevel.district->{
                view?.text = getString(R.string.textview_personal_info_street_of_residence)
            }
            RegionLevel.area->{
                view?.text = getString(R.string.textview_personal_info_area_of_residence)
            }
            else->{
                throw IllegalArgumentException("regionlevel wrong")
            }
        }

        return false
    }

    override fun getBackPressListener(): View.OnClickListener {
        return View.OnClickListener {
            (mActivity as AreaActivityInterface).returnLastFragment()
        }
    }


    constructor(region: RegionLevel) : this() {
        regionLevel = region
    }

    override fun initPresenter(): AreaPresenter {
        Log.d(TAG,"initPreseneter:" + regionLevel.toString())
        return AreaPresenterImpl()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_area
    }

    private val TAG: String = "AreaFragment"

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        rvArea = view!!.find(R.id.rv_area)
        rvArea.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvArea.adapter = AreaAdapter(mDatas, mActivity = mActivity as AreaActivityInterface)
        Log.d(TAG, "initview:" + regionLevel.toString())
    }

    override fun initData() {
    }


    override fun onObtainDatas(regionBean: RegionBean) {
        mDatas.clear()
        mDatas.addAll(regionBean.regions)
        rvArea.adapter.notifyDataSetChanged()
    }

}

interface AreaActivityInterface {
    fun forwardToNextFragment(regionsBean: RegionBean.RegionsBean?)
    fun returnLastFragment()

}

class AreaPresenterImpl : BaseFragmentPresenterImpl(), AreaPresenter {
    override fun obtainAreaDatas(regionLevel: RegionLevel, id: Int) {
        Log.d("AreaFragment", "obtainDatas:" + regionLevel)
        if (isAttached) {
            mView.baseActivity.showLoading(null)
        }
        mUserApi.getRegion(regionLevel.toString(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<RegionBean>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        XLeoToast.showMessage(R.string.show_get_region_faild)
                        if (isAttached) {
                            mView.baseActivity.dismissLoading()
                        }
                    }

                    override fun onNext(regionBean: RegionBean) {
                        if (isAttached) {
                            (mView as AreaView).onObtainDatas(regionBean)
                            mView.baseActivity.dismissLoading()
                        }
                    }
                })
    }
}

interface AreaView {
    fun onObtainDatas(regionBean: RegionBean)
}

interface AreaPresenter : BaseFragmentPresenter {
    fun obtainAreaDatas(regionLevel: RegionLevel, id: Int)
}

package com.sulu.kotlin.activity


import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.view.MsgBoxActivity
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.adapter.ActivityCenterAdapter
import com.sulu.kotlin.data.ActivityInfoBean
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 17:37
 * @描述:${TODO}
 *
 * @更新者:${Author}
 * @更新时间:${Date}
 * @更新描述:${TODO}
 * @下一步：
 */
class MyActivityCenterActivity : BaseActivity<MyActivityCenterPresenter>(), MyActivityCenterView {


    lateinit var rv:RecyclerView
    val mDatas by lazy {
        ArrayList<ActivityInfoBean>()
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_my_activity_center
    }

    override fun init() {
        findView()
        rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        rv.adapter = ActivityCenterAdapter(mDatas)
        mPresenter.requestLatestData()
    }

    override fun onActivityObtain(t: ArrayList<ActivityInfoBean>?) {
        if (t != null) {
            mDatas.clear()
            mDatas.addAll(t)
            rv.adapter.notifyDataSetChanged()
        }
    }

    private fun findView() {
        find<ImageButton>(R.id.id_imagebutton_back).onClick { finish() }
        find<ImageButton>(R.id.id_imagebutton_info_list).onClick {
            val intent = Intent(applicationContext,MsgBoxActivity::class.java)
            startActivity(intent) }
        find<TextView>(R.id.id_textview_title).setText(R.string.title_activity_center)
        rv = find(R.id.rv_activity_center)
    }

    override fun initPresenterImpl(): MyActivityCenterPresenter {
        return MyActivityCenterPresenterImpl()
    }


}

class MyActivityCenterPresenterImpl : MyActivityCenterPresenter, BaseActivityPresenterImpl() {
    override fun requestLatestData() {
        showLoading(null)
        mUserApi.getActivityList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :Subscriber<ArrayList<ActivityInfoBean>>(){
                    override fun onNext(t: ArrayList<ActivityInfoBean>?) {
                        if (isAttached) {
                            dismissLoading()
                            (mView as MyActivityCenterView).onActivityObtain(t)
                        }
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                        if (isAttached) {
                            dismissLoading()
                            XLeoToast.showMessage(R.string.show_netwok_wrong)
                        }
                    }
                })
    }

}

interface MyActivityCenterView {
    fun onActivityObtain(t: ArrayList<ActivityInfoBean>?)

}

interface MyActivityCenterPresenter : BaseActivityPresenter {
    fun requestLatestData()

}

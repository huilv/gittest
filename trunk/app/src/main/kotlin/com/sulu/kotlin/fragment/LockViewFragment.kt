package com.sulu.kotlin.fragment

import android.os.Bundle
import android.view.View
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import org.jetbrains.anko.find

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 9:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class LockViewFragment : BaseFragment<LockViewPresenter>(), LockView {
    private lateinit var mLockView: PatternLockView
    private var isLogin: Boolean = true
    private val STEP_SIZE = 3
    override fun initPresenter(): LockViewPresenter {
        return LockViewPresenterImpl()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_lock_view
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        val get = arguments?.get(FieldParams.LockViewMode)
        if (get != null) {
            isLogin = get as Boolean
        }

        mLockView = view!!.find<PatternLockView>(R.id.plv)
        mLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {

                if (pattern!!.size < 6) {
                    mLockView.clearPattern()
                    XLeoToast.showMessage(R.string.toast_input_password_longger_than_five)
                } else {
                    var password = StringBuffer()
                    pattern.forEach {
                        password.append(it.column + 1 + it.row * STEP_SIZE)
                    }
                    when (isLogin) {
                        true -> {
                            mPresenter.toCheckLogin(password.toString(), fun(result: Boolean) {
                                if (result) {

                                } else {
                                    mLockView.clearPattern()
                                }
                            })
                        }
                        else -> {
                            mPresenter.setupPassword(password.toString())
                        }
                    }
                }

            }

            override fun onCleared() {
            }

            override fun onStarted() {
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }

        })
    }

    override fun initData() {
    }

}


/**
 * view
 */
interface LockView {

}

/**
 * presenter
 */
interface LockViewPresenter : BaseFragmentPresenter {
    fun toCheckLogin(password: String, function: (Boolean) -> Unit)
    fun setupPassword(password: String)
}

class LockViewPresenterImpl : LockViewPresenter, BaseFragmentPresenterImpl() {
    override fun setupPassword(password: String) {
        XLeoToast.showMessage("password:" + password)
    }

    override fun toCheckLogin(password: String, function: (Boolean) -> Unit) {
        XLeoToast.showMessage("password:" + password)
        function.invoke(false)
    }

}
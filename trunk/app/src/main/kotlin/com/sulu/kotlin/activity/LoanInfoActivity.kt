package com.sulu.kotlin.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean
import com.daunkredit.program.sulu.common.EventCollection
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.common.utils.StringFormatUtils
import com.daunkredit.program.sulu.common.utils.TimeFormat
import com.daunkredit.program.sulu.view.MsgBoxActivity
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.hwangjr.rxbus.RxBus
import com.sulu.kotlin.adapter.LoanInfoAdapter
import com.sulu.kotlin.utils.tokenInstance
import okhttp3.ResponseBody
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/21 10:19
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class LoanInfoActivity : BaseActivity<LoanInfoPresenter>(), LoanInfoView {
    override fun onCancelFailed(e: Throwable?) {
        XLeoToast.showMessage(R.string.show_loan_cancel_fail)
    }

    override fun onCancelSuccess() {
        XLeoToast.showMessage(R.string.show_loan_cancel_success)
        finish()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_loan_info
    }

    lateinit var cancelButton:Button
    lateinit var stateList:RecyclerView

    override fun init() {
        initHeader()
        findView()
        initWithData()
}

    private fun initHeader() {
        find<ImageButton>(R.id.id_imagebutton_back).onClick {
            finish()
        }
        find<ImageButton>(R.id.id_imagebutton_info_list).onClick {
            startActivity(Intent(applicationContext, MsgBoxActivity::class.java))
        }
        find<TextView>(R.id.id_textview_title).text = getString(R.string.title_loan_info)
    }

    private fun findView() {
        cancelButton = find(R.id.btn_cancel_loan)
        stateList = find(R.id.rv_loan_info)
    }

    private fun initWithData() {
        val historyBean = TokenManager.checkoutMessage(FieldParams.SHOW_MY_LOAN_INFO, HistoryLoanAppInfoBean::class.java)
        if (historyBean != null) {
            if (TextUtils.equals(FieldParams.LoanStatus.WITHDRAWN, historyBean.status)) {
                hideCancelButton()
            } else if (TextUtils.equals(FieldParams.LoanStatus.SUBMITTED, historyBean.status)) {
                hideCancelButton()
            } else if (TextUtils.equals(FieldParams.LoanStatus.PAID_OFF, historyBean.status)) {
                hideCancelButton()
            } else {
                cancelButton.onClick {
                    mPresenter.cancleLoan(historyBean.loanAppId)
                }
            }
            stateList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            LinearSnapHelper().attachToRecyclerView(stateList)
            stateList.adapter = LoanInfoAdapter(historyBean)
            initTexts(historyBean!!)
        }
    }

    /**
     *  @BindView(R.id.id_textview_process_loan_app_id)
    TextView idTextviewProcessLoanAppId;
    @BindView(R.id.id_textview_process_loan_app_time)
    TextView idTextviewProcessLoanAppTime;
    @BindView(R.id.id_textview_process_loan_app_amount)
    TextView idTextviewProcessLoanAppAmount;
    @BindView(R.id.id_textview_process_loan_app_period)
    TextView idTextviewProcessLoanAppPeriod;
    @BindView(R.id.id_textview_process_loan_app_cost)
    TextView idTextviewProcessLoanAppCost;
    @BindView(R.id.id_textview_process_loan_total_amount)
    TextView idTextviewProcessLoanTotalAmount;
    @BindView(R.id.id_textview_process_loan_app_bank_code)
    TextView idTextviewProcessLoanAppBankCode;
    @BindView(R.id.id_textview_process_loan_app_bank_no)
    TextView idTextviewProcessLoanAppBankNo;
    @BindView(R.id.id_textview_process_loan_app_ktp)
    TextView idTextviewProcessLoanAppKtp;
     */
    private fun initTexts(historyBean: HistoryLoanAppInfoBean) {
        find<TextView>(R.id.tv_loan_amount).text = StringFormatUtils.indMoneyFormat(historyBean.amount)
        find<TextView>(R.id.tv_loan_period).text = ""+historyBean.period + getString(R.string.days)
        find<TextView>(R.id.id_textview_process_loan_app_id).text = "" + historyBean.loanAppId
        find<TextView>(R.id.id_textview_process_loan_app_time).text = "" + TimeFormat.convertTime(historyBean.createTime)
        find<TextView>(R.id.id_textview_process_loan_app_cost).text = "" + StringFormatUtils.indMoneyFormat(historyBean.cost)
        find<TextView>(R.id.id_textview_process_loan_total_amount).text = "" +StringFormatUtils.indMoneyFormat(historyBean.totalAmount)
        find<TextView>(R.id.id_textview_process_loan_app_bank_code).text = "" + historyBean.bankCode
        find<TextView>(R.id.id_textview_process_loan_app_bank_no).text = "" + historyBean.cardNo
        find<TextView>(R.id.id_textview_process_loan_app_ktp).text = "" + historyBean.credentialNo
        find<TextView>(R.id.id_textview_process_loan_app_amount).text = "" + StringFormatUtils.indMoneyFormat(historyBean.amount)
        find<TextView>(R.id.id_textview_process_loan_app_period).text = "" + historyBean.period + getString(R.string.days)
        find<Button>(R.id.btn_read_agreement).onClick {
            showAgreement()
        }
    }

    private val statement_file = "needcopy/loan_agreement.html"

    private fun showAgreement() {
        val title:String = getString(R.string.title_loan_agreement_title)
        DialogManager.showHtmlDialogWithCheck(statement_file,context,title)
    }


    private fun hideCancelButton() {
        cancelButton.visibility = View.GONE
    }

    override fun initPresenterImpl(): LoanInfoPresenter {
    return LoanInfoPresenterImpl()
}
}

class LoanInfoPresenterImpl : LoanInfoPresenter, BaseActivityPresenterImpl() {
    override fun cancleLoan(loanAppId: String) {
        showLoading(null)
        mUserApi.cancelLoan(loanAppId, tokenInstance.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onNext(t: ResponseBody?) {
                        dismissLoading()
                        RxBus.get().post(EventCollection.CancelLoan())
                        if (isAttached) {
                            (mView as LoanInfoView).onCancelSuccess()
                        }
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                        dismissLoading()
                        if (isAttached) {
                            (mView as LoanInfoView).onCancelFailed(e)
                        }
                    }
                })
    }

}

interface LoanInfoPresenter : BaseActivityPresenter {
    fun cancleLoan(loanAppId: String)

}

interface LoanInfoView {
    fun onCancelSuccess()
    fun onCancelFailed(e: Throwable?)

}

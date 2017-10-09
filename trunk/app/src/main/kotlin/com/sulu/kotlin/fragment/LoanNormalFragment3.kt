package com.sulu.kotlin.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.bean.LatestLoanAppBean
import com.daunkredit.program.sulu.common.utils.StringFormatUtils
import com.daunkredit.program.sulu.view.fragment.LoanNormalView
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPreImp
import com.daunkredit.program.sulu.view.fragment.presenter.LoanNormalFraPresenter
import com.sulu.kotlin.data.LoanRange
import com.sulu.kotlin.utils.tokenInstance
import com.x.leo.slidebar.OnSlideDrag
import com.x.leo.slidebar.SlideBarWithText
import kotlinx.android.synthetic.main.fragment_loan_normal_2.*

/**
 * @作者:My
 * @创建日期: 2017/7/13 17:51
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
internal class LoanNormalFragment3 : BaseFragment<LoanNormalFraPresenter>(), LoanNormalView {
    override fun onSpanObtain(loanRange: LoanRange?) {
    }


    lateinit var sbMoney:SlideBarWithText
    lateinit var sbTerm:SlideBarWithText
    override fun initPresenter(): LoanNormalFraPresenter {
        return LoanNormalFraPreImp()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_loan_normal_2 //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        sbMoney = view!!.findViewById(R.id.sb_money) as SlideBarWithText

        val moneyConvertor: SlideBarWithText.ValueConvertor = object : SlideBarWithText.ValueConvertor {
            override fun convertorValue(value: Double): String {
                return StringFormatUtils.indMoneyFormat(value)
            }

            override fun ValueFromPercent(progress: Int): Double {
                return progress * (sb_money.maximum - sb_money.minimum) / 100 + sb_money.minimum
            }

        }
        sbMoney.setValueConvertor(moneyConvertor)
        sbMoney.maximum = 20000000.0
        sbMoney.minimum = 6000000.0

        sbMoney.setOnDragCallBack(object : OnSlideDrag{
            override fun onDragStart(v: View?) {

            }

            override fun onDraging(v: View?, percent: Int) {
                (v as TextView)!!.text = moneyConvertor.convertorValue(moneyConvertor.ValueFromPercent(percent))
            }

            override fun onDragEnd(v: View?, isComplete: Boolean) {
            }

        })
        sbTerm = view!!.findViewById(R.id.sb_term) as SlideBarWithText

        val termConvertor: SlideBarWithText.ValueConvertor = object : SlideBarWithText.ValueConvertor {
            override fun convertorValue(value: Double): String {
                return ""+ value  + getString(R.string.days)
            }

            override fun ValueFromPercent(progress: Int): Double {
                return progress * (sbTerm.maximum - sbTerm.minimum) / 100 + sbTerm.minimum
            }

        }
        sbTerm.setValueConvertor(termConvertor)
        sbTerm.maximum = 14.0
        sbTerm.minimum = 7.0
        sbTerm.setOnDragCallBack(object : OnSlideDrag{
            override fun onDragStart(v: View?) {

            }

            override fun onDraging(v: View?, percent: Int) {
                (v as TextView)!!.text = termConvertor.convertorValue(termConvertor.ValueFromPercent(percent))
            }

            override fun onDragEnd(v: View?, isComplete: Boolean) {
            }

        })
        initButtonState()
//        sb_money.maximum = 6000000.0
//        sb_term.maximum = 14.0
//        sb_money.minimum = 3000000.0
//        sb_term.minimum = 7.0
//        sb_money.setValueConvertor(object : SlideBarWithText.ValueConvertor {
//            override fun convertorValue(value: Double): String {
//                return "RP." + value
//            }
//
//            override fun ValueFromPercent(progress: Int): Double {
//                return progress * (sb_money.maximum - sb_money.minimum) / 100 + sb_money.minimum
//            }
//
//        })

    }

    private fun initButtonState() {
        val message = tokenInstance.getMessage(FieldParams.LATESTBEAN) as LatestLoanAppBean
        if (message.status == FieldParams.LoanStatus.SUBMITTED) {
            sbMoney.setValueProgress( message.amount)
            sbTerm.setValueProgress(message.period as Double)
        }else{
            sbMoney.setPercentProgress(0)
            sbTerm.setPercentProgress(0)
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initButtonState()
        }
    }

    override fun initData() {
    }

}
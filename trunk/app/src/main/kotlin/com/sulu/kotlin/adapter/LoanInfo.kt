package com.sulu.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean
import com.daunkredit.program.sulu.bean.LoanAppBeanFather
import com.x.leo.timelineview.TimeLineView
import org.jetbrains.anko.find

/**
 * @作者:XLEO
 * @创建日期: 2017/8/22 14:44
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class LoanInfoAdapter(val data: HistoryLoanAppInfoBean) : RecyclerView.Adapter<LoanInfoHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LoanInfoHolder {
        var view: View?
        if (viewType == 0) {
            view = View.inflate(parent!!.context, R.layout.item_loan_info_bottom, null)
        } else {
            view = View.inflate(parent!!.context, R.layout.item_loan_info_top, null)
        }
        return LoanInfoHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount(): Int {
        return data.statusLogs.size
    }

    override fun onBindViewHolder(holder: LoanInfoHolder?, position: Int) {
        val size = data.statusLogs.size
        val relPos = size - position - 1
        holder?.injectView(data.statusLogs[relPos], relPos, size)
    }

}


class LoanInfoHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun injectView(statusLogsBean: LoanAppBeanFather.StatusLogsBean?, position: Int, size: Int) {
        itemView.find<TextView>(R.id.tv_status).text = statusLogsBean?.status
        val tlv = itemView.find<TimeLineView>(R.id.tlv_loan_info)
        when (position) {
            size - 1 -> {
                tlv.setMarkPosition(TimeLineView.POINT_START)
                tlv.currentStatus = TimeLineView.COMPLETE
            }
            0 -> {
                tlv.setMarkPosition(TimeLineView.POINT_END)
                tlv.currentStatus = TimeLineView.ACTIVE
            }
            else -> {
                tlv.setMarkPosition(TimeLineView.POINT_MIDDLE)

                tlv.currentStatus = TimeLineView.COMPLETE
            }
        }


    }
}


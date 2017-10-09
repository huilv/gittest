package com.sulu.kotlin.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.common.utils.StringFormatUtils
import com.daunkredit.program.sulu.common.utils.TimeFormat
import com.sulu.kotlin.data.CouponBean
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/15 14:54
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class CouponAdapter(val fragments: Array<out Fragment>, val titles: Array<String>, val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}

interface OnItemClickListener {
    fun onItemClick(view: View, couponBean: CouponBean)
}

class CouponRecycleViewAdapter(val datas: ArrayList<CouponBean>) : RecyclerView.Adapter<CouponRecycleViewHolder>() {
    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CouponRecycleViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_recycle_coupon, parent, false)
        return CouponRecycleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: CouponRecycleViewHolder?, position: Int) {
        holder!!.injectView(datas[position])
        holder!!.itemView.onClick {
            if (listener != null) {
                listener!!.onItemClick(holder.itemView, datas[position])
            }
        }
    }

}

class CouponRecycleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun injectView(couponBean: CouponBean) {
        itemView.find<LinearLayout>(R.id.ll_view).setBackgroundResource(
                when (couponBean.type) {
                    "DISCHARGE_INTEREST" -> {
                        R.mipmap.coupon_orange
                    }

                    else -> {
                        R.mipmap.coupon_blue
                    }
                }
        )
        //后台的不给加我也很绝望
        val rate = TokenManager.checkoutMessage(FieldParams.CURRENT_LOAN_RATE, java.lang.Double::class.java)
        if (rate != null) {
            itemView.find<TextView>(R.id.tv_rate).text = "-" + StringFormatUtils.formatDouble(rate.toDouble() * couponBean.dischargeInterestDay,2)
        }
        itemView.find<TextView>(R.id.tv_content).text = couponBean.title
        itemView.find<TextView>(R.id.tv_term).text = couponBean.dischargeInterestDay.toString()
        itemView.find<TextView>(R.id.tv_time).text = itemView.context.getString(R.string.obtain_time) + ":" + TimeFormat.convertTimeDay(couponBean.validEndTime)
    }

}

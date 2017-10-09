package com.sulu.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.bean.RegionBean
import com.daunkredit.program.sulu.view.certification.PersonalInfoActivity
import com.sulu.kotlin.fragment.AreaActivityInterface
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/14 11:59
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class AreaAdapter(val mDatas: List<RegionBean.RegionsBean>,val mActivity:AreaActivityInterface) :RecyclerView.Adapter<AreaHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AreaHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_area,parent,false)
        return AreaHolder(view,mActivity)
    }

    override fun onBindViewHolder(holder: AreaHolder?, position: Int) {
        holder!!.injectView(mDatas[position])
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}

class AreaHolder(itemView: View,val mActivity: AreaActivityInterface):RecyclerView.ViewHolder(itemView) {
    fun injectView(regionsBean: RegionBean.RegionsBean) {
        val view = itemView.find<TextView>(R.id.tv_area_item)
        view.text = regionsBean.name
        view.onClick {
            mActivity.forwardToNextFragment(regionsBean)
        }
    }

}

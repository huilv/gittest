package com.sulu.kotlin.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.sulu.kotlin.activity.ActivityDetailAcivity
import com.sulu.kotlin.data.ActivityCenterBean
import com.sulu.kotlin.data.ActivityInfoBean
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/15 12:02
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class ActivityCenterAdapter(val mDatas: ArrayList<ActivityInfoBean>) :RecyclerView.Adapter<ActivityCenterHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ActivityCenterHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_activity_center,parent,false)
        val holder = ActivityCenterHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: ActivityCenterHolder?, position: Int) {
        holder!!.injectView(mDatas[position])
    }
}

class ActivityCenterHolder(view: View): RecyclerView.ViewHolder(view) {
    init {

    }
    fun injectView(activityCenterBean: ActivityInfoBean) {
        Glide.with(itemView.context).load(activityCenterBean.url).into(itemView.find<ImageView>(R.id.iv_image_activity_center))
        itemView.find<TextView>(R.id.tv_content_activity_center).text = "happy coder"
        itemView.find<TextView>(R.id.tv_time_activity_center).text = "2017-08-22  to forever"
        itemView.find<View>(R.id.ll_activity_center_item).onClick {
            //TODO detail design
            val intent = Intent(itemView.context.applicationContext, ActivityDetailAcivity::class.java)
            intent.putExtra(FieldParams.ACTIVITY_DETAIL_IMAGE_URL,activityCenterBean.url)
            itemView.context.startActivity(intent)
        }
    }

}

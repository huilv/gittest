package com.sulu.kotlin.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager
import com.sulu.kotlin.data.FunctionInfo
import com.sulu.kotlin.data.MessageBean
import com.sulu.kotlin.utils.dp2Px
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:My
 * @创建日期: 2017/8/4 16:30
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
class OnlineServiceAdapter(val mHistoryDatas: ArrayList<MessageBean>, val mActivity: Context) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val get = mHistoryDatas.get(position)
        val holder: OnLineServiceHolder = OnLineServiceHolder.getHolder(mActivity, get.fromMe, convertView)
        holder.injectView(get)
        return holder.view
    }

    override fun getItem(position: Int): MessageBean {
        return mHistoryDatas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mHistoryDatas?.size
    }
}

class OnLineServiceHolder(val view: View) {
    companion object {
        fun getHolder(ctx: Context, fromMe: Boolean, view: View?): OnLineServiceHolder {
            if (view != null && view.getTag(R.id.message_tag) == fromMe) {
                return view.getTag(R.id.view_holder) as OnLineServiceHolder
            } else {
                when (fromMe) {
                    true -> {
                        val view = View.inflate(ctx, R.layout.item_im_rl, null)
                        val holder = OnLineServiceHolder(view)
                        view.setTag(R.id.view_holder, holder)
                        view.setTag(R.id.message_tag, true)
                        return holder
                    }
                    false -> {
                        val view = View.inflate(ctx, R.layout.item_im_lr, null)
                        val holder = OnLineServiceHolder(view)
                        view.setTag(R.id.view_holder, holder)
                        view.setTag(R.id.message_tag, false)
                        return holder
                    }
                }
            }
        }
    }

    val time: TextView
    val message: TextView
    val image: ImageView
    val maxSize = 120

    init {
        time = view.findViewById(R.id.tv_time) as TextView
        message = view.find<TextView>(R.id.tv_message)
        image = view.find(R.id.iv_image)
    }

    fun injectView(get: MessageBean) {
        when (get.type) {
            1 -> {
                if (message.visibility == View.VISIBLE) {
                    message.visibility = View.GONE
                }
                if (image.visibility != View.VISIBLE) {
                    image.visibility = View.VISIBLE
                }
                Glide.with(view.context).load(get.message).override(time.context.dp2Px(maxSize).toInt(),time.context.dp2Px(maxSize).toInt())
                        .fitCenter()
                        .into(image)
                image.onClick {
                    val localView = View.inflate(image.context, R.layout.dialog_image_big, null)
                    Glide.with(view.context).load(get.message).into(localView.find<ImageView>(R.id.iv_bigImage))
                    val newDialog = DialogManager.newDialog(image.context, localView, true)
                    localView.find<ImageButton>(R.id.ib_close_button).onClick {
                        newDialog.dismiss()
                    }
                }
            }
            4 -> {
                image.setOnClickListener(null)
                if (message.visibility == View.VISIBLE) {
                    message.visibility = View.GONE
                }
                if (image.visibility != View.VISIBLE) {
                    image.visibility = View.VISIBLE
                }
                Glide.with(view.context).load(get.message).asGif().into(image)
            }
            else -> {
                message.text = null
                if (message.visibility != View.VISIBLE) {
                    message.visibility = View.VISIBLE
                }
                if (image.visibility == View.VISIBLE) {
                    image.visibility = View.GONE
                }
                message.text = get.message
            }
        }
        time.text = get.time
    }

}


class OnlineServiceGridAdapter(val mGvDatas: ArrayList<FunctionInfo>, val mActivity: Context) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder = OnlineServiceGridHolder.getHolder(convertView, mActivity)
        holder.injectView(mGvDatas.get(position))
        return holder.view
    }

    override fun getItem(position: Int): Any {
        return mGvDatas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mGvDatas.size
    }

}

class OnlineServiceGridHolder(val view: View) {
    companion object {
        fun getHolder(view: View?, ctx: Context): OnlineServiceGridHolder {
            var holder: OnlineServiceGridHolder
            when (view) {
                null -> {
                    val view = View.inflate(ctx, R.layout.item_online_service_gird_fun, null)
                    holder = OnlineServiceGridHolder(view)
                    view.setTag(holder)
                }
                else -> {
                    holder = view.getTag() as OnlineServiceGridHolder
                }
            }
            return holder;
        }
    }

    val icon: ImageView
    val desc: TextView

    init {
        icon = view.findViewById(R.id.iv_icon) as ImageView
        desc = view.find<TextView>(R.id.tv_desc)
    }

    fun injectView(get: FunctionInfo) {
        icon.setImageResource(get.iconId)
        desc.setText(get.name)
        view.onClick { get.func.invoke() }
    }
}

/**
 * viewpage adapter for onlineservicefragment
 */
class OnLineServiceViewPageAdapter(val mEmojiVP: ViewPager) : PagerAdapter() {
    private val ITEM_SIZE: Int by lazy {
        mEmojiVP.context.dp2Px(40).toInt()
    }
    private val ITEM_IMAGE_SIZE: Int by lazy {
        mEmojiVP.context.dp2Px(30).toInt()
    }
    private var ITEM_PADDING_HOR: Int = 10
    private var ITEM_PADDING_VER: Int = 10
    private var HOR_SUM = 1
    private var VER_SUM = 1
    private val ITEM_SUM = 99
    private var PAGE_SUM = 1
    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        val height = mEmojiVP.height
        val width = mEmojiVP.width
        HOR_SUM = width / ITEM_SIZE
        ITEM_PADDING_HOR = width % ITEM_SIZE / (HOR_SUM + 1)
        VER_SUM = height / ITEM_SIZE
        ITEM_PADDING_VER = height % ITEM_SIZE / (VER_SUM + 1)
        PAGE_SUM = HOR_SUM * VER_SUM
        return if(ITEM_SUM % PAGE_SUM > 0){ 1 + ITEM_SUM / PAGE_SUM}else{ITEM_SUM / PAGE_SUM}
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val view: View = createView(position)
        container!!.addView(view)
        return view
    }

    private fun createView(position: Int): View {
        val startImagePosition = position * PAGE_SUM
        val endImagePosition = if ((position + 1) * PAGE_SUM < ITEM_SUM) {
            (position + 1) * PAGE_SUM
        } else {
            ITEM_SUM
        }
        val generalLinearLayout = LinearLayout(mEmojiVP.context)
        val layoutParams = ViewPager.LayoutParams()
        layoutParams.width = mEmojiVP.width
        layoutParams.height = mEmojiVP.height
        layoutParams.isDecor = false
        generalLinearLayout.layoutParams = layoutParams
        generalLinearLayout.orientation = LinearLayout.VERTICAL
        val tem_ver_sum = (endImagePosition - startImagePosition) / HOR_SUM
        if (tem_ver_sum <= VER_SUM) {
            val ver_sum = if ((endImagePosition - startImagePosition) % HOR_SUM == 0) {
                tem_ver_sum
            } else {
                tem_ver_sum + 1
            }
            var i = 0
            while (i < ver_sum) {
                val startIndex = startImagePosition + HOR_SUM * i
                generalLinearLayout.addView(getLineOfItem(i == 0, startIndex, if (startIndex + HOR_SUM > endImagePosition) {
                    endImagePosition - startIndex
                } else {
                    startIndex + HOR_SUM
                }))
                i++
            }
        } else {

        }

        return generalLinearLayout
    }

    private fun getLineOfItem(isFirst: Boolean, startIndex: Int,endIndex:Int): View? {
        val lineView = LinearLayout(mEmojiVP.context)
        val layoutParams = LinearLayout.LayoutParams(mEmojiVP.width, if (isFirst) {
            ITEM_SIZE + 2 * ITEM_PADDING_VER
        } else {
            ITEM_SIZE + ITEM_PADDING_VER
        })
        lineView.layoutParams = layoutParams
        lineView.orientation = LinearLayout.HORIZONTAL
        var i = 0
        while (i < endIndex - startIndex){
            val item = ImageView(mEmojiVP.context)
            val defaultPaddiing = (ITEM_SIZE - ITEM_IMAGE_SIZE) / 2
            val itemLayoutParam = LinearLayout.LayoutParams(ITEM_IMAGE_SIZE,ITEM_IMAGE_SIZE)
            itemLayoutParam.leftMargin = if(i == 0){ITEM_PADDING_HOR + defaultPaddiing}else{defaultPaddiing}
            itemLayoutParam.rightMargin = ITEM_PADDING_HOR + defaultPaddiing
            itemLayoutParam.topMargin = if(isFirst){ITEM_PADDING_VER + defaultPaddiing}else{defaultPaddiing}
            itemLayoutParam.bottomMargin = ITEM_PADDING_VER + defaultPaddiing
            item.layoutParams = itemLayoutParam
            val resId = R.mipmap.aliwx_s001 + startIndex + i
            item.setImageResource(resId)
            item.onClick {
                listener.onEmojiClick(resId)
            }
            lineView.addView(item)
            i++
        }
            return lineView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container!!.removeView(`object`!! as View)
    }

    lateinit var listener:OnEmojiClickListener
    fun setOnEmojiClickListener(l:OnEmojiClickListener){
        listener = l
    }
}

interface OnEmojiClickListener {
    fun onEmojiClick(resId:Int)
}
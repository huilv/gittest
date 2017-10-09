package com.sulu.kotlin.fragment

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ResultReceiver
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alibaba.mobileim.IYWPushListener
import com.alibaba.mobileim.YWIMCore
import com.alibaba.mobileim.channel.YWEnum
import com.alibaba.mobileim.channel.event.IWxCallback
import com.alibaba.mobileim.contact.IYWContact
import com.alibaba.mobileim.conversation.*
import com.alibaba.mobileim.gingko.model.tribe.YWTribe
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.App
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseActivity
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.app.base.YWManager
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.adapter.OnEmojiClickListener
import com.sulu.kotlin.adapter.OnLineServiceViewPageAdapter
import com.sulu.kotlin.adapter.OnlineServiceAdapter
import com.sulu.kotlin.adapter.OnlineServiceGridAdapter
import com.sulu.kotlin.data.FunctionInfo
import com.sulu.kotlin.data.MessageBean
import com.sulu.kotlin.utils.*
import com.x.leo.refrashviews.OnRefrashAdapter
import com.x.leo.refrashviews.RefrashLayout
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import java.io.File


/**
 * @作者:My
 * @创建日期: 2017/8/3 10:25
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：下拉刷新
 */
class OnlineServiceFragment : BaseFragment<OnlineServicePresenter>(), OnlineServiceView {

    private var mHistoryDatas = ArrayList<MessageBean>()

    private var isSpanShowing: Boolean = false

    lateinit var mEtMessage: EditText
    lateinit var mAddFunctionButton: ImageButton
    lateinit var mSendButton: Button
    lateinit var mMessageList: ListView
    lateinit var mGvAddFunction: GridView
    lateinit var mFaceButton: ImageButton
    lateinit var mAdapter: BaseAdapter
    lateinit var mExtendSpan: FrameLayout
    lateinit var mEmojiVP: ViewPager
    var mEmojiAdapter: OnLineServiceViewPageAdapter? = null
    val mGvDatas: ArrayList<FunctionInfo> by lazy {
        ArrayList<FunctionInfo>()
    }

    override fun doPreBuildHeader(): Boolean {
        return true
    }

    override fun getBackPressListener(): View.OnClickListener {
        return View.OnClickListener {
            mActivity.finish()
        }
    }

    override fun updateList(newMessage: List<YWMessage>) {
        mHistoryDatas.clear()
        newMessage.forEach {
            val bean = MessageBean(it)
            mHistoryDatas.add(bean)
        }
        mAdapter.notifyDataSetChanged()
//        mMessageList.smoothScrollToPosition(getLastPosition())
    }

    private fun getLastPosition(): Int {
        when (mHistoryDatas.size - 1 >= 0) {
            true -> {
                return mHistoryDatas.size - 1
            }
            else -> {
                return 0
            }
        }
    }

    override fun updateList(newMessage: MessageBean) {
        mHistoryDatas.add(newMessage)
        mAdapter.notifyDataSetChanged()
        mMessageList.smoothScrollToPosition(getLastPosition())
    }

    override fun initPresenter(): OnlineServicePresenter = OnlineServicePreImpl()

    override fun getLayoutId(): Int {
        return R.layout.fragment_online_service
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mPresenter.loginYW(mActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.loginYW(mActivity)
    }

    var isEmojiShowing: Boolean = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mPresenter.loginYW(mActivity)
        EmojiManager.init(mActivity)
        checkAndRequestPermissions(permissions)
        view!!.find<RefrashLayout>(R.id.lrl).onRefrashListener = object : OnRefrashAdapter() {

            override fun onTopRefrash() {
                mPresenter.upperDataNums(10)
                mPresenter.getHistoryDatas()
            }

            override fun onBottomRefrash() {
            }

        }
        mEtMessage = view!!.find(R.id.id_edittext_msg)
        mGvAddFunction = view!!.find(R.id.gv_add_function)
        mExtendSpan = view!!.find(R.id.fl_span)
        mEmojiVP = view!!.find(R.id.vp_emoji)
        initFunction()
        mFaceButton = view!!.find(R.id.id_imagebutton_face)
        mAddFunctionButton = view!!.find(R.id.id_imagebutton_add)
        mMessageList = view!!.find(R.id.id_list_chat_history)
        mSendButton = view!!.find(R.id.btn_send)
        mEtMessage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mAddFunctionButton?.onClick {
            when (isSpanShowing) {
                true -> {
                    if (isEmojiShowing) {
                        mEmojiVP.visibility = View.GONE
                        mGvAddFunction.visibility = View.VISIBLE
                        isEmojiShowing = false
                    } else {
                        mExtendSpan?.visibility = View.GONE
                        mGvAddFunction?.visibility = View.GONE
                        isSpanShowing = false
                    }
                }
                else -> {
                    isEmojiShowing = false
                    mExtendSpan?.visibility = View.VISIBLE
                    mGvAddFunction?.visibility = View.VISIBLE
                    isSpanShowing = true
                }
            }
        }
        mMessageList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> mEtMessage.clearFocus() }
        mFaceButton.onClick {
            when (isSpanShowing) {
                true -> {
                    if (isEmojiShowing) {
                        mEmojiVP.visibility = View.GONE
                        mExtendSpan.visibility = View.GONE
                        isEmojiShowing = false
                        isSpanShowing = false
                    } else {
                        mGvAddFunction.visibility = View.GONE
                        mEmojiVP.visibility = View.VISIBLE
                        isEmojiShowing = true
                        initEmojiVp()
                    }
                }
                else -> {
                    mEmojiVP.visibility = View.VISIBLE
                    mExtendSpan.visibility = View.VISIBLE
                    isEmojiShowing = true
                    initEmojiVp()
                    isSpanShowing = true
                }
            }
        }

        mEtMessage?.onFocusChange { v, hasFocus ->
            if (hasFocus) {
                mAddFunctionButton.visibility = View.GONE
                mSendButton.visibility = View.VISIBLE;
                if (isSpanShowing && !isEmojiShowing) {
                    mGvAddFunction?.visibility = View.GONE
                    isSpanShowing = !isSpanShowing
                }
            } else {
                mAddFunctionButton.visibility = View.VISIBLE
                mSendButton.visibility = View.GONE
                hideSoftInputSpan(mEtMessage)
            }
        }
        mSendButton.onClick {
            if (!TextUtils.isEmpty(mEtMessage.text)) {
                mPresenter.sendMessage(mEtMessage.text!!)
                mEtMessage.text = null
                mEtMessage.clearFocus()
                hideSoftInputSpan(mEtMessage)
                hideExtraSpan()
            }
        }
        mAdapter = OnlineServiceAdapter(mHistoryDatas, mActivity)
        mMessageList?.adapter = mAdapter
    }

    private fun hideExtraSpan() {
        if (isSpanShowing) {
            mExtendSpan.visibility = View.GONE
            isSpanShowing = false
            if (isEmojiShowing) {
                mEmojiVP.visibility = View.GONE
                isEmojiShowing = false
            }
        }
    }

    private fun hideSoftInputSpan(view: View) {
        val inputMethodManager = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS, object : ResultReceiver(null) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                when (resultCode) {
                    InputMethodManager.RESULT_UNCHANGED_SHOWN -> {
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    else -> {
                    }
                }
            }
        })
    }

    private fun initEmojiVp() {
        if (mEmojiAdapter == null) {
            mEmojiVP.post {
                mEmojiAdapter = OnLineServiceViewPageAdapter(mEmojiVP)
                mEmojiAdapter!!.setOnEmojiClickListener(object : OnEmojiClickListener {
                    override fun onEmojiClick(resId: Int) {
                        val text = EmojiManager.resourceIdToString(resId)
                        mEtMessage.requestFocus()
                        mEtMessage.append(text)
                    }

                })
                mEmojiVP.adapter = mEmojiAdapter

            }
        }
    }

    val TAKE_PHOTO_REQUEST_CODE = 1012
    val PICK_IMAGE_REQUEST_CODE = 1013
    val REQUES_PERMISSION_CODE = 1213
    val permissions by lazy {
        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
    }

    fun checkAndRequestPermissions(permission: Array<String>) {
        if (Build.VERSION.SDK_INT >= 23) {
            mActivity.checkAndRequestPermissions(permission, REQUES_PERMISSION_CODE)
        }
    }
    var storeImgPath:String? = null
    private fun initFunction() {
        mGvDatas.add(FunctionInfo(getString(R.string.text_take_photo), R.drawable.ic_photograph_d, fun() {
            checkAndRequestPermissions(permissions)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                storeImgPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + System.currentTimeMillis() + ".jpg"
            } else {
                storeImgPath = mActivity.filesDir.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"
            }
            val file = File(storeImgPath)
            if (file.exists()) {
                file.delete()
                file.createNewFile()
            }
            val uri = FileProvider.getUriForFile(context,context.packageName + ".fileprovider",file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
        }))
        mGvDatas.add(FunctionInfo(getString(R.string.text_choose_pic), R.mipmap.choose_pic, fun() {
            checkAndRequestPermissions(permissions)
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }))
        mGvAddFunction.adapter = OnlineServiceGridAdapter(mGvDatas, mActivity)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PHOTO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && storeImgPath != null) {
                    Thread(Runnable {
                        val opts = BitmapFactory.Options()
                        opts.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(storeImgPath,opts)
                        mActivity.runOnUiThread {
                            sendImageMessage(storeImgPath!!, opts.outWidth, opts.outHeight, File(storeImgPath).length().toInt(), getExternalName(storeImgPath!!))
                        }
                    }).start()
                }
            }
            PICK_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Thread(Runnable {
                        val uri = data!!.data
                        val cursor = mActivity.getContentResolver().query(uri, null, null,
                                null, null)
                        cursor.moveToFirst()
                        val path = cursor.getString(1)
                        val opts = BitmapFactory.Options()
                        opts.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(path, opts)
                        if (TextUtils.equals(getExternalName(path), "jpg")) {
                            mActivity.runOnUiThread {
                                sendImageMessage(path, opts.outWidth, opts.outHeight, File(path).length().toInt(), "jpg")
                            }
                        } else {
                            val newPath: String? = toJpeg(mActivity, path)
                            if (newPath != null) {
                                mActivity.runOnUiThread {
                                    sendImageMessage(newPath!!, opts.outWidth, opts.outHeight, File(path).length().toInt(), "jpg")
                                }
                            }
                        }
                    }).start()


                }
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUES_PERMISSION_CODE -> {
                val permissionToRequest = ArrayList<String>()
                permissions.forEachIndexed { index, s ->
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        permissionToRequest.add(s)
                    }
                }
                if (permissionToRequest.size > 0) {
                    checkAndRequestPermissions(permissionToRequest.toArray(arrayOfNulls(permissionToRequest.size)))
                }
            }
            else -> {
            }
        }
    }

    private fun sendImageMessage(path: String, width: Int, height: Int, size: Int, externalName: String) {
        mPresenter.sendImageMessage(path, width, height, size, externalName)
    }

    override fun initData() {
    }

    override fun initHeader(view: TextView?): Boolean {
        view?.text = getText(R.string.onlineQA)
        return true
    }
}


class OnlineServicePreImpl : OnlineServicePresenter, BaseFragmentPresenterImpl() {
    override fun upperDataNums(i: Int) {
        lineNumber += i
    }

    override fun isLogin(): Boolean {
        return conversation == null
    }

    override fun sendImageMessage(path: String, width: Int, height: Int, size: Int, externalName: String) {
        val message = YWMessageChannel.createImageMessage(path, path, width, height, size, externalName, YWEnum.SendImageResolutionType.ORIGINAL_IMAGE)
        conversation!!.messageSender.sendMessage(message, TIMEOUT, object : IWxCallback {
            override fun onSuccess(vararg p0: Any?) {
                updateMessageViewList(msgList)
            }

            override fun onProgress(p0: Int) {

            }

            override fun onError(p0: Int, p1: String?) {

            }

        })
    }

    var msgList: List<YWMessage> = ArrayList<YWMessage>()
    var lineNumber = 10
    override fun getHistoryDatas() {
        msgList = conversation!!.messageLoader.loadMessage(lineNumber,0,false, object : IWxCallback {
            override fun onSuccess(vararg result: Any) {
                //到这里表明loadMessage同步返回的List已经有内容，请在这里同步更新UI，比如进行notifyDataSetChange调用
                updateMessageViewList(msgList)
            }

            override fun onError(code: Int, info: String) {

            }

            override fun onProgress(progress: Int) {

            }
        })
    }

    private fun updateMessageViewList(msgList: List<YWMessage>) {
        if (msgList != null) {
            if (isAttached) {
                val fragView = mView as OnlineServiceView
                fragView.updateList(msgList)
            }
        }
    }

    private val TIMEOUT: Long = 5 * 1000
    private var conversation: YWConversation? = null
    override fun sendMessage(text: Editable) {
        val message = YWMessageChannel.createTextMessage(text.toString())
        conversation!!.messageSender.sendMessage(message, TIMEOUT, object : IWxCallback {
            override fun onSuccess(vararg p0: Any?) {
                val newMessage = MessageBean(message)
                updateMessageView(newMessage)
            }

            override fun onProgress(p0: Int) {
            }

            override fun onError(p0: Int, p1: String?) {
                XLeoToast.showMessage(p1)
            }

        })
    }


    override fun loginYW(mActivity: BaseActivity<*>?) {
        val hasLogin = if(tokenInstance.getMessage(FieldParams.HAS_YW_LOGIN) == null){
            false
        }else{
            tokenInstance.getMessage(FieldParams.HAS_YW_LOGIN) as Boolean
        }
        if(hasLogin && conversation != null){
            getHistoryDatas()
            return
        }
        YWManager.initYW(mActivity, object : YWManager.OnLoginCompleteListener {
            override fun onLoginComplete(imCore: YWIMCore) {
                //创建客服联系人对象
                val contact = EServiceContact(App.SERVICE_ACCOUNT, 0)
                //获取客服会话
                val contemp = imCore.conversationService.getConversation(contact)
                //若获取到的conversation为null，则创建一个
                if (contemp == null) {
                    conversation = imCore.conversationService.conversationCreater.createConversationIfNotExist(contact)
                } else {
                    conversation = contemp
                }
                conversation!!.messageLoader.addMessageListener(object : IYWMessageListener {

                    override fun onItemUpdated() {
                        // 消息列表中的内容变更，一般是文件下载成功，删除消息成功等
                        // 如果本地已经获取到消息记录用于展示到页面上。
                        // 可直接调用BaseAdapter.notifyDataSetChanged();
                    }

                    override fun onItemComing() {
                        // 接收到一条新消息
                        // 如果本地已经获取到消息记录用于展示到页面上。
                        // 可直接调用BaseAdapter.notifyDataSetChanged();
                        lineNumber++
                        getHistoryDatas()
                    }

                    override fun onInputStatus(status: Byte) {
                        //对方输入状态变更
                    }
                })
                getHistoryDatas()
                val msgPushListener = object : IYWPushListener {
                    override fun onPushMessage(p0: IYWContact?, p1: YWMessage?) {
                        if (p0?.userId == App.SERVICE_ACCOUNT) {
                            notifyNewMessage(p1)
                        }
                    }

                    override fun onPushMessage(p0: YWTribe?, p1: YWMessage?) {
                    }
                }

//如果之前add过，请清除
                imCore.conversationService.removePushListener(msgPushListener)
//增加新消息到达的通知
                imCore.conversationService.addPushListener(msgPushListener)
                tokenInstance.storeMessage(FieldParams.HAS_YW_LOGIN,true)
            }

            override fun onLoginError(errCode: Int, description: String?) {
                XLeoToast.showMessage(description)
                loginYW(mActivity)
            }

        })

    }

    private fun notifyNewMessage(p1: YWMessage?) {
        val pendintent = PendingIntent.getActivity(App.instance,FieldParams.NOTIFICATION_CLICKED_REQUEST_CODE,Intent(),Intent.FLAG_ACTIVITY_NEW_TASK)
        val notification = NotificationCompat.Builder(App.instance)
                .setLargeIcon(BitmapFactory.decodeResource(App.instance.resources,R.mipmap.ic_logo))
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentText(App.instance.getText(R.string.notify_new_message))
                .setSubText(p1?.content)
                .setContentIntent(pendintent)
                .build()
        val notificationManager = App.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0,notification)
    }

    private fun updateMessageView(newMessage: MessageBean) {
        if (isAttached) {
            val fragView = mView as OnlineServiceView
            fragView.updateList(newMessage)
        }
    }

}

interface OnlineServiceView {
    fun updateList(newMessage: MessageBean)
    fun updateList(newMessage: List<YWMessage>)

}

interface OnlineServicePresenter : BaseFragmentPresenter {
    fun loginYW(mActivity: BaseActivity<*>?)
    fun sendMessage(text: Editable)
    fun getHistoryDatas()
    fun sendImageMessage(path: String, width: Int, height: Int, size: Int, externalName: String)
    fun isLogin(): Boolean
    fun upperDataNums(i: Int)

}

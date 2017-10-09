package com.sulu.kotlin.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.bean.LatestLoanAppBean
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.sulu.kotlin.data.RequestLevel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @作者:My
 * @创建日期: 2017/7/12 14:20
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
val tokenInstance  by lazy {  TokenManager.getInstance()}
fun getToken() = tokenInstance.token
fun getLatestLoanData(requestLevel: RequestLevel, block:(result:LatestLoanAppBean)->Unit){
    val currentTimeMillis = System.currentTimeMillis()
    when (requestLevel) {
        RequestLevel.IMMEDIATELY -> {
            tokenInstance.requestLatestLoanData(block)
        }
        RequestLevel.LATEST ->{
            block(tokenInstance.getMessage(FieldParams.LATESTBEAN) as @kotlin.ParameterName(name = "result") LatestLoanAppBean)
        }
        RequestLevel.WAITNEXT ->{
            tokenInstance.addToNextPull(block)
        }
    }
}

fun Context.dp2Px(dp:Int) = resources.displayMetrics.density * dp

fun timeLongToString(time:Long):String{
    val timeFormator = SimpleDateFormat("yyy-MM-dd HH:mm:ss")
    return timeFormator.format(Date(time))
}

fun getExternalName(path:String):String{
    val split = path.split(".")
    if (split != null && split.size > 0) {
            return split.get(split.size - 1)
        }
    return "";
}

fun toJpeg(ctx: Activity, path: String): String? {
    if(Build.VERSION.SDK_INT >= 23 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
        XLeoToast.showMessage(" write external storage permission not granted,can't use image")
        return null
    }
    var png = BitmapFactory.decodeStream(FileInputStream(File(path)))
    Log.d("UTILS",(png == null).toString())
    val newPath = path.replace(".png", ".jpg")
    val os = FileOutputStream(File(newPath))
    png.compress(Bitmap.CompressFormat.JPEG,80,os)
    os.flush()
    os.close()
    return newPath
}

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.checkAndRequestPermissions(permission:Array<String>,requestCode:Int){
    val permissionToRequest by lazy {
       ArrayList<String>()
    }
    for (s in permission) {
        if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
            permissionToRequest.add(s)
        }
    }
    if(permissionToRequest.size > 0){
        requestPermissions(permissionToRequest.toArray(arrayOfNulls(permissionToRequest.size)),requestCode)
    }
}

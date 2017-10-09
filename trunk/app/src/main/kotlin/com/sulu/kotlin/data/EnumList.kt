package com.sulu.kotlin.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @作者:My
 * @创建日期: 2017/7/12 14:34
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
enum class RequestLevel{
    IMMEDIATELY,LATEST,WAITNEXT
}
enum class LockViewMode{
    SETTING,LOGGING
}
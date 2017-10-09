package com.sulu.kotlin.utils

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @作者:XLEO
 * @创建日期: 2017/8/24 10:20
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object ThreadPoolManager {
    val threadPool:ThreadPoolExecutor by lazy{
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        ThreadPoolExecutor(availableProcessors,availableProcessors *3,10,TimeUnit.MINUTES,LinkedBlockingDeque<Runnable>(availableProcessors))
    }
    fun runWithThread(runnable:Runnable){
        threadPool.execute {
            runnable.run()
        }
    }
}
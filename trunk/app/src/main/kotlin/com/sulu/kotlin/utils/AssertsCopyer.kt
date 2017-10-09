package com.sulu.kotlin.utils

import android.content.Context
import android.content.res.AssetManager
import com.daunkredit.program.sulu.common.utils.XLeoSp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @作者:XLEO
 * @创建日期: 2017/8/24 10:57
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object AssertsCopyer {
    fun checkFile(s: String, ctx: Context): Boolean {
        val aBoolean = XLeoSp.getInstance(ctx).getBoolean(s)
        return aBoolean
    }

    fun copyfile(s: String, assets: AssetManager, ctx: Context, afterCopy: (() -> Unit)?) {
        ThreadPoolManager.runWithThread(Runnable {
            var fileOutputStream: FileOutputStream? = null
            if (s.contains(File.separator)) {
                val dir = s.substring(0, s.lastIndexOf(File.separator))
                val file = File(ctx.filesDir.absolutePath,dir)
                if (!(file.isDirectory && file.exists())) {
                    file.mkdirs()
                }
            }
            var open: InputStream? = null
            try {
                val file = File(ctx.filesDir, s)
                if (file.exists()) {
                    file.delete()
                    file.createNewFile()
                }
                fileOutputStream = FileOutputStream(file)
                open = assets.open(s)
                var len = -1
                val buffer = ByteArray(1024 * 8)
                len = open!!.read(buffer)
                while (len != -1) {
                    fileOutputStream!!.write(buffer, 0, len)
                    len = open!!.read(buffer)
                }
                fileOutputStream!!.flush()
                XLeoSp.getInstance(ctx).putBoolean(s, true)
                afterCopy?.invoke()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                if (open != null) {
                    try {
                        open.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })

    }
}
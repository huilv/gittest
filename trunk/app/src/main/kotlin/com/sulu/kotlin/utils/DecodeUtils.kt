@file:JvmName("EncodeUtils")
@file:JvmMultifileClass
package com.sulu.kotlin.utils

import android.util.Base64
import java.security.Key
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec

//import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 10:42
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
fun endecode(src: String): String {
    return src
}

fun decode(src: String): String {
    return src
}

/**
 * SHA加密 生成40位SHA码
 */
@Throws(Exception::class)
fun shaEncode(inStr: String): String {
    var sha: MessageDigest? = null
    try {
        sha = MessageDigest.getInstance("SHA")
    } catch (e: Exception) {
        println(e.toString())
        e.printStackTrace()
        return ""
    }

    val byteArray = inStr.toByteArray(charset("UTF-8"))
    val md5Bytes = sha!!.digest(byteArray)
    val hexValue = StringBuffer()
    for (i in md5Bytes.indices) {
        val `val` = md5Bytes[i].toInt() and 0xff
        if (`val` < 16) {
            hexValue.append("0")
        }
        hexValue.append(Integer.toHexString(`val`))
    }
    return hexValue.toString()
}

@Throws(Exception::class)
fun des3EncodeCBC(key: ByteArray, keyiv: ByteArray, data: ByteArray): ByteArray {
//    Security.addProvider(BouncyCastleProvider())
    val deskey = keyGenerator(String(key))
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    val ips = IvParameterSpec(keyiv)
    cipher.init(Cipher.ENCRYPT_MODE, deskey, ips)
    val bOut = cipher.doFinal(Base64.encode(data,Base64.NO_PADDING))
    for (k in bOut.indices) {
        print(bOut[k].toString() + " ")
    }
    println("")
    return bOut
}

// 算法名称
val KEY_ALGORITHM = "desede"
// 算法名称/加密模式/填充方式
val CIPHER_ALGORITHM = "desede/CBC/PKCS5Padding"

/**
 * 生成密钥key对象
 * @param KeyStr 密钥字符串
 * *
 * @return 密钥对象
 * *
 * @throws InvalidKeyException
 * *
 * @throws NoSuchAlgorithmException
 * *
 * @throws InvalidKeySpecException
 * *
 * @throws Exception
 */
@Throws(Exception::class)
private fun keyGenerator(keyStr: String): Key {
    val input = HexString2Bytes(keyStr)
    val KeySpec = DESedeKeySpec(input)
    val KeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
    return KeyFactory.generateSecret(KeySpec as java.security.spec.KeySpec) as Key
}

private fun parse(c: Char): Int {
    if (c >= 'a') return c - 'a' + 10 and 0x0f
    if (c >= 'A') return c - 'A' + 10 and 0x0f
    return c - '0' and 0x0f
}

// 从十六进制字符串到字节数组转换
fun HexString2Bytes(hexstr: String): ByteArray {
    val b = ByteArray(hexstr.length / 2)
    var j = 0
    for (i in 0..b.size / 2) {
        val c0 = hexstr[j++]
        val c1 = hexstr[j++]
        b[i] = parse(c0).shl(4).or(parse(c1)).toByte()
    }
    return b
}

/**
 *
 * CBC解密
 * @param key 密钥
 * @param keyiv IV
 * @param data Base64编码的密文
 * @return 明文
 * @throws Exception
 **/
@Throws(Exception::class)
fun des3DecodeCBC(key: ByteArray, keyiv: ByteArray, data: ByteArray): ByteArray {
    val deskey = keyGenerator(String(key))
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM);
    val ips = IvParameterSpec(keyiv)
    cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
    val bOut = cipher.doFinal(data)
    return Base64.decode(bOut,Base64.NO_PADDING);
}
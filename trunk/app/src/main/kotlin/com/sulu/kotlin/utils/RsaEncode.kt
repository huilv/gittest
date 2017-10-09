@file:JvmName("RsaEncodeUtils")
package com.sulu.kotlin.utils

import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher


/**
 * @作者:XLEO
 * @创建日期: 2017/8/16 10:45
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
/**
 * String to hold name of the encryption algorithm.
 */
val ALGORITHM = "RSA"

/**
 * String to hold name of the encryption padding.
 */
val PADDING = "RSA/NONE/NoPadding"

/**
 * String to hold name of the security provider.
 */
val PROVIDER = "BC"

/**
 * String to hold the name of the private key file.
 */
val PRIVATE_KEY_FILE = "e:/defonds/work/20150116/private.key"

/**
 * String to hold name of the public key file.
 */
val PUBLIC_KEY_FILE = "e:/defonds/work/20150116/public.key"

/**
 * Generate key which contains a pair of private and public key using 1024
 * bytes. Store the set of keys in Prvate.key and Public.key files.

 * @throws NoSuchAlgorithmException
 * *
 * @throws IOException
 * org.bouncycastle.jce.provider.BouncyCastleProvider()
 * @throws FileNotFoundException
 */
fun generateKey() {
    try {

//        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        val keyGen = KeyPairGenerator.getInstance(
                ALGORITHM, PROVIDER)
        keyGen.initialize(256)
        val key = keyGen.generateKeyPair()

        val privateKeyFile = File(PRIVATE_KEY_FILE)
        val publicKeyFile = File(PUBLIC_KEY_FILE)

        // Create files to store public and private key
        if (privateKeyFile.getParentFile() != null) {
            privateKeyFile.getParentFile().mkdirs()
        }
        privateKeyFile.createNewFile()

        if (publicKeyFile.getParentFile() != null) {
            publicKeyFile.getParentFile().mkdirs()
        }
        publicKeyFile.createNewFile()

        // Saving the Public key in a file
        val publicKeyOS = ObjectOutputStream(
                FileOutputStream(publicKeyFile))
        publicKeyOS.writeObject(key.getPublic())
        publicKeyOS.close()

        // Saving the Private key in a file
        val privateKeyOS = ObjectOutputStream(
                FileOutputStream(privateKeyFile))
        privateKeyOS.writeObject(key.getPrivate())
        privateKeyOS.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/**
 * The method checks if the pair of public and private key has been
 * generated.

 * @return flag indicating if the pair of keys were generated.
 */
fun areKeysPresent(): Boolean {

    val privateKey = File(PRIVATE_KEY_FILE)
    val publicKey = File(PUBLIC_KEY_FILE)

    if (privateKey.exists() && publicKey.exists()) {
        return true
    }
    return false
}

/**
 * Encrypt the plain text using public key.

 * @param text
 * *            : original plain text
 * *
 * @param key
 * *            :The public key
 * *
 * @return Encrypted text
 * *
 * @throws java.lang.Exception
 */
fun encrypt(text: String, key: PublicKey): ByteArray? {
    var cipherText: ByteArray? = null
    try {
        // get an RSA cipher object and print the provider
//        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        val cipher = Cipher.getInstance(PADDING, PROVIDER)

        // encrypt the plain text using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key)
        cipherText = cipher.doFinal(text.toByteArray())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return cipherText
}

/**
 * Decrypt text using private key.

 * @param text
 * *            :encrypted text
 * *
 * @param key
 * *            :The private key
 * *
 * @return plain text
 * *
 * @throws java.lang.Exception
 */
fun decrypt(text: ByteArray, key: PrivateKey): String {
    var dectyptedText: ByteArray? = null
    try {
        // get an RSA cipher object and print the provider
//        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        val cipher = Cipher.getInstance(PADDING, PROVIDER)

        // decrypt the text using the private key
        cipher.init(Cipher.DECRYPT_MODE, key)
        dectyptedText = cipher.doFinal(text)

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return String(dectyptedText!!)
}
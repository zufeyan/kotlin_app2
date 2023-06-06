package com.sctgold.ltsgold.android.util

import android.util.Log
import java.io.Serializable
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher


class EncryptUtil : Serializable {

    companion object {

        private val TAG = "ENCRYPT"

    fun encryptForRequest(raw: String, publicKey: PublicKey?): String? {
        val encryptToken = encrypt(raw.toByteArray(), publicKey)
        return Base64.getMimeEncoder().encodeToString(encryptToken)
    }

    fun encrypt(text: ByteArray?, key: PublicKey?): ByteArray {
        var encryptedText = ByteArray(0)
        try {
            val cipher: Cipher = Cipher.getInstance("RSA", "BC")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            encryptedText = cipher.doFinal(text)
        } catch (ex: Exception) {
            Log.e(TAG, "encrypt RSA failed!$ex")
        }
        return encryptedText
    }

//    fun md5(input: String): String? {
//        val md5 = MD5Digest()
//        md5.update(input.toByteArray(), 0, input.toByteArray().size)
//        val digest = ByteArray(md5.getDigestSize())
//        md5.doFinal(digest, 0)
//        return Base64.toBase64String(digest)
//    }

        fun md5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            val md5Base16 = BigInteger(1, md.digest(input.toByteArray(Charsets.UTF_8))).toString(16)
                .padStart(32, '0')
            val bytes = md5Base16.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            return Base64.getMimeEncoder().encodeToString(bytes)
        }

//    fun createPublicKey(pubKey: ByteArray?): PublicKey? {
//        val keyFactory: KeyFactory
//        return try {
//            LogUtil.debug(TAG, "Entering : createPublicKey")
//            val bPubKey: ByteArray = Base64.decode(pubKey)
//            LogUtil.debug(TAG, "After Base64.decode")
//            keyFactory = KeyFactory.getInstance("RSA")
//            LogUtil.debug(TAG, "After keyFactory")
//            val _spec = X509EncodedKeySpec(bPubKey)
//            LogUtil.debug(TAG, "After X509EncodedKeySpec")
//            keyFactory.generatePublic(_spec)
//        } catch (ex: Exception) {
//            Log.e(TAG, "create publicKey system_error!! $ex")
//            null
//        }
//    }

    }

}
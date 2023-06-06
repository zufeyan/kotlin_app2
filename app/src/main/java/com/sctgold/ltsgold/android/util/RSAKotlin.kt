package com.sctgold.android.tradeplus.util

import android.R.id
import android.content.Context
import android.util.Base64.encodeToString
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


class RSAKotlin {
    private val bufferSize_ = 32
    private val headerTag = "-----BEGIN RSA PUBLIC KEY-----"
    private val footerTag = "-----END RSA PUBLIC KEY-----"


    private fun readFile(myContext: Context): String {
        val certPath = "public_key.pem"
        val input: InputStream = myContext.assets.open(certPath)
        val buffer = ByteArray(bufferSize_)
        var len = 0
        val keyBuffer = ByteArrayOutputStream()
        while (input.read(buffer).also { len = it } != -1) {
            keyBuffer.write(buffer, 0, len)
        }
        var str = String(keyBuffer.toByteArray())
        str = str.replace(headerTag, "")
        str = str.replace(footerTag, "")
        return str
    }

    fun decryptRsa(message: String, context: Context): String {
        var pub_key: String? = context.let { it1 -> readFile(it1) }
        val decoded: ByteArray = Base64.getMimeDecoder().decode(pub_key.toString().trim())
        val pubKeySpec =
            X509EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey: PublicKey = keyFactory.generatePublic(pubKeySpec)
        val cipher: Cipher = Cipher.getInstance("RSA", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        val cipherText: ByteArray = cipher.doFinal(message.toByteArray(Charsets.UTF_8))

        return Base64.getEncoder().encodeToString(cipherText).toString()

    }


}
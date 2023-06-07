package com.sctgold.android.tradeplus.util

import android.content.Context
import android.provider.Settings
import com.sctgold.wgold.android.rest.ServiceApiSpot
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.Socket
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ManageData {


    fun getToken(): String? {
        return FirebaseInstanceId.getInstance().token
    }

    fun getDeviceId(myContext: Context): String {
        var deviceId = ""
        deviceId = Settings.Secure.getString(
            myContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return deviceId
    }

    fun getMd5Base64(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val md5Base16 = BigInteger(1, md.digest(input.toByteArray(Charsets.UTF_8))).toString(16)
            .padStart(32, '0')
        val bytes = md5Base16.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return Base64.getMimeEncoder().encodeToString(bytes)
    }

    fun getMd5Base64a(encTarget: String): String? {
        val mdEnc: MessageDigest?
        try {
            mdEnc = MessageDigest.getInstance("MD5")
            // Encryption algorithmy
            val md5Base16 = BigInteger(
                1,
                mdEnc.digest(encTarget.toByteArray(Charsets.UTF_8))
            )     // calculate md5 hash
            return Base64.getEncoder().encodeToString(md5Base16.toByteArray())
                .trim()     // convert from base16 to base64 and remove the new line character
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }
    }

    fun addSpace(_type: String, self: String): String {
        var string: String = self
        var lenght = self.length
        var lenghtType = checkSpace(_type)
        var total = lenghtType - lenght
        if (total < 0) {
            total = 0
        }
        val sb = StringBuilder()
        for (i in 1..total) {
            string = sb.append(" ").toString()
        }
        return string
    }

    private fun checkSpace(_type: String): Int {
        return if (_type == "BRC") {
            5
        } else if (_type == "ACC") {
            10
        } else if (_type == "OID") {
            10
        } else if (_type == "DID") {
            40
        } else if (_type == "PWD") {
            30
        } else if (_type == "NPW") {
            30
        } else if (_type == "PIN") {
            30
        } else if (_type == "NPN") {
            30
        } else if (_type == "TKN") {
            40
        } else {
            0
        }
    }


    fun isBothLettersandDigitAll(word: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Z0-9a-z]+$".toRegex()
        return regex.matches(word)
    }

    fun isBothLettersandDigit(word: String): Boolean {
        val numRegex = "^(?=.*[0-9])[A-Z0-9a-z]+$".toRegex()
        return numRegex.matches(word)
    }

    fun isBothLettersandUpCase(word: String): Boolean {
        val upRegex = "^(?=.*[A-Z])[A-Z0-9a-z]+$".toRegex()
        return upRegex.matches(word)
    }

    fun isBothLettersandAlphaLowCase(word: String): Boolean {
        val lowRegex = "^(?=.*[a-z])[A-Z0-9a-z]+$".toRegex()
        return lowRegex.matches(word)
    }

    fun numberFormatToDouble(data: String): Double {
        var format: NumberFormat = NumberFormat.getInstance(Locale.getDefault())
        var value = format.parse(data)
        return value.toDouble()
    }

    fun numberFormatToDecimal(data: Double): String {
        val df = DecimalFormat("#,##0.00")
        return df.format(data).toString()
    }

    fun splitWebsocket(text: String): String {
        return text.split("|").toString()
    }


    fun checkSessionAuthority(session: String, context: Context) {
        if (session == "INVALID_CREDENTIAL") {
            ServiceApiSpot.logout(context)
        }
    }


    fun getDataCoding(context: Context): String {
        var sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace(
                "BRC",
                PropertiesKotlin.branch
            ) + PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) + PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            )

        return RSAKotlin().decryptRsa(sumText, context)

    }

    fun hasInternetConnection(): Single<Boolean> {
        return Single.fromCallable {
            try {
                // Connect to Google DNS to check for connection
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                socket.connect(socketAddress, timeoutMs)
                socket.close()

                true
            } catch (e: IOException) {
                false
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}

package com.sctgold.goldinvest.android.util

import android.app.Application
import android.app.KeyguardManager
import android.content.Context

import android.hardware.fingerprint.FingerprintManager
import java.security.KeyStore
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

import android.graphics.Typeface
import com.sctgold.goldinvest.android.dto.MobileVersion
import java.lang.Exception
import java.lang.reflect.Field


class AppUtil : Application(){
    val maxIdle = 60 //Idle 20 minutes.

    private val TAG = "APP-UTIL"

    //Alias for our key in the Android Key Store
    private val KEY_NAME = "key_name"
    private val mFingerprintManager: FingerprintManager? = null
    private val mKeyguardManager: KeyguardManager? = null
    private val mKeyStore: KeyStore? = null
    private val mKeyGenerator: KeyGenerator? = null
    private val cipher: Cipher? = null
    private val formatDate_ddMMyyyyy: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val formatDate_yyyyy: SimpleDateFormat = SimpleDateFormat("yyyy", Locale.US)
    private val two_point: DecimalFormat = DecimalFormat("###,##0.00")

    companion object {
        fun isEmpty(obj: String?): Boolean {
            if (obj == null) return true
            return "" == obj
        }


        fun overrideFont(
            context: Context,
            defaultFontNameToOverride: String,
            customFontFileNameInAssets: String
        ) {
            try {
                val customFontTypeface =
                    Typeface.createFromAsset(context.assets, customFontFileNameInAssets)
                val defaultFontTypefaceField: Field =
                    Typeface::class.java.getDeclaredField(defaultFontNameToOverride)
                defaultFontTypefaceField.isAccessible = true
                defaultFontTypefaceField.set(null, customFontTypeface)
            } catch (e: Exception) {
//                LogUtil.debug(
//                    TAG,
//                    "Can not set custom font $customFontFileNameInAssets instead of $defaultFontNameToOverride"
//                )
            }
        }
        fun extractVersion(version: String): MobileVersion? {
            var version = version
            val mobileVersion = MobileVersion()
            version = version.split("-").toTypedArray()[0]
            val tokenizer = StringTokenizer(version, AppConstants.DELIMITOR_DOT)
            var i = 0
            while (tokenizer.hasMoreTokens()) {
                if (i == 0) {
                    mobileVersion.setMajor(parseInt(tokenizer.nextToken()))
                } else if (i == 1) {
                    mobileVersion.setMinor(parseInt(tokenizer.nextToken()))
                } else if (i == 2) {
                    mobileVersion.setBuildNumber(parseInt(tokenizer.nextToken()))
                    break
                }
                i++
            }
            return mobileVersion
        }

        fun parseInt(`val`: String): Int {
            return try {
                `val`.toInt()
            } catch (ex: Exception) {
                ex.printStackTrace()
                0
            }
        }


//        fun getProduct(productCode: String): ProductDTO? {
//            val cache = GoldSpotCache.getInstanceGoldSpotCache()
//                    List<MobileProductSetting> mobileProductSettings = cache.getFullProductList();
//            for (productDTO in cache!!.fullProductList) {
//                if (productCode == productDTO.code) {
//                    return productDTO
//                }
//            }
//            for (productDTO in cache!!.fullProductList) {
//                if (productCode == productDTO.code) {
//                    return productDTO
//                }
//            }
//
////        mobileProductSettings.addAll(cache.getMyProductList());
//            return null
//        }
//    }

    }

}

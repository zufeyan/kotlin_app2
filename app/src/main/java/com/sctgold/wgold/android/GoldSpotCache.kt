package com.sctgold.wgold.android

import android.graphics.Typeface
import com.sctgold.wgold.android.dto.MobileProductSetting
import com.sctgold.wgold.android.dto.ProductPrice
import java.security.PublicKey

import com.sctgold.wgold.android.dto.ProductDTO


class GoldSpotCache {


    private val TAG = "CACHE"

    //    private boolean isPriceSocketConnected;
    private var apiServerURL: String? = null
    private var secureServerURL: String? = null
    var feedURI: String? = null
     var systemURI: String? = null
     var eventURI: String? = null
    private var uploadURI: String? = null
     var branchCode: String? = null
    private var sctAPIURL: String? = null
     val publicKey: PublicKey? = null

     var awesomeFont: Typeface? = null
     var awesomeFont400: Typeface? = null
     var avenirBlackFont: Typeface? = null
     var avenirHeavyFont: Typeface? = null

    //Authentication Data Cache//
     val androidId: String? = null
     var accountId: String? = null
     var pin: String? = null
     var grade: String? = null
     val token: String? = null
     var apiEncryptToken: String? = null
     var isSavePin: String? = null
     var authToken: String? = null

    //    private boolean isServiceStart;
    private var isRegisterToken = false


//    private val moreMenuList: List<MoreMenu> = ArrayList<MoreMenu>()
    private var unNotificationRead = 0

    private var isJustLogin = false
   // val systemInfo: SystemDTO = SystemDTO()
    private val isAfterSplash = false

    private var screenWidth = 0
    private var screenHeight = 0
    private var cachePrefsFileName = "cache_prefs"
    private var masterKeyAlias: String? = null
    val myProductList: List<MobileProductSetting> = ArrayList()
    val fullProductList: List<ProductDTO> = ArrayList()
    var productPrices: List<ProductPrice> = ArrayList()
    private fun GoldSpotCache() {
        //Hidden Constructor to avoid creating multiple cache info.
    }



    companion object {
        private var instance: GoldSpotCache? = null
        fun getInstanceGoldSpotCache(): GoldSpotCache? {
            return instance
        }



    }


    fun initInstance(context: GoldSpotApplication) {
//        instance = getInstanceGoldSpotCache()
//        val applicationInfo: ApplicationInfo? = null
//        instance?.secureServerURL = applicationInfo!!.metaData.getString(context.METAKEY_SECURE_BASE_URL)
//        instance?.apiServerURL = applicationInfo.metaData.getString(context.METAKEY_API_BASE_URL)
//        instance?.branchCode = applicationInfo.metaData.getInt(context.METAKEY_BRANCH).toString()
////      /  instance?.feedURI = PropertiesKotlin.WEB_SOCKET_URL + PropertiesKotlin.pathPriceRealTime
//        instance?.systemURI = applicationInfo.metaData.getString(context.METAKEY_CONFIG_SOCKET_URL)
//        instance?.eventURI = applicationInfo.metaData.getString(context.METAKEY_EVENT_SOCKET_URL)
//        instance?.uploadURI = applicationInfo.metaData.getString(context.METAKEY_FILE_UPLOAD_URL)

    }

}

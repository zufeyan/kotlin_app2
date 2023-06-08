package com.sctgold.goldinvest.android.util


import android.app.ActivityManager
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Parcel
import android.util.Log
import com.sctgold.goldinvest.android.GoldSpotCache
import com.sctgold.goldinvest.android.dto.*
import com.sctgold.goldinvest.android.service.MobileEventService
import com.sctgold.goldinvest.android.service.MobilePricingService
import com.sctgold.goldinvest.android.service.MobileSystemService
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.CopyOnWriteArrayList


class PropertiesKotlin : Application() {
    companion object {
        private const val TAG = "APP-UTIL"
        const val url = "https://www.sctgoldspot.com/mkkgoldFO/" //115.178.62.204
        const val serverUrl = "https://www.sctgoldspot.com/mkkgoldFO/mobile/api/v3.0/"
        const val WEB_SOCKET_URL = "wss://www.sctgoldspot.com"
        const val pathPriceRealTime = "/mkkgoldFO/primepush/productPrice/700/ONLINE"
        const val pathSystem = "/mkkgoldFO/primepush/mobileSystemUpdate"
        const val pathEvent = "/mkkgoldFO/primepush/mobileEventNotify"
        const val sctUrl = "http://www.sctgoldgroup.com:8080/sctAPIMobile/rest/" //115.178.62.204
        var serverURL: String = "https://www.sctgoldspot.com/mkkgoldFO/mobile/upload"

        var colorBlack ="#ff000000"
        var colorWhite ="#ffffffff"
        var colorMainM ="#ffE2C67F"
        var colorDarkM ="#ffffcc00"
        var colorMainM1 ="#000000"
        var colorDarkM1 ="#EDD185"
        var phoneNumber = "089-129-7478"
        //var phoneNumber = "02-017-0770"
        var tokenStart: String = ""
        var token: String = ""
        var deviceId: String = ""
        const val branch: String = "700"
        var account: String = ""
        var grade: String = ""
        var state: String = ""
        var beforeState: String = ""
        var pdMatch: String = ""

        var spotPriceBuy: String = "0.00"
        var spotPriceSell: String = "0.00"

        var spotPriceDTO: String = ""
        var pdName: String = ""
        var pdCode: String = ""
        var qtyUnit: String = ""
        var currency: String = ""

        //port
        var mobileCusStocks: List<CusStocksDTO> = ArrayList()
        var cashDepositMargin: String = "0.00"
        var cashDeposit: String = "0.00"
        var cashDepositBuy: String = "0.00"
        var holdMoney: String = "0.00"
        var holdMoneyTmp: String = "0.00"
        var arAmount: String = "0.00"
        var apAmount: String = "0.00"
        var mobileCustomerPortAccruedBacklog: CopyOnWriteArrayList<CustomerPortAccruedBacklogDTO> = CopyOnWriteArrayList()

        var backupPriceProductListDTO: CopyOnWriteArrayList<BackupPriceProductDTO> = CopyOnWriteArrayList()
        var systemInfo: SystemDTO = SystemDTO(Parcel.obtain())

        var test: ArrayList<ProductPriceDTO> = ArrayList()

        var myProductUpdate: MutableList<String> = ArrayList()

        var checkMode: Boolean = false
        var checkTradeType: Boolean = false
        var checkPortTab = 0

        fun isConnected(context: Context?): Boolean {
            if (isNetworkAvailable(context)) {
                return true
            } else {
                Log.e(TAG, "No network available!")
            }
            return false
        }

        fun isNetworkAvailable(context: Context?): Boolean {
            Log.e(TAG, "begin isConnected ")
            if (context == null) {
                return false
            }
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

        fun manageSocket(context: Context) {
            Log.e(TAG, "manageSocket")
            startAllBackGroundService(context)
//            val ft: FragmentTransaction =
//                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//            ft.detach(HomeFragment()).attach(HomeFragment()).commit()
//            Log.e(TAG, "startAllBackGroundService")
        }

        fun startAllBackGroundService(context: Context) {
            val cache: GoldSpotCache? = GoldSpotCache.getInstanceGoldSpotCache()
            val pricingIntent = Intent(context, MobilePricingService::class.java)
            pricingIntent.putExtra(AppConstants.EXTRA_FEED_URI, cache?.feedURI)
            pricingIntent.putExtra(AppConstants.EXTRA_GRADE, cache?.grade)
            pricingIntent.putExtra(AppConstants.EXTRA_ACCOUNT_ID, cache?.accountId)
            context.startService(pricingIntent)

            val systemIntent = Intent(context, MobileSystemService::class.java)
            systemIntent.putExtra(AppConstants.EXTRA_SYSTEM_URI, cache?.systemURI)
            systemIntent.putExtra(AppConstants.EXTRA_ACCOUNT_ID, cache?.accountId)
            context.startService(systemIntent)

            val eventIntent = Intent(context, MobileEventService::class.java)
            eventIntent.putExtra(AppConstants.EXTRA_EVENT_URI, cache?.eventURI)
            eventIntent.putExtra(AppConstants.EXTRA_ACCOUNT_ID, cache?.accountId)
            context.startService(eventIntent)
        }

        fun stopAllBackGroundService(context: Context) {
            //   printServiceRunning(context)
            //   LogUtil.debug(TAG, "stopAllBackGroundService")
            val pricingIntent = Intent(context, MobilePricingService::class.java)
            context.stopService(pricingIntent)
            // LogUtil.debug(TAG, "pricingIntent stop")
            val systemIntent = Intent(context, MobileSystemService::class.java)
            context.stopService(systemIntent)
            val eventIntent = Intent(context, MobileEventService::class.java)
            context.stopService(eventIntent)
        }

        fun printServiceRunning(context: Context) {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                // LogUtil.debug(TAG, "serviceClass.getName() : {}", service.service.className)
            }
        }

        fun compress(bitmap: Bitmap, bos: ByteArrayOutputStream) {
            var percentCompress = 100
            do {
                bos.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, percentCompress, bos)
                percentCompress -= 10
            } while (bos.size() * 0.001 > 500)
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        //      LogUtil.debug(TAG, "-- begin calculate sample size --")
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        //  LogUtil.debug(TAG, "-- end calculate sample size, return: {} --", inSampleSize)
        return inSampleSize
    }


    //Scale Image when selected from
    @Throws(Exception::class)
    fun scaleImage(contentURI: Uri, bos: ByteArrayOutputStream, context: Context) {
        var imageStream: InputStream? = null
        try {
            //Get Image bound from stream
            imageStream = context.contentResolver?.openInputStream(contentURI)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(imageStream, null, options)
            imageStream?.close()

            //find in Sample Size
            options.inSampleSize = calculateInSampleSize(
                options, AppConstants.UPLOAD_IMG_WIDTH,
                AppConstants.UPLOAD_IMG_HEIGHT
            )

            //Decode bitmap again to get scale expected size;
            options.inJustDecodeBounds = false
            imageStream = context.contentResolver.openInputStream(contentURI)
            val bitmap = BitmapFactory.decodeStream(imageStream, null, options)
            if (bitmap != null) {
                compress(bitmap, bos)
            }
        } finally {
            if (imageStream != null) imageStream.close()
        }
        //  LogUtil.debug("TAG", "return File Size: {}", bos.size() * 0.001)
    }

    //Scale Image from camera
    @Throws(Exception::class)
    fun scaleImage(filePath: String?, bos: ByteArrayOutputStream) {
        //Decode bitmap to get boundary
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        //find in Sample Size
        options.inSampleSize = calculateInSampleSize(
            options, AppConstants.UPLOAD_IMG_WIDTH,
            AppConstants.UPLOAD_IMG_HEIGHT
        )

        //Decode bitmap again to get scale expected size;
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        compress(bitmap, bos)
        //  LogUtil.debug("TAG", "return File Size: {}", bos.size() * 0.001)
    }

    fun resetAuthenDataPin(context: Context?) {
        account = null.toString()
        token = null.toString()
        grade = null.toString()
//        pin = null
//        isSavePin = java.lang.Boolean.FALSE.toString()
//        val prefValue: MutableMap<String, String> = HashMap()
//        prefValue["accountId"] = accountId
//        prefValue["token"] = token
//        prefValue["grade"] = grade
//        prefValue["pin"] = pin
//        prefValue["isSavePin"] = isSavePin
//        editSecuritySharedPref(context, prefValue)
    }

    fun getVersionName(ctx: Context): String? {
        //Check Version//
        var appVersionName: String? = "0.0.0"
        try {
            appVersionName = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            //  LogUtil.debug(AppUtil.TAG, "Current Version Code: {}", appVersionName)
        } catch (ex: java.lang.Exception) {
            // LogUtil.error(AppUtil.TAG, "Exception when get versionCode: {}", ex)
        }
        return appVersionName
    }

    fun launchAppStore(context: Context) {
        val packageName = context.packageName
        var intent: Intent? = null
        intent = try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        } catch (anfe: ActivityNotFoundException) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        }
        context.startActivity(intent)
    }

}

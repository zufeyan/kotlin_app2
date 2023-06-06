package com.sctgold.ltsgold.android.service

import android.app.Service
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sctgold.ltsgold.android.GoldSpotCache
import com.sctgold.ltsgold.android.dto.BackupPriceProductDTO
import com.sctgold.ltsgold.android.dto.ServiceStatus
import com.sctgold.ltsgold.android.dto.SpotPriceDTO
import com.sctgold.ltsgold.android.util.AppConstants
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.gson.Gson
import com.neovisionaries.ws.client.*
import java.util.concurrent.CopyOnWriteArrayList

class MobilePricingService : Service() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var cache: GoldSpotCache? = null
    var isServiceStarting = false
    private val mBinder: IBinder = PricingLocalBinder()
    private var feedUri: String? = null
    var customerGrade : String? = "A"
    private var accountId: String? = null
    private var serviceStartId = 0


    inner class PricingLocalBinder : Binder() {
        val service: MobilePricingService
            get() = this@MobilePricingService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (pricingWSHandler == null) {
            pricingWSHandler = MobilePricingWSHandler()
        }
    }

    fun checkBeforeStartService(): Boolean {
        return status !== ServiceStatus.STARTING
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (checkBeforeStartService()) {
            status = ServiceStatus.STARTING
            isServiceStarting = true
            serviceStartId = startId
            handler = Handler()
            cache = GoldSpotCache.getInstanceGoldSpotCache()
            super.onStartCommand(intent, flags, startId)

            val app: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName,
                PackageManager.GET_META_DATA
            )
//            val bundle: Bundle = app.metaData

//            if (intent.extras != null) {
                feedUri = PropertiesKotlin.WEB_SOCKET_URL+ PropertiesKotlin.pathPriceRealTime
                customerGrade = PropertiesKotlin.grade
                accountId = PropertiesKotlin.account
//            } else {
//                val sharedPreferences = getSharedPreferences(
//                    getString(R.string.key_prefs_session_initialized),
//                    MODE_PRIVATE
//                )
//                feedUri = PropertiesKotlin.WEB_SOCKET_URL+PropertiesKotlin.pathPriceRealTime
//                customerGrade = PropertiesKotlin.grade
//                accountId = PropertiesKotlin.account
//            }
          //  stopServiceSelf(startId)
            startFeedListener()
        } else {
            Log.e(TAG, "stopSelf : {}$startId")
        }
        return START_NOT_STICKY
    }

    fun stopServiceSelf(startId: Int) {
        for (i in startId - 1 downTo 1) {
            stopSelf(i)
        }
    }

    fun startFeedListener() {
        start(AppConstants.SOCKET_RETRY_DELAY)
    }

    private fun reconnectFeed() {
        if (isServiceStarting && status !== ServiceStatus.DISCONNECT) {
            start(AppConstants.SOCKET_RETRY_DELAY_AFTER_LOGIN)
        } else {
            status = ServiceStatus.DISCONNECT
        }
    }

    private fun start(delaySecond: Int) {
        if (feedUri != null && customerGrade != null && accountId != null) {
            runnable = Runnable {
                Thread(
                    starter, MobilePricingStarter(
                        "$feedUri/$customerGrade"
                    )
                ).start()
            }
            handler!!.postDelayed(runnable!!, delaySecond.toLong())
        }
    }

    override fun onDestroy() {
        isServiceStarting = false
        status = ServiceStatus.DISCONNECT
        disconnectSocket()
        super.onDestroy()
    }

    fun disconnectSocket() {
        if (ws != null && ws!!.isOpen) {
            ws!!.disconnect()
            ws = null
        }
    }

    private fun publishResults(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>) {
        val intent = Intent(FEED_UPDATE_NOTIFICATION)
        intent.putExtra(FEED_UPDATE, productPrices)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    internal inner class MobilePricingStarter(uri: String?) : Runnable {
        var uri: String? = null
        override fun run() {
            try {
                if (ws != null) {
                    ws!!.clearListeners()
                    disconnectSocket()
                }
                ws = factory.createSocket(
                    "$feedUri/$customerGrade"
                )

                ws?.addListener(pricingWSHandler)
                ws?.connect()
            } catch (ex: Exception) {
                ex.printStackTrace()
                reconnectFeed()
            }
        }

        init {
            this.uri = uri
        }
    }

    //Handle Incomming Feed Price
    internal inner class MobilePricingWSHandler : WebSocketAdapter() {
        @Throws(Exception::class)
        override fun onConnected(websocket: WebSocket, headers: Map<String, List<String>>) {
            super.onConnected(websocket, headers)
            isServiceStarting = true
            status = ServiceStatus.STARTED
        }

        @Throws(Exception::class)
        override fun onTextMessage(websocket: WebSocket, message: String) {
            isServiceStarting = true
            Thread(processor, MobilePricingProcessor(message)).start()
        }

        @Throws(Exception::class)
        override fun handleCallbackError(websocket: WebSocket, cause: Throwable) {
            super.handleCallbackError(websocket, cause)
        }

        @Throws(Exception::class)
        override fun onCloseFrame(websocket: WebSocket, frame: WebSocketFrame) {
            super.onCloseFrame(websocket, frame)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onError(websocket: WebSocket, cause: WebSocketException) {
            super.onError(websocket, cause)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onMessageError(
            websocket: WebSocket,
            cause: WebSocketException,
            frames: List<WebSocketFrame>
        ) {
            super.onMessageError(websocket, cause, frames)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onUnexpectedError(websocket: WebSocket, cause: WebSocketException) {
            super.onUnexpectedError(websocket, cause)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onStateChanged(websocket: WebSocket, newState: WebSocketState) {
            super.onStateChanged(websocket, newState)
            if (WebSocketState.OPEN != newState && WebSocketState.CONNECTING != newState) {
                reconnect()
            }
        }

        private fun reconnect() {
            if (isServiceStarting) {
                if (status !== ServiceStatus.RECONNECTING && status !== ServiceStatus.DISCONNECT) {
                    status = ServiceStatus.RECONNECTING
                    reconnectFeed()
                }
            } else {
                disconnectSocket()
            }
        }
    }

    internal inner class MobilePricingProcessor(text: String?) : Runnable {
        var incommingText = text
        override fun run() {
//            if (incommingText != null && incommingText != "X") {
                val toObjStr =
                    if (incommingText!!.contains(AppConstants.DELIMITOR_JSON_TEXT_WS)) incommingText!!.substring(
                        incommingText!!.indexOf(AppConstants.DELIMITOR_JSON_TEXT_WS) + 1
                    ) else incommingText!!
                try {

                   val gson = Gson()
                    val finalPriceView: SpotPriceDTO =
                        gson.fromJson(toObjStr, SpotPriceDTO::class.java)
                    var productPrices: ArrayList<BackupPriceProductDTO>
                    var backupPriceProductDTO: BackupPriceProductDTO
                    PropertiesKotlin.test = finalPriceView.productPriceDTOs
                    for (productPriceServer in finalPriceView.productPriceDTOs) {
                        PropertiesKotlin.backupPriceProductListDTO = CopyOnWriteArrayList()
                        productPrices = ArrayList()
                        for (pd in finalPriceView.productPriceDTOs) {
                            backupPriceProductDTO = BackupPriceProductDTO()
                            backupPriceProductDTO.productCode = pd.productDTO.code.toString()
                            backupPriceProductDTO.statusFreeze = pd.freeze
                            backupPriceProductDTO.buyPriceNew = pd.buyPrice.toString()
                            backupPriceProductDTO.sellPriceNew = pd.sellPrice.toString()
                            PropertiesKotlin.backupPriceProductListDTO.add(backupPriceProductDTO)
                            productPrices.add(backupPriceProductDTO)

                        }


                    }

                    publishResults( PropertiesKotlin.backupPriceProductListDTO)

                } catch (ex: Exception) {

                    Log.e(TAG, "Input Document ex : {}$ex")
                }
//            }
        }

        init {
            incommingText = text
        }
    }

    companion object {
        private const val TAG = "price_feed"
        const val FEED_UPDATE_NOTIFICATION = "com.clevel.tmgold.android.service.FEED_UPDATE"
        const val FEED_UPDATE = "feed_update"
        var status = ServiceStatus.DISCONNECT

        private var pricingWSHandler: MobilePricingWSHandler? = null
        private val starter = ThreadGroup("PricingStater")
        private val processor = ThreadGroup("PricingProcessor")
        private val factory = WebSocketFactory().setConnectionTimeout(500)
        private var ws: WebSocket? = null
    }
}
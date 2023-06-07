package com.sctgold.wgold.android.service


import android.app.Service
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sctgold.wgold.android.dto.ServiceStatus
import com.sctgold.wgold.android.dto.SystemDTO
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.sctgold.wgold.android.util.AppConstants
import com.google.gson.Gson
import com.neovisionaries.ws.client.*


class MobileSystemService  : Service() {
    private val mBinder: IBinder = SystemServiceBinder()

    inner class SystemServiceBinder : Binder() {
        val service: MobileSystemService
            get() = this@MobileSystemService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (systemWSHandler == null) {
            systemWSHandler = MobileSystemWSHandler()
        }
    }

    fun checkBeforeStartService(): Boolean {
        return status !== ServiceStatus.STARTING
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (checkBeforeStartService()) {
            status = ServiceStatus.STARTING
            super.onStartCommand(intent, flags, startId)
            isServiceStarting = true
            handler = Handler()
            val app: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle: Bundle = app.metaData
//            if (bundle != null) {
//                systemURI = bundle!!.getString("com.clevel.tmgold.android.CONFIG_SOCKET_URL")
                systemURI = PropertiesKotlin.WEB_SOCKET_URL+ PropertiesKotlin.pathSystem
                accountId = PropertiesKotlin.account

            Log.e("systemURI : ", systemURI.toString())
//            } else {
//                val sharedPreferences = getSharedPreferences(
//                    getString(R.string.key_prefs_session_initialized),
//                    MODE_PRIVATE
//                )
//                systemURI = PropertiesKotlin.WEB_SOCKET_URL+PropertiesKotlin.pathSystem
//                accountId =PropertiesKotlin.account
//            }
            stopServiceSelf(startId)
            startSystemListener()
        }
        return START_NOT_STICKY
    }

    fun stopServiceSelf(startId: Int) {
        for (i in startId - 1 downTo 1) {
            stopSelf(i)
        }
    }

    override fun onDestroy() {
        isServiceStarting = false
        disconnectSocket()
        super.onDestroy()
    }

    fun disconnectSocket() {
        if (ws != null && ws!!.isOpen) {
            status = ServiceStatus.DISCONNECT
            ws!!.disconnect()
            ws = null
        }
    }

    fun startSystemListener() {
//        disconnectSocket();
        start(AppConstants.SOCKET_RETRY_DELAY)
    }

    fun reconnectSystemListener() {
        if (isServiceStarting && status !== ServiceStatus.DISCONNECT) {
            start(AppConstants.SOCKET_RETRY_DELAY_AFTER_LOGIN)
        } else {
            status = ServiceStatus.DISCONNECT
        }
    }

    private fun start(delaySecond: Int) {
        if (systemURI != null && accountId != null) {
            runnable = Runnable {
                Thread(starter, MobileSystemStarter(systemURI)).start()
            }
            handler!!.postDelayed(runnable!!, delaySecond.toLong())
        }
    }

    private fun publishResults(systemUpdate: SystemDTO) {
        val intent = Intent(SYSTEM_NOTIFICATION)
        intent.putExtra(SYSTEM_ALERT, systemUpdate)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


    }

    internal inner class MobileSystemStarter(uri: String?) : Runnable {
        var uri: String? = null
        override fun run() {
            try {
                if (ws != null) {
                    ws!!.clearListeners()
                    disconnectSocket()
                }
                ws = factory.createSocket(uri)
                ws!!.addListener(systemWSHandler)
                ws!!.connect()
            } catch (ex: Exception) {
             //   LogUtil.error(TAG, "Cannot Open Socket", ex)
                reconnectSystemListener()
            }
        }

        init {
            this.uri = uri
        }
    }

    //Handle Incomming Feed System Info
    internal inner class MobileSystemWSHandler : WebSocketAdapter() {
        @Throws(Exception::class)
        override fun onConnected(websocket: WebSocket?, headers: Map<String?, List<String?>?>?) {
            super.onConnected(websocket, headers)
            status = ServiceStatus.STARTED
        }

        @Throws(Exception::class)
        override fun onTextMessage(websocket: WebSocket?, message: String?) {
            isServiceStarting = true
            Thread(processor, MobileSystemProcessor(message)).start()

            Log.e("MobileSystem message : ",message.toString())
        }
        @Throws(Exception::class)
        override fun onCloseFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
            super.onCloseFrame(websocket, frame)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
            super.onError(websocket, cause)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onMessageError(
            websocket: WebSocket?,
            cause: WebSocketException?,
            frames: List<WebSocketFrame?>?
        ) {
            super.onMessageError(websocket, cause, frames)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
            super.onUnexpectedError(websocket, cause)
            reconnect()
        }

        @Throws(Exception::class)
        override fun onStateChanged(websocket: WebSocket?, newState: WebSocketState) {
            super.onStateChanged(websocket, newState)
            if (WebSocketState.OPEN != newState && WebSocketState.CONNECTING != newState) {
                reconnect()
            }
        }

        private fun reconnect() {
            if (isServiceStarting) {
                if (status !== ServiceStatus.RECONNECTING && status !== ServiceStatus.DISCONNECT) {
                    status = ServiceStatus.RECONNECTING
                    reconnectSystemListener()
                }
            } else {
                disconnectSocket()
            }
        }
    }

    internal inner class MobileSystemProcessor(text: String?) : Runnable {
        var incommingText= text
        override fun run() {
            val toObjStr =
                if (incommingText!!.contains(AppConstants.DELIMITOR_JSON_TEXT_WS)) incommingText!!.substring(
                    incommingText!!.indexOf(AppConstants.DELIMITOR_JSON_TEXT_WS) + 1
                ) else incommingText!!
            try {
                val gson = Gson()
               // Log.d("toObjStr", ":  $toObjStr")
                val systemUpdate = gson.fromJson(toObjStr, SystemDTO::class.java)
             //   Log.d("systemUpdate", ":  $systemUpdate")

                publishResults(systemUpdate)
            } catch (ex: Exception) {
                Log.e(TAG," Exception system: $ex")
            }
        }

        init {
            incommingText = text
        }
    }

    companion object {
        private val TAG = "SYS_FEED"

        private var handler: Handler? = null
        private var runnable: Runnable? = null
        val SYSTEM_NOTIFICATION = "com.clevel.tmgold.android.service.SYSTEM_UPDATE_NOTIFICATION"
        val SYSTEM_ALERT = "system_alert"

        var status = ServiceStatus.DISCONNECT
        var isServiceStarting = false


        private var systemURI: String? = null
        private var accountId: String? = null

        private var systemWSHandler: MobileSystemWSHandler? = null

        private val starter = ThreadGroup("SystemStarter")
        private val processor = ThreadGroup("SystemProcessor")

        private val factory = WebSocketFactory().setConnectionTimeout(500)
        private var ws: WebSocket? = null
    }


}

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
import com.sctgold.ltsgold.android.dto.EventDTO
import com.sctgold.ltsgold.android.dto.NotifyEvent
import com.sctgold.ltsgold.android.dto.ServiceStatus
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.sctgold.ltsgold.android.util.AppConstants
import com.google.gson.Gson
import com.neovisionaries.ws.client.*
import com.sctgold.android.tradeplus.util.ManageData


class MobileEventService : Service() {
    private val mBinder: IBinder = EventServiceBinder()
    //NotifyEvent Local Binder Service
    inner class EventServiceBinder : Binder() {
        val service: MobileEventService
            get() = this@MobileEventService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (eventWSHandler == null) {
            eventWSHandler = MobileEventWSHandler()
        }
    }

    fun checkBeforeStartService(): Boolean {
        return status !== ServiceStatus.STARTING
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (checkBeforeStartService()) {
            status = ServiceStatus.STARTING
            isServiceStarting = true
            super.onStartCommand(intent, flags, startId)
            handler = Handler()

            val app: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName,
                PackageManager.GET_META_DATA
            )
//            val bundle: Bundle = app.metaData
//            if (intent != null && intent.extras != null) {
                eventURI = PropertiesKotlin.WEB_SOCKET_URL+ PropertiesKotlin.pathEvent
//                    bundle!!.getString("com.clevel.tmgold.android.EVENT_SOCKET_URL")
//            } else {
                //   LogUtil.debug(TAG, "Start Service using latest Login Info from preferences")
//                val sharedPreferences = getSharedPreferences(
//                    getString(com.clevel.tmgold.android.R.string.key_prefs_session_initialized),
//                    MODE_PRIVATE
//                )
//                sharedPreferences.getString(
//                    getString(com.clevel.tmgold.android.R.string.key_session_eventURL),
//                    null
//                )
//                eventURI =PropertiesKotlin.WEB_SOCKET_URL+PropertiesKotlin.pathEvent
//            }
            stopServiceSelf(startId)
            startEventListener()
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

    fun startEventListener() {
//        disconnectSocket();
        start(AppConstants.SOCKET_RETRY_DELAY)
    }

    fun reconnectEventListener() {

        if (isServiceStarting && status !== ServiceStatus.DISCONNECT) {
            start(AppConstants.SOCKET_RETRY_DELAY_AFTER_LOGIN)
        } else {
            status = ServiceStatus.DISCONNECT
        }
    }

    private fun start(delaySecond: Int) {
        if (eventURI != null) {
//            disconnectSocket();
            //    handler.removeCallbacks(runnable)
            runnable = Runnable {
                Thread(starter, MobileEventStarter(eventURI)).start()
            }
            handler?.postDelayed(runnable!!, delaySecond.toLong())
        }
    }

    fun publishResults(eventNotification: EventDTO) {


        var intent: Intent? = if (NotifyEvent.FORCE_LOGOUT.name == eventNotification.event) {
           var acc = eventNotification.accountNumber?.let { ManageData().splitWebsocket(it) }

            Log.e("eventNotification.event  :",eventNotification.event.toString())

            if(eventNotification.event.equals("FORCE_LOGOUT")){


                if(PropertiesKotlin.account == eventNotification.accountNumber){
                    Intent(FORCE_LOGOUT_NOTIFICATION)
                }else{
                    Intent(FORCE_LOGOUT_NOTIFICATION)
                }


            }else{
                Intent(PRICE_ALERT_UPDATE_NOTIFICATION)
            }
        } else {
            Intent(PRICE_ALERT_UPDATE_NOTIFICATION)
        }
        intent?.putExtra(EVENT_ALERT, eventNotification)
        if (intent != null) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    internal inner class MobileEventStarter(uri: String?) : Runnable {
        var uri: String? = null
        override fun run() {
            try {
                if (ws != null) {
                    ws!!.clearListeners()
                    disconnectSocket()
                }
                ws = factory.createSocket(eventURI)
                ws!!.addListener(eventWSHandler)
                ws!!.connect()
            } catch (ex: Exception) {
                reconnectEventListener()
                   Log.e(TAG, "Cannot Open Event Socket $ex")
            }
        }

        init {
            this.uri = uri
        }
    }

    //Handle Incomming Feed NotifyEvent Info
    internal inner class MobileEventWSHandler : WebSocketAdapter() {
        @Throws(Exception::class)
        override fun onConnected(websocket: WebSocket?, headers: Map<String?, List<String?>?>?) {
            super.onConnected(websocket, headers)
            status = ServiceStatus.STARTED
        }

        @Throws(Exception::class)
        override fun onTextMessage(websocket: WebSocket?, message: String?) {
            isServiceStarting = true
            Thread(processor, MobileEventProcessor(message)).start()
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
        override fun onDisconnected(
            websocket: WebSocket?,
            serverCloseFrame: WebSocketFrame?,
            clientCloseFrame: WebSocketFrame?,
            closedByServer: Boolean
        ) {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
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
                    reconnectEventListener()
                }
            } else {
                disconnectSocket()
            }
        }
    }

    internal inner class MobileEventProcessor(text: String?) : Runnable {
        var incommingText = text
        override fun run() {
            val toObjStr =
                if (incommingText!!.startsWith(AppConstants.DELIMITOR_JSON_TEXT_WS)) incommingText!!.substring(
                    1
                ) else incommingText!!
            try {
                val gson = Gson()
                val eventNotification: EventDTO =
                    gson.fromJson(toObjStr, EventDTO::class.java)
                publishResults(eventNotification)
            } catch (ex: Exception) {
                   Log.e(TAG, "Event Exception: {}$ex")
            }
        }

        init {
            incommingText = text
        }
    }

    companion object {

        val TAG = "EVENT_FEED"
        val FORCE_LOGOUT_NOTIFICATION = "com.clevel.tmgold.android.service.EVENT_FORCE_LOGOUT"
        val PRICE_ALERT_UPDATE_NOTIFICATION =
            "com.clevel.tmgold.android.service.EVENT_PRICE_ALERT_UPDATE"
        val EVENT_ALERT = "event_alert"

        private var handler: Handler? = null
        private var runnable: Runnable? = null
        private var cache: GoldSpotCache? = null
        var status = ServiceStatus.DISCONNECT
        var isServiceStarting = false


        private var eventURI: String? = null

        private val starter = ThreadGroup("EventStarter")
        private val processor = ThreadGroup("EventProcessor")

        private var eventWSHandler: MobileEventWSHandler? = null
        private val factory = WebSocketFactory().setConnectionTimeout(500)
        private var ws: WebSocket? = null
    }

}
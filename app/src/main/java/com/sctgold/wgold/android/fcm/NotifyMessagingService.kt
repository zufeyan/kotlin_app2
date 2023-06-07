package com.sctgold.wgold.android.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.rest.MobileFrontService
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*

class NotifyMessagingService :FirebaseMessagingService() {

    private val TAG = "NOTI-RECEIVE"

    val PRICE_ALERT = "com.clevel.tmgold.android.service.PRICE_ALERT"

    private var mNotificationManager: NotificationManager? = null

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        //LogUtil.debug(TAG, "Refreshed token: {}", refreshedToken)
        startApplication(refreshedToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
      //  LogUtil.debug(TAG, "From: {}, id: {}", remoteMessage.from, remoteMessage.messageId)
        if (remoteMessage.data.size > 0) {
            val data = remoteMessage.data
           // LogUtil.debug(TAG, "{}, {}, {}, {}", remoteMessage.data.values)
            if (data["message"] != null) {
             //   LogUtil.debug(TAG, "Message data payload: " + remoteMessage.data)
                sendNotification(remoteMessage.messageId, "Notice", data["message"])
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
         //   LogUtil.debug(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            val notification = remoteMessage.notification
            sendNotification(remoteMessage.messageId, notification!!.title, notification.body)
        } else {
         //   LogUtil.debug(TAG, "Notification Null")
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(id: String?, title: String?, body: String?) {
    //    LogUtil.debug(TAG, "sendNotification id : {}", id)
        val intent = Intent(this, NotifyMessagingService::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, Calendar.getInstance().timeInMillis.toInt(), intent,
            PendingIntent.FLAG_MUTABLE
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(ContextCompat.getColor(this, R.color.white))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        mNotificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "YOUR_CHANNEL_ID"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager!!.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        if ("PriceAlert" == title) {
            notificationBuilder.priority = Notification.PRIORITY_HIGH
            val priceAlertIntent = Intent(PRICE_ALERT)
            LocalBroadcastManager.getInstance(this).sendBroadcast(priceAlertIntent)
        }
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun startApplication(clientToken: String?) {
        var jsonobj = JSONObject()
        jsonobj.put("os", "AND")
        jsonobj.put("deviceId", PropertiesKotlin.deviceId)
        jsonobj.put("clientToken", PropertiesKotlin.tokenStart)
        val jsonObjectString = jsonobj.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        Log.e("startApplication: ", PropertiesKotlin.tokenStart)
        CoroutineScope(Dispatchers.IO).launch {
            val api = MobileFrontService().startApp(requestBody)
            api.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.e("SUCCESS :", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ERROR", t.message.toString())
                }

            })

        }

    }

//    private fun sendRegistrationToServer(clientToken: String?) {
//     //  LogUtil.debug(TAG, "send Registration Token")
//        val cache: GoldSpotCache = GoldSpotCache.getInstance()
//        val mobileFrontService: MobileFrontService = MobileRestServiceGenerator.createAPIService()
//        val startAppData = StartAppData()
//        startAppData.setClientToken(clientToken)
//        startAppData.setDeviceId(cache.getAndroidId())
//        startAppData.setOs(getString(R.string.os_value))
//        var restResponse: RestResponse? = null
//        var retry = 0
//        while (retry++ < AppConstants.CONNECTION_maxRetry && restResponse == null) {
//            try {
//                restResponse = mobileFrontService.startApplication(startAppData)
//            } catch (ex: Exception) {
//                if (retry == AppConstants.CONNECTION_maxRetry) {
//                   // LogUtil.debug(TAG, "Network error cannot sent token to server.")
//                }
//            }
//        }
//        val sharedPreferences =
//            getSharedPreferences(getString(R.string.key_prefs_session_initialized), MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        if (restResponse != null) {
//            when (restResponse.getResponse()) {
//                SUCCESS -> {
//                    editor.putBoolean(getString(R.string.key_session_is_register_token), true)
//                    cache.setRegisterToken(true)
//                }
//                FAILED -> {
//                    editor.putBoolean(getString(R.string.key_session_is_register_token), false)
//                    cache.setRegisterToken(false)
//                }
//                else -> {
//                    editor.putBoolean(getString(R.string.key_session_is_register_token), false)
//                    cache.setRegisterToken(false)
//                }
//            }
//        } else {
//            editor.putBoolean(getString(R.string.key_session_is_register_token), false)
//            cache.setRegisterToken(false)
//        }
//        editor.commit()
//        LogUtil.debug(
//            TAG,
//            "register: {}",
//            sharedPreferences.getBoolean(getString(R.string.key_session_is_register_token), false)
//        )
//    }
}

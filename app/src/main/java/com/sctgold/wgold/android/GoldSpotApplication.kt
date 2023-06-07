package com.sctgold.wgold.android

import android.app.Application
import android.util.Log

import android.app.Activity

import android.os.Bundle
import com.sctgold.wgold.android.GoldSpotCache.Companion.getInstanceGoldSpotCache


class GoldSpotApplication : Application(){

    private val TAG = "APP"
    private lateinit var myInstance: GoldSpotApplication
    var inForeground: Boolean = true
    val METAKEY_HOST_PORT = "com.clevel.tmgold.android.API_HOST_PORT"
    val METAKEY_API_APP_URI = "com.clevel.tmgold.android.API_APP_URI"
    val METAKEY_SECURE_BASE_URL = "com.clevel.tmgold.android.SECURITY_BASE_URL"
    val METAKEY_API_BASE_URL = "com.clevel.tmgold.android.API_BASE_URL"
    val METAKEY_BRANCH = "com.clevel.tmgold.android.BRANCH_CODE"
    val METAKEY_FEED_SOCKET_URL = "com.clevel.tmgold.android.FEED_SOCKET_URL"
    val METAKEY_CONFIG_SOCKET_URL = "com.clevel.tmgold.android.CONFIG_SOCKET_URL"
    val METAKEY_EVENT_SOCKET_URL = "com.clevel.tmgold.android.EVENT_SOCKET_URL"
    val METAKEY_FILE_UPLOAD_URL = "com.clevel.tmgold.android.FILE_UPLOAD_URL"
    val METAKEY_SCT_API_URL = "com.clevel.tmgold.android.SCT_API_URL"
    val METAKEY_MORE_MENU_VISIBLE = "com.clevel.tmgold.android.MORE_MENU_VISIBLE"





    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate -- load GoldSpotApplication -- ")
//        LogUtil.debug(TAG, "-- onCreate --")
        myInstance = this
     //   initialValue()
//        AppUtil.overrideFont(applicationContext, "SERIF", "fonts/Roboto-Regular.ttf")
//        registerActivityLifecycleCallbacks(GoldSpotActivityLifecycleCallbacks())
    }

    fun initialValue() {
//        LogUtil.debug(TAG, "Entering initialValue")
        getInstanceGoldSpotCache()?.initInstance(myInstance)
    }




    companion object{

        fun getInstance(): GoldSpotApplication = GoldSpotApplication()
    }

    private class GoldSpotActivityLifecycleCallbacks :
        ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            //No Need to do anything here
        }

        override fun onActivityDestroyed(activity: Activity) {
            //No Need to do anything here
        }

        override fun onActivityPaused(activity: Activity) {
            getInstance().inForeground = true
        }

        override fun onActivityResumed(activity: Activity) {
            GoldSpotApplication().inForeground   = true
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            //No Need to do anything here;
        }

        override fun onActivityStarted(activity: Activity) {
            //No need to do anything here;
        }

        override fun onActivityStopped(activity: Activity) {
            //No need to do anything here;
        }
    }

}

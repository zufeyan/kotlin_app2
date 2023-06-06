package com.sctgold.ltsgold.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.ltsgold.android.activities.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MainActivity.setNotification(context, true)
    }

}
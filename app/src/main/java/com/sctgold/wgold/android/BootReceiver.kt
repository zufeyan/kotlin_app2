package com.sctgold.wgold.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.wgold.android.activities.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MainActivity.setNotification(context, true)
    }

}

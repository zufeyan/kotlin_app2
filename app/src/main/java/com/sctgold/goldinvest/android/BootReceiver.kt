package com.sctgold.goldinvest.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.goldinvest.android.activities.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MainActivity.setNotification(context, true)
    }

}

package com.sctgold.ltsgold.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.ltsgold.android.dto.SystemDTO
import com.sctgold.ltsgold.android.listener.OnSystemUpdateListener
import com.sctgold.ltsgold.android.service.MobileSystemService
import com.sctgold.ltsgold.android.util.PropertiesKotlin


class MobileSystemReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val systemUpdate: SystemDTO =
            intent.extras!!.get(MobileSystemService.SYSTEM_ALERT) as SystemDTO
        if (systemUpdate != null) {
            onSystemUpdateListener?.onSystemUpdate(systemUpdate)
            PropertiesKotlin.systemInfo = systemUpdate
        }
    }

    companion object {
        var onSystemUpdateListener: OnSystemUpdateListener? = null
    }
}
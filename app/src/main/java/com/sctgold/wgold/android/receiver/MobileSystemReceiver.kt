package com.sctgold.wgold.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.wgold.android.dto.SystemDTO
import com.sctgold.wgold.android.listener.OnSystemUpdateListener
import com.sctgold.wgold.android.service.MobileSystemService
import com.sctgold.wgold.android.util.PropertiesKotlin


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

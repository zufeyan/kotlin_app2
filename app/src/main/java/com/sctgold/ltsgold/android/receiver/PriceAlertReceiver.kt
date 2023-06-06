package com.sctgold.ltsgold.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.ltsgold.android.listener.OnPriceAlertListener

class PriceAlertReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        if (onPriceAlertListener != null ) {
            onPriceAlertListener!!.priceAlert()
        }
    }

    companion object {
        var onPriceAlertListener: OnPriceAlertListener? = null
    }
}
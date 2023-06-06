package com.sctgold.ltsgold.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.ltsgold.android.dto.EventDTO
import com.sctgold.ltsgold.android.listener.OnEventUpdateListener
import com.sctgold.ltsgold.android.service.MobileEventService


class MobileEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if(onEventUpdateListener != null){
            val eventNotification: EventDTO? =
                intent.extras!!.get(MobileEventService.EVENT_ALERT) as EventDTO
            if(eventNotification != null ){
                onEventUpdateListener?.onEventUpdate(eventNotification)
            }


        }

    }

    companion object {
        var onEventUpdateListener: OnEventUpdateListener? = null
    }
}
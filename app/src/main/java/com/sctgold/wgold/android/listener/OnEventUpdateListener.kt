package com.sctgold.wgold.android.listener

import com.sctgold.wgold.android.dto.EventDTO

interface OnEventUpdateListener {
    fun onEventUpdate(eventDTO: EventDTO?)
}

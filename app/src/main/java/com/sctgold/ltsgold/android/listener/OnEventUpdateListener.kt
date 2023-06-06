package com.sctgold.ltsgold.android.listener

import com.sctgold.ltsgold.android.dto.EventDTO

interface OnEventUpdateListener {
    fun onEventUpdate(eventDTO: EventDTO?)
}
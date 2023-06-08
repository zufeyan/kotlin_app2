package com.sctgold.goldinvest.android.listener

import com.sctgold.goldinvest.android.dto.EventDTO

interface OnEventUpdateListener {
    fun onEventUpdate(eventDTO: EventDTO?)
}

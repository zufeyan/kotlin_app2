package com.sctgold.ltsgold.android.listener

import com.sctgold.ltsgold.android.dto.PriceAlertDetailDTO

interface OnPADeleteListener {
    fun onDelete(mobilePriceAlert: PriceAlertDetailDTO?)

}
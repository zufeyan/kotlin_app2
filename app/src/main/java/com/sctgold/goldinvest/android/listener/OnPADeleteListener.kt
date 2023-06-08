package com.sctgold.goldinvest.android.listener

import com.sctgold.goldinvest.android.dto.PriceAlertDetailDTO

interface OnPADeleteListener {
    fun onDelete(mobilePriceAlert: PriceAlertDetailDTO?)

}

package com.sctgold.ltsgold.android.listener

import com.sctgold.ltsgold.android.dto.BackupPriceProductDTO
import java.util.concurrent.CopyOnWriteArrayList


interface OnPriceFeedListener {
    fun onPricePush(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>)
}
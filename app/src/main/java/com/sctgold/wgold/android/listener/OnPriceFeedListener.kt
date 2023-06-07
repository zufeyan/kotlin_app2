package com.sctgold.wgold.android.listener

import com.sctgold.wgold.android.dto.BackupPriceProductDTO
import java.util.concurrent.CopyOnWriteArrayList


interface OnPriceFeedListener {
    fun onPricePush(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>)
}

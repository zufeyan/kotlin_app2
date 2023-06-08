package com.sctgold.goldinvest.android.listener

import com.sctgold.goldinvest.android.dto.BackupPriceProductDTO
import java.util.concurrent.CopyOnWriteArrayList


interface OnPriceFeedListener {
    fun onPricePush(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>)
}

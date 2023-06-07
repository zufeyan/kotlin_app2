package com.sctgold.wgold.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sctgold.wgold.android.dto.BackupPriceProductDTO
import com.sctgold.wgold.android.listener.OnPriceFeedListener
import com.sctgold.wgold.android.service.MobilePricingService
import com.sctgold.wgold.android.util.PropertiesKotlin
import java.util.concurrent.CopyOnWriteArrayList


class MobilePricingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val productPrices =
            intent.extras!!.get(MobilePricingService.FEED_UPDATE) as CopyOnWriteArrayList<BackupPriceProductDTO>
        if (onPriceFeedListener != null) {
            onPriceFeedListener?.onPricePush(productPrices)
            PropertiesKotlin.backupPriceProductListDTO = productPrices
        }
    }

    companion object {
        var onPriceFeedListener: OnPriceFeedListener? = null
    }


}

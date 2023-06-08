package com.sctgold.goldinvest.android.dto

import com.sctgold.goldinvest.android.util.AppUtil

class ProductPrice {
    private var product: MobileProductSetting? = null
    private var sellPrice = 0.0
    private var buyPrice = 0.0
    private var freeze = false

    constructor(
        product: MobileProductSetting?,
        sellPrice: Double,
        buyPrice: Double,
        freeze: String
    ) {
        this.product = product
        this.sellPrice = sellPrice
        this.buyPrice = buyPrice

        if(AppUtil.isEmpty(freeze)) {
            this.freeze = true
        } else {
            this.freeze = freeze.toBoolean()
        }
    }

    fun getProduct(): MobileProductSetting? {
        return product
    }

    fun setProduct(product: MobileProductSetting?) {
        this.product = product
    }

    fun getSellPrice(): Double {
        return sellPrice
    }

    fun setSellPrice(sellPrice: Double) {
        this.sellPrice = sellPrice
    }

    fun getBuyPrice(): Double {
        return buyPrice
    }

    fun setBuyPrice(buyPrice: Double) {
        this.buyPrice = buyPrice
    }

    fun isFreeze(): Boolean {
        return freeze
    }

    fun setFreeze(freeze: Boolean) {
        this.freeze = freeze
    }

    override fun toString(): String {
        val sb = StringBuffer("ProductPrice{")
        sb.append("product=").append(product)
        sb.append(", sellPrice=").append(sellPrice)
        sb.append(", buyPrice=").append(buyPrice)
        sb.append(", freeze=").append(freeze)
        sb.append('}')
        return sb.toString()
    }
}

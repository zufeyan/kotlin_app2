package com.sctgold.ltsgold.android.dto

class ProductPriceServer {
    private var sellPrice = 0.0
    private var buyPrice = 0.0
    private var freeze: String? = null
    private var productDTO: ProductServer? = null

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

    fun getFreeze(): String? {
        return freeze
    }

    fun setFreeze(freeze: String?) {
        this.freeze = freeze
    }

    fun getProductDTO(): ProductServer? {
        return productDTO
    }

    fun setProductDTO(productDTO: ProductServer?) {
        this.productDTO = productDTO
    }

    override fun toString(): String {
        val sb = StringBuffer("ProductPriceServer{")
        sb.append("sellPrice=").append(sellPrice)
        sb.append(", buyPrice=").append(buyPrice)
        sb.append(", freeze='").append(freeze).append('\'')
        sb.append(", productDTO=").append(productDTO)
        sb.append('}')
        return sb.toString()
    }
}
package com.sctgold.ltsgold.android.dto

class FinalPriceView {
    private var customerGrade: String? = null
    private var productPriceDTOs: List<ProductPriceServer>? = null
    private var pushTime: Long = 0
    private var startCalculate: Long = 0

    fun getCustomerGrade(): String? {
        return customerGrade
    }

    fun setCustomerGrade(customerGrade: String?) {
        this.customerGrade = customerGrade
    }

    fun getProductPriceDTOs(): List<ProductPriceServer?>? {
        return productPriceDTOs
    }

    fun setProductPriceDTOs(productPriceDTOs: List<ProductPriceServer>?) {
        this.productPriceDTOs = productPriceDTOs
    }

    fun getPushTime(): Long {
        return pushTime
    }

    fun setPushTime(pushTime: Long) {
        this.pushTime = pushTime
    }

    fun getStartCalculate(): Long {
        return startCalculate
    }

    fun setStartCalculate(startCalculate: Long) {
        this.startCalculate = startCalculate
    }

    override fun toString(): String {
        val sb = StringBuffer("FinalPriceView{")
        sb.append("customerGrade='").append(customerGrade).append('\'')
        sb.append(", productPriceDTOs=").append(productPriceDTOs)
        sb.append(", pushTime=").append(pushTime)
        sb.append(", startCalculate=").append(startCalculate)
        sb.append('}')
        return sb.toString()
    }

}
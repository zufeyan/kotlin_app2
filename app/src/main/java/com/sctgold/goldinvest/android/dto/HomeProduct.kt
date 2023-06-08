package com.sctgold.goldinvest.android.dto

import com.google.gson.annotations.SerializedName

data class HomeProduct (
    @SerializedName("product") var product: MobileProductSetting,
    @SerializedName("view") var view: Boolean,
    @SerializedName("buyPrice") var buyPrice: Double,
    @SerializedName("sellPrice") var sellPrice: Double,
    @SerializedName("buyPriceDiff") var buyPriceDiff: Double,
    @SerializedName("sellPriceDiff") var sellPriceDiff: Double,
    @SerializedName("isSystemUpdate") var isSystemUpdate: Boolean,
    @SerializedName("isSystemClose") var isSystemClose: Boolean,
)

//    @SerializedName("product") var product: MobileProductSetting,
//    @SerializedName("view") var view: Boolean,
//    @SerializedName("buyPrice") var buyPrice: Double,
//    @SerializedName("sellPrice") var sellPrice: Double,
//    @SerializedName("buyPriceDiff") var buyPriceDiff: Double,
//    @SerializedName("sellPriceDiff") var sellPriceDiff: Double,
//    @SerializedName("isSystemUpdate") var isSystemUpdate: Boolean,
//    @SerializedName("isSystemClose") var isSystemClose: Boolean,

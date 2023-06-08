package com.sctgold.goldinvest.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TradeDTO (
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("mobileTrade") var mobileTrade: TradeDetailDTO
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("mobileTrade")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(response)
        parcel.writeString(message)
        parcel.writeString(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeDTO> {
        override fun createFromParcel(parcel: Parcel): TradeDTO {
            return TradeDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class TradeDetailDTO (
    @SerializedName("orderId") var orderId: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("tradeType") var tradeType: String?,
    @SerializedName("tradeSide") var tradeSide: String?,
    @SerializedName("spotPrice") var spotPrice: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("quantity") var quantity: String?,
    @SerializedName("amount") var amount: String?,
    @SerializedName("orderDate") var orderDate: String?,
    @SerializedName("confirmInterval") var confirmInterval: String?,
    @SerializedName("currencyCode") var currencyCode: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(productCode)
        parcel.writeString(tradeType)
        parcel.writeString(tradeSide)
        parcel.writeString(spotPrice)
        parcel.writeString(price)
        parcel.writeString(quantity)
        parcel.writeString(amount)
        parcel.writeString(orderDate)
        parcel.writeString(confirmInterval)
        parcel.writeString(currencyCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeDetailDTO> {
        override fun createFromParcel(parcel: Parcel): TradeDetailDTO {
            return TradeDetailDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeDetailDTO?> {
            return arrayOfNulls(size)
        }
    }

}


data class TradeConfirmDTO (
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("ivNumber") var ivNumber: String?

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(response)
        parcel.writeString(message)
        parcel.writeString(timeStamp)
        parcel.writeString(ivNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeConfirmDTO> {
        override fun createFromParcel(parcel: Parcel): TradeConfirmDTO {
            return TradeConfirmDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeConfirmDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class TradeRequestDTO (
    @SerializedName("orderId") var orderId: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("tradeType") var tradeType: String?,
    @SerializedName("tradeSide") var tradeSide: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("quantity") var quantity: String?,
    @SerializedName("orderDate") var orderDate: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(productCode)
        parcel.writeString(tradeType)
        parcel.writeString(tradeSide)
        parcel.writeString(price)
        parcel.writeString(quantity)
        parcel.writeString(orderDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeRequestDTO> {
        override fun createFromParcel(parcel: Parcel): TradeRequestDTO {
            return TradeRequestDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeRequestDTO?> {
            return arrayOfNulls(size)
        }
    }

}

package com.sctgold.goldinvest.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TradeHistoryDTO(
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("mobileTradeHistoryList") var mobileTradeHistoryList: List<TradeHistoryDetailDTO>
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(TradeHistoryDetailDTO)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(response)
        parcel.writeString(message)
        parcel.writeString(timeStamp)
        parcel.writeTypedList(mobileTradeHistoryList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeHistoryDTO> {
        override fun createFromParcel(parcel: Parcel): TradeHistoryDTO {
            return TradeHistoryDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeHistoryDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class TradeHistoryDetailDTO(
    var type: Int,
    @SerializedName("orderId") var orderId: String?,
    @SerializedName("orderDate") var orderDate: String?,
    @SerializedName("tradeType") var tradeType: String?,
    @SerializedName("ivNumber") var ivNumber: String?,
    @SerializedName("orderNumber") var orderNumber: String?,
    @SerializedName("tradeSide") var tradeSide: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
    @SerializedName("quantity") var quantity: String?,
    @SerializedName("productUnit") var productUnit: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("total") var total: String?,
    @SerializedName("dueDate") var dueDate: String?,
    @SerializedName("approveStatus") var approveStatus: String?,
    @SerializedName("matchStatus") var matchStatus: String?,
    @SerializedName("paymentStatus") var paymentStatus: String?,
    @SerializedName("groupOrderStatus") var groupOrderStatus: String?,
    @SerializedName("deleteEnable") var deleteEnable: Boolean,
    @SerializedName("fifoStatus") var fifoStatus: String?,
    @SerializedName("priceUnit") var priceUnit: String?,
    @SerializedName("currencyCode") var currencyCode: String?,


): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(orderId)
        parcel.writeString(orderDate)
        parcel.writeString(tradeType)
        parcel.writeString(ivNumber)
        parcel.writeString(orderNumber)
        parcel.writeString(tradeSide)
        parcel.writeString(productCode)
        parcel.writeString(productName)
        parcel.writeString(quantity)
        parcel.writeString(productUnit)
        parcel.writeString(price)
        parcel.writeString(total)
        parcel.writeString(dueDate)
        parcel.writeString(approveStatus)
        parcel.writeString(matchStatus)
        parcel.writeString(paymentStatus)
        parcel.writeString(groupOrderStatus)
        parcel.writeByte(if (deleteEnable) 1 else 0)
        parcel.writeString(fifoStatus)
        parcel.writeString(priceUnit)
        parcel.writeString(currencyCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeHistoryDetailDTO> {
        override fun createFromParcel(parcel: Parcel): TradeHistoryDetailDTO {
            return TradeHistoryDetailDTO(parcel)
        }

        override fun newArray(size: Int): Array<TradeHistoryDetailDTO?> {
            return arrayOfNulls(size)
        }
    }

}

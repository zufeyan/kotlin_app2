package com.sctgold.goldinvest.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PriceAlertsDTO (
    @SerializedName("response") val response: String,
    @SerializedName("message") val message: String,
    @SerializedName("timeStamp") val timeStamp: String,
    @SerializedName("mobilePriceAlertList") var mobilePriceAlertList:ArrayList<PriceAlertDetailDTO>,
)


data class PriceAlertDetailDTO(
    @SerializedName("id") val id: String?,
    @SerializedName("type") var type: String?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("currencySymbol") val currencySymbol: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("mobileEnable") var mobileEnable: Boolean,
    @SerializedName("smsEnable") var smsEnable: Boolean,
    @SerializedName("emailEnable") var emailEnable: Boolean,
    @SerializedName("alerted") var alerted: Boolean,
    @SerializedName("createDate") val createDate: String?,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    constructor() : this(
        "", "", "", "", "", "", "", true, false, false, false, ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(currency)
        parcel.writeString(currencySymbol)
        parcel.writeString(productCode)
        parcel.writeString(productName)
        parcel.writeString(price)
        parcel.writeByte(if (mobileEnable) 1 else 0)
        parcel.writeByte(if (smsEnable) 1 else 0)
        parcel.writeByte(if (emailEnable) 1 else 0)
        parcel.writeByte(if (alerted) 1 else 0)
        parcel.writeString(createDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PriceAlertDetailDTO> {
        override fun createFromParcel(parcel: Parcel): PriceAlertDetailDTO {
            return PriceAlertDetailDTO(parcel)
        }

        override fun newArray(size: Int): Array<PriceAlertDetailDTO?> {
            return arrayOfNulls(size)
        }
    }
}

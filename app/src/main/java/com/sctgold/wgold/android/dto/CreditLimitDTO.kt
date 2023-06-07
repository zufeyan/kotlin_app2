package com.sctgold.wgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class CreditLimitDTO(
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("mobileCreditLimit") var mobileCreditLimit: CreditLimitDetailDTO?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(CreditLimitDetailDTO::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(response)
        parcel.writeString(message)
        parcel.writeString(timeStamp)
        parcel.writeParcelable(mobileCreditLimit, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreditLimitDTO> {
        override fun createFromParcel(parcel: Parcel): CreditLimitDTO {
            return CreditLimitDTO(parcel)
        }

        override fun newArray(size: Int): Array<CreditLimitDTO?> {
            return arrayOfNulls(size)
        }
    }

}


data class CreditLimitDetailDTO (
    @SerializedName("orgPointBuy") val orgPointBuy: String?,
    @SerializedName("pointBuy") val pointBuy: String?,
    @SerializedName("orgPointSell") val orgPointSell: String?,
    @SerializedName("pointSell") val pointSell: String?,
    @SerializedName("percentBuy") val percentBuy: Float,
    @SerializedName("percentSell") val percentSell: Float,
    @SerializedName("canBuy") val canBuy: Boolean,
    @SerializedName("canSell") val canSell: Boolean,
    @SerializedName("weight") val weight: String?,
    @SerializedName("weightBuy") val weightBuy: String?,
    @SerializedName("weightSell") val weightSell: String?,
    @SerializedName("weightUnit") val weightUnit: String?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("enableDecimal") val enableDecimal: Boolean,
    @SerializedName("decimalPlace") val decimalPlace: Int,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orgPointBuy)
        parcel.writeString(pointBuy)
        parcel.writeString(orgPointSell)
        parcel.writeString(pointSell)
        parcel.writeFloat(percentBuy)
        parcel.writeFloat(percentSell)
        parcel.writeByte(if (canBuy) 1 else 0)
        parcel.writeByte(if (canSell) 1 else 0)
        parcel.writeString(weight)
        parcel.writeString(weightBuy)
        parcel.writeString(weightSell)
        parcel.writeString(weightUnit)
        parcel.writeString(currency)
        parcel.writeByte(if (enableDecimal) 1 else 0)
        parcel.writeInt(decimalPlace)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreditLimitDetailDTO> {
        override fun createFromParcel(parcel: Parcel): CreditLimitDetailDTO {
            return CreditLimitDetailDTO(parcel)
        }

        override fun newArray(size: Int): Array<CreditLimitDetailDTO?> {
            return arrayOfNulls(size)
        }
    }
}

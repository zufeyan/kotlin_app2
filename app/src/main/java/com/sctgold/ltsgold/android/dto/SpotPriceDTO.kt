package com.sctgold.ltsgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SpotPriceDTO(
    @SerializedName("customerGrade") var customerGrade: String?,
    @SerializedName("productPriceDTOs") var productPriceDTOs: ArrayList<ProductPriceDTO>,
    @SerializedName("priceDate_th") var priceDate_th: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArrayList(null) as ArrayList<ProductPriceDTO>,
        parcel.readString() )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(customerGrade)
        parcel.writeString(priceDate_th)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpotPriceDTO> {
        override fun createFromParcel(parcel: Parcel): SpotPriceDTO {
            return SpotPriceDTO(parcel)
        }

        override fun newArray(size: Int): Array<SpotPriceDTO?> {
            return arrayOfNulls(size)
        }
    }
}

data class SystemDTO(
    @SerializedName("frontTradeEnable") var frontTradeEnable: Boolean,
    @SerializedName("normalTradeEnable") var normalTradeEnable: Boolean,
    @SerializedName("placeOrderTradeEnable") var placeOrderTradeEnable: Boolean,
    @SerializedName("tradeFreezing") var tradeFreezing: Boolean,
    @SerializedName("timeStamp") var timeStamp: String?,
//    @SerializedName("mobileAccounts") var mobileAccounts: Arrays,
    @SerializedName("orderNotificationSoundEnable") var orderNotificationSoundEnable: Boolean,
    @SerializedName("customerLogin") var customerLogin: String?,
    @SerializedName("systemClose") var systemClose: Boolean,
    @SerializedName("frontClose") var frontClose: Boolean,
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (frontTradeEnable) 1 else 0)
        parcel.writeByte(if (normalTradeEnable) 1 else 0)
        parcel.writeByte(if (placeOrderTradeEnable) 1 else 0)
        parcel.writeByte(if (tradeFreezing) 1 else 0)
        parcel.writeString(timeStamp)
        parcel.writeByte(if (orderNotificationSoundEnable) 1 else 0)
        parcel.writeString(customerLogin)
        parcel.writeByte(if (systemClose) 1 else 0)
        parcel.writeByte(if (frontClose) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SystemDTO> {
        override fun createFromParcel(parcel: Parcel): SystemDTO {
            return SystemDTO(parcel)
        }

        override fun newArray(size: Int): Array<SystemDTO?> {
            return arrayOfNulls(size)
        }
    }
}

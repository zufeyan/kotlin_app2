package com.sctgold.wgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DetailRealtimeSpotDTO(
    @SerializedName("index") var seqNo: Int = 0,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("fullProductName") var fullProductName: String? ,
    @SerializedName("viewable") var viewable: Boolean = false,
    @SerializedName("freeze") var freeze: Boolean = false,
    @SerializedName("sellPriceOld") var sellPriceOld: String? ,
    @SerializedName("buyPriceOld") var buyPriceOld: String? ,
    @SerializedName("sellPriceNew") var sellPriceNew: String? ,
    @SerializedName("buyPriceNew") var buyPriceNew: String?,
    @SerializedName("changeSell") var changeSell: Boolean = false,
    @SerializedName("changeBuy") var changeBuy: Boolean = false,
    @SerializedName("sellUp") var sellUp: Boolean = false,
    @SerializedName("buyUp") var buyUp: Boolean = false,
    @SerializedName("checkStatus") var checkStatus: Boolean = false,
    @SerializedName("currentStatus") var currentStatus: String?,
    @SerializedName("colorBuy") var colorBuy: String?,
    @SerializedName("colorSell") var colorSell: String? ,
    @SerializedName("currency") var currency: String?,

    )  : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor() : this(
        0, "", "", false, false, "", "", "",
        "",
        false, false, false, false, false, "", "", "", ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(seqNo)
        parcel.writeString(productCode)
        parcel.writeString(fullProductName)
        parcel.writeByte(if (viewable) 1 else 0)
        parcel.writeByte(if (freeze) 1 else 0)
        parcel.writeString(sellPriceOld)
        parcel.writeString(buyPriceOld)
        parcel.writeString(sellPriceNew)
        parcel.writeString(buyPriceNew)
        parcel.writeByte(if (changeSell) 1 else 0)
        parcel.writeByte(if (changeBuy) 1 else 0)
        parcel.writeByte(if (sellUp) 1 else 0)
        parcel.writeByte(if (buyUp) 1 else 0)
        parcel.writeByte(if (checkStatus) 1 else 0)
        parcel.writeString(currentStatus)
        parcel.writeString(colorBuy)
        parcel.writeString(colorSell)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DetailRealtimeSpotDTO> {
        override fun createFromParcel(parcel: Parcel): DetailRealtimeSpotDTO {
            return DetailRealtimeSpotDTO(parcel)
        }

        override fun newArray(size: Int): Array<DetailRealtimeSpotDTO?> {
            return arrayOfNulls(size)
        }
    }


}

data class BackupPriceProductDTO(
    var status: String?,
    var productCode: String?,
    var statusFreeze: Boolean,
    var sellPriceNew: String?,
    var buyPriceNew: String?,
    var currency: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor() : this(
        "", "", false, "", "", ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(productCode)
        parcel.writeByte(if (statusFreeze) 1 else 0)
        parcel.writeString(sellPriceNew)
        parcel.writeString(buyPriceNew)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BackupPriceProductDTO> {
        override fun createFromParcel(parcel: Parcel): BackupPriceProductDTO {
            return BackupPriceProductDTO(parcel)
        }

        override fun newArray(size: Int): Array<BackupPriceProductDTO?> {
            return arrayOfNulls(size)
        }
    }
}

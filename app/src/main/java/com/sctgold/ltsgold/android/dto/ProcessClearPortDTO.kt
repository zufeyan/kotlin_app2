package com.sctgold.ltsgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class ProcessClearPortDTO(
    @SerializedName("buySelect") var buySelect: ArrayList<MatchingSelectListDTO>,
    @SerializedName("payType") var payType: String?,
    @SerializedName("sellSelect") var sellSelect: ArrayList<MatchingSelectListDTO>,
): Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("buySelect"),
        parcel.readString(),
        TODO("sellSelect")
    ) {
    }

    constructor() : this(
        ArrayList(), "",     ArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(payType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProcessClearPortDTO> {
        override fun createFromParcel(parcel: Parcel): ProcessClearPortDTO {
            return ProcessClearPortDTO(parcel)
        }

        override fun newArray(size: Int): Array<ProcessClearPortDTO?> {
            return arrayOfNulls(size)
        }
    }
}

data class OrderMatchDTO(
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("mobileMatchingSelectBuyList") var mobileMatchingSelectBuyList: ArrayList<MatchingSelectListDTO>,
    @SerializedName("mobileMatchingSelectSellList") var mobileMatchingSelectSellList: ArrayList<MatchingSelectListDTO>,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("mobileMatchingSelectBuyList"),
        TODO("mobileMatchingSelectSellList")
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

    companion object CREATOR : Parcelable.Creator<OrderMatchDTO> {
        override fun createFromParcel(parcel: Parcel): OrderMatchDTO {
            return OrderMatchDTO(parcel)
        }

        override fun newArray(size: Int): Array<OrderMatchDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class MatchingSelectListDTO(
    @SerializedName("orderId") var orderId: Int,
    @SerializedName("code") var code: String?,
    @SerializedName("dueDate") var dueDate: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
    @SerializedName("qty") var qty: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("total") var total: String?,
    @SerializedName("overdue") var overdue: String?,
    @SerializedName("side") var side: String?,
    @SerializedName("qtyUnit") var qtyUnit: String?,
) : Parcelable {
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
        parcel.readString()
    ) {
    }

    constructor() : this(
        0, "", "", "", "", "", "", "",
        "",
        "", ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(orderId)
        parcel.writeString(code)
        parcel.writeString(dueDate)
        parcel.writeString(productCode)
        parcel.writeString(productName)
        parcel.writeString(qty)
        parcel.writeString(price)
        parcel.writeString(total)
        parcel.writeString(overdue)
        parcel.writeString(side)
        parcel.writeString(qtyUnit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MatchingSelectListDTO> {
        override fun createFromParcel(parcel: Parcel): MatchingSelectListDTO {
            return MatchingSelectListDTO(parcel)
        }

        override fun newArray(size: Int): Array<MatchingSelectListDTO?> {
            return arrayOfNulls(size)
        }
    }
}

//data class MatchingSelectSellListDTO (
//    @SerializedName("orderId") val orderId: Int,
//    @SerializedName("code") val code: String,
//    @SerializedName("dueDate") val dueDate: String,
//    @SerializedName("productCode") val productCode: String,
//    @SerializedName("productName") val productName: String,
//    @SerializedName("qty") val qty: String,
//    @SerializedName("price") val price: String,
//    @SerializedName("total") val total: String,
//    @SerializedName("overdue") val overdue: String,
//    @SerializedName("side") val side: String,
//    @SerializedName("qtyUnit") val qtyUnit: String,
//) {
//    constructor() : this(
//        0, "", "", "", "", "", "", "",
//        "",
//        "", ""
//    )
//}
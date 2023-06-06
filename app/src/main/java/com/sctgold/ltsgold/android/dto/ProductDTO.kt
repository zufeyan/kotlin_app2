package com.sctgold.ltsgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class ProductDTO(
    @SerializedName("id") var id: Int,
    @SerializedName("code") var code: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("nameEn") var nameEn: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeString(nameEn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductDTO> {
        override fun createFromParcel(parcel: Parcel): ProductDTO {
            return ProductDTO(parcel)
        }

        override fun newArray(size: Int): Array<ProductDTO?> {
            return arrayOfNulls(size)
        }
    }

}


data class ProductPriceDTO(
    @SerializedName("sellPrice") var sellPrice: String?,
    @SerializedName("buyPrice") var buyPrice: String?,
    @SerializedName("productDTO") var productDTO: ProductDTO,
    @SerializedName("freeze") var freeze: Boolean,
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        TODO("productDTO"),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sellPrice)
        parcel.writeString(buyPrice)
        parcel.writeByte(if (freeze) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductPriceDTO> {
        override fun createFromParcel(parcel: Parcel): ProductPriceDTO {
            return ProductPriceDTO(parcel)
        }

        override fun newArray(size: Int): Array<ProductPriceDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class ProductResponse (
    @SerializedName("response") var response: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("mobileProductList") var mobileProductList: ArrayList<MyProductDTO>,
    @SerializedName("mobileMyProductList") var mobileMyProductList: ArrayList<MyProductDTO>,
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("mobileProductList"),
        TODO("mobileMyProductList")
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

    companion object CREATOR : Parcelable.Creator<ProductResponse> {
        override fun createFromParcel(parcel: Parcel): ProductResponse {
            return ProductResponse(parcel)
        }

        override fun newArray(size: Int): Array<ProductResponse?> {
            return arrayOfNulls(size)
        }
    }

}

data class MyProductDTO (
    @SerializedName("name") var name: String?,
    @SerializedName("code") var code: String?,
    @SerializedName("viewable") var viewable: Boolean,
    @SerializedName("currency") var currency: String?,
    @SerializedName("productColor") var productColor: String?,
    @SerializedName("currentStatus") var currentStatus: String?,
) : Parcelable{
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
        "", "", true, "", "", ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeByte(if (viewable) 1 else 0)
        parcel.writeString(currency)
        parcel.writeString(productColor)
        parcel.writeString(currentStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyProductDTO> {
        override fun createFromParcel(parcel: Parcel): MyProductDTO {
            return MyProductDTO(parcel)
        }

        override fun newArray(size: Int): Array<MyProductDTO?> {
            return arrayOfNulls(size)
        }
    }
}
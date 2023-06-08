package com.sctgold.goldinvest.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ResponseDTO (
    @SerializedName("response") var response: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
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

    companion object CREATOR : Parcelable.Creator<ResponseDTO> {
        override fun createFromParcel(parcel: Parcel): ResponseDTO {
            return ResponseDTO(parcel)
        }

        override fun newArray(size: Int): Array<ResponseDTO?> {
            return arrayOfNulls(size)
        }
    }

}


data class ResponseTokenDTO (
    @SerializedName("response") val response: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timeStamp") val timeStamp: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("grade") val grade: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
        parcel.writeString(token)
        parcel.writeString(grade)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseTokenDTO> {
        override fun createFromParcel(parcel: Parcel): ResponseTokenDTO {
            return ResponseTokenDTO(parcel)
        }

        override fun newArray(size: Int): Array<ResponseTokenDTO?> {
            return arrayOfNulls(size)
        }
    }

}

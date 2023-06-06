package com.sctgold.ltsgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

 data class EventDTO(
    @SerializedName("event") var event: String?,
    @SerializedName("accountNumber") var accountNumber: String?,
    @SerializedName("timeStamp") var timeStamp: String?
 ): Parcelable {
     constructor(parcel: Parcel) : this(
         parcel.readString(),
         parcel.readString(),
         parcel.readString()
     )

     override fun writeToParcel(parcel: Parcel, flags: Int) {
         parcel.writeString(event)
         parcel.writeString(accountNumber)
         parcel.writeString(timeStamp)
     }

     override fun describeContents(): Int {
         return 0
     }

     companion object CREATOR : Parcelable.Creator<EventDTO> {
         override fun createFromParcel(parcel: Parcel): EventDTO {
             return EventDTO(parcel)
         }

         override fun newArray(size: Int): Array<EventDTO?> {
             return arrayOfNulls(size)
         }
     }

 }
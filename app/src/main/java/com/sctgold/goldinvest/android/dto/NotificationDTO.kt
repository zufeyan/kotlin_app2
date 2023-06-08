package com.sctgold.goldinvest.android.dto

import com.google.gson.annotations.SerializedName

data class NotificationDTO (
    @SerializedName("response") val response: String,
    @SerializedName("message") val message: String,
    @SerializedName("timeStamp") val timeStamp: String,
    @SerializedName("mobileNotificationList") var mobileNotificationList: ArrayList<NotificationDetailDTO>,
    @SerializedName("unNotificationRead") var unNotificationRead: Int,
        )

data class NotificationDetailDTO (
    @SerializedName("notificationId") var notificationId: Int,
    @SerializedName("notificationTitle") var notificationTitle: String,
    @SerializedName("notificationMessage") var notificationMessage: String,
    @SerializedName("notificationTime") var notificationTime: String,
    @SerializedName("notificationRead") var notificationRead: Boolean,

    )

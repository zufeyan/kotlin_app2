package com.sctgold.wgold.android.dto

import com.google.gson.annotations.SerializedName

data class VersionResponse (
    @SerializedName("mobileVersion") val mobileVersion: MobileVersion,
)

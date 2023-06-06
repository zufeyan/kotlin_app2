package com.sctgold.ltsgold.android.dto

import com.google.gson.annotations.SerializedName

data class NewsListDTO (
    @SerializedName("response") var response: String,
    @SerializedName("newsDto") var newsDTO: List<NewsDTO>,
        )

data class NewsDTO (
    @SerializedName("id") var id: Int,
    @SerializedName("headline") var headline: Int,
    @SerializedName("title") var title: String,
    @SerializedName("postdate") var postdate: String,
    @SerializedName("process") var process: String,
    @SerializedName("response") var response: String,
    @SerializedName("description") var description: String,
    @SerializedName("picture") var picture: String,

    )

data class NewsAllDeatilDto(
    @SerializedName("id") var id: Int,
    @SerializedName("headline") var headline: Int,
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("picture") var picture: String,
    @SerializedName("picture") var picture2: String,
    @SerializedName("picture") var picture3: String,
    @SerializedName("postdate") var postdate: String,
    @SerializedName("linkvdo") var linkvdo: String,
    @SerializedName("process") var process: String,
    @SerializedName("response") var response: String,
)
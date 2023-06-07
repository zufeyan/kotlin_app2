package com.sctgold.wgold.android.rest

import com.sctgold.wgold.android.dto.NewsListDTO
import com.sctgold.wgold.android.util.PropertiesKotlin
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface NewsService {

    @Headers("Content-Type: application/json")
    @GET("news/getNewsDailyList/th")
    fun getNewsDaily(): Call<NewsListDTO>

    @GET("news/getNewsDailyById/th/{id}")
    fun getNewsDailyById(@Path("id") id: String): Call<ResponseBody>

    @GET("news/getDailyAnalysisList/th")
    fun getDailyAnalysisList(): Call<NewsListDTO>


    companion object {
        val client: OkHttpClient = OkHttpClient.Builder().callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
        operator fun invoke(): NewsService {
            return Retrofit.Builder()
                .baseUrl(PropertiesKotlin.sctUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(NewsService::class.java)

        }
    }


}

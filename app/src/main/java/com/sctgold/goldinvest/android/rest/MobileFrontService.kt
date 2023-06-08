package com.sctgold.goldinvest.android.rest

import android.util.Log
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.sctgold.goldinvest.android.dto.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface MobileFrontService {

    @POST("start")
    fun startApp(@Body startInfoDto: RequestBody): Call<ResponseBody>

//    @POST("secure/authentication")
//    fun authentication(
//        @Header("token") token: String,
//        @Body startInfoDto: RequestBody
//    ) : Call<ResponseTokenDto>

    @POST("secure/authentication")
    fun authentication(
        @Header("token") token: String,
        @Body startInfoDto: RequestBody
    ): Call<ResponseBody>

    @PUT("canlogin")
    fun canlogin(@Body startInfoDto: Map<String, String>): Call<ResponseBody>

    @POST("secure/pin")
    fun checkPin(
        @Header("token") token: String
    ): Call<ResponseBody>

    @PUT("secure/changePasswordAndPin")
    fun changePasswordAndPin(
        @Header("token") token: String
    ): Call<ResponseDTO>

    @PUT("secure/changePassword")
    fun changePassword(
        @Header("token") token: String
    ): Call<ResponseDTO>

    @PUT("secure/changePin")
    fun changePin(
        @Header("token") token: String
    ): Call<ResponseDTO>

    @POST("secure/logout")
    fun logout(
        @Header("token") token: String
    ): Call<ResponseDTO>

    @GET("products")
    fun products(
        @Header("token") token: String
    ): Call<ProductResponse>

    @GET("products/{productCode}")
    fun products(
        @Header("token") token: String,
        @Path("productCode") productCode: String
    ): Call<CreditLimitDTO>

    @POST("trade")
    fun trade(
        @Header("token") token: String,
        @Body tradeRequestDTO: Map<String, String>
    ): Call<ResponseBody>

    //    @Body HashMap<String, Object> body
    @PUT("trade/{productCode}/{side}")
    fun tradeConfirm(
        @Header("token") token: String,
        @Path("productCode") productCode: String,
        @Path("side") side: String
    ): Call<TradeConfirmDTO>

    @GET("history")
    fun history(
        @Header("token") token: String
    ): Call<TradeHistoryDTO>

    @GET("history/{firstResult}/{maxResults}")
    fun history(
        @Header("token") token: String,
        @Path("firstResult") firstResult: String,
        @Path("maxResults") maxResults: String,
    ): Call<TradeHistoryDTO>

    @DELETE("order/{id}")
    fun deleteHistory(
        @Header("token") token: String,
        @Path("id") id: String
    ): Call<ResponseDTO>

    @GET("notifications")
    fun getNotifications(
        @Header("token") token: String
    ): Call<NotificationDTO>

    @GET("notifications/{firstResult}/{maxResults}")
    fun getNoticeMessageList(
        @Header("token") token: String,
        @Path("firstResult") firstResult: String,
        @Path("maxResults") maxResults: String,
    ): Call<NotificationDTO>

    @POST("notification/read/{notificationId}")
    fun noticeRead(
        @Header("token") token: String,
        @Path("notificationId") id: String
    ): Call<ResponseDTO>

    @POST("notification/readall")
    fun noticeReadall(
        @Header("token") token: String
    ): Call<NotificationDTO>

    @GET("portfolio/new")
    fun getPortfolio(
        @Header("token") token: String
    ): Call<PortfolioProductDTO>

    @POST("products/update")
    fun update(
        @Header("token") token: String,
        @Body productsCodeRequest: List<String>
    ): Call<ResponseDTO>


    @GET("priceAlerts")
    fun getPriceAlertsList(
        @Header("token") token: String
    ): Call<PriceAlertsDTO>


    @POST("priceAlert/addNew")
    fun addNewPriceAlert(
        @Header("token") token: String,
        @Body priceAlerts: RequestBody
    ): Call<PriceAlertsDTO>

    @DELETE("priceAlert/delete/{id}")
    fun deletePriceAlert(
        @Header("token") token: String,
        @Path("id") id: String,
    ): Call<ResponseDTO>


    @GET("orderForMatch/{productCode}")
    fun orderForMatch(
        @Header("token") token: String,
        @Path("productCode") productCode: String
    ): Call<OrderMatchDTO>

    @POST("processClearPort")
    fun processClearPort(
        @Header("token") token: String,
        @Body orderMatch: ProcessClearPortDTO
    ): Call<ResponseDTO>

    @GET("version/latest")
    fun getLatestVersion(): Call<VersionResponse>

    @POST("account")
    fun newAccount(@Body customer: Map<String, String>): Call<ResponseDTO>

    @GET("forgotPassword/{username}/{email}")
    fun forgotPassword(
        @Path("username") username: String, @Path("email") email: String
    ): Call<ResponseDTO>

    companion object {
        val client: OkHttpClient = OkHttpClient.Builder().build()
        operator fun invoke(): MobileFrontService {
            Log.e("PropertiesKotlin.serverUrl :", PropertiesKotlin.serverUrl.toString())
            return Retrofit.Builder()
                .baseUrl(PropertiesKotlin.serverUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MobileFrontService::class.java)

        }
    }

}

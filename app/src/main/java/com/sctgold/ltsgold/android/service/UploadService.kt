package com.sctgold.ltsgold.android.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UploadService {


//    @Multipart
//    @POST("mobile/upload")
//    fun uploadImage(
//        @Header("token") token: String,
//        @Part file: RequestBody
//    ): Call<ResponseBody>

    @Multipart
    @POST("upload")
    fun uploadImage( @Part image: MultipartBody.Part?):  Call<ResponseBody>



}
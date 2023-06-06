package com.sctgold.ltsgold.android.rest

import android.content.Context
import android.content.Intent
import android.util.Log
import com.sctgold.ltsgold.android.activities.LauncherActivity
import com.sctgold.ltsgold.android.dto.*
import com.sctgold.ltsgold.android.fragments.ByteArrayToBase64Adapter
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.sctgold.ltsgold.android.dto.*
import com.google.gson.GsonBuilder
import com.sctgold.android.tradeplus.util.ManageData
import com.sctgold.android.tradeplus.util.RSAKotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object ServiceApiSpot {

    fun forceChangePasswordAndPin(
        newPwd: String, newPin: String, context: Context,
        onResult: (ResponseDTO) -> Unit
    ) {
        var pwdNew: String? = ManageData().getMd5Base64(newPwd)
        var pinNew: String? = ManageData().getMd5Base64(newPin)
        var sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace(
                "BRC",
                PropertiesKotlin.branch
            ) + PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) + PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            ) + pwdNew + pwdNew?.let {
                ManageData().addSpace(
                    "NPW",
                    it
                )
            } + pinNew + pinNew?.let { ManageData().addSpace("NPN", it) }

        var encryptText = context.let { RSAKotlin().decryptRsa(sumText, it) }

        if (encryptText != null) {
            MobileFrontService.invoke().changePasswordAndPin(encryptText).enqueue(object :
                retrofit2.Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>
                ) {

                    var data = response.body()
                    when (data?.response) {
                        "SUCCESS" -> {
                            onResult(data)
                        }
                        "INVALID_CREDENTIAL" -> {
                            ManageData().checkSessionAuthority(
                                data.response.toString(),
                                context
                            )
                        }
                        else -> {
                            if (data != null) {
                                onResult(data)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                }
            })
        }
    }

    fun changeNewPassword(
        currentPass: String, newPass: String, context: Context,
        onResult: (ResponseDTO) -> Unit
    ) {
        var currentPwd: String? = ManageData().getMd5Base64(currentPass)
        var newPwd: String? = ManageData().getMd5Base64(newPass)
        var sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace(
                "BRC",
                PropertiesKotlin.branch
            ) + PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) + PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            ) + currentPwd + currentPwd?.let {
                ManageData().addSpace(
                    "PWD",
                    it
                )
            } + newPwd + newPwd?.let { ManageData().addSpace("NPW", it) }

        var encryptText = context.let { RSAKotlin().decryptRsa(sumText, it) }

        if (encryptText != null) {
            MobileFrontService.invoke().changePassword(encryptText).enqueue(object :
                retrofit2.Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>
                ) {
                    var data = response.body()
                    when (data?.response) {
                        "SUCCESS" -> {
                            onResult(data)
                        }
                        "INVALID_CREDENTIAL" -> {
                            ManageData().checkSessionAuthority(
                                data.response.toString(),
                                context
                            )
                        }
                        else -> {
                            if (data != null) {
                                onResult(data)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                }
            })
        }
    }

    fun changeNewPin(
        currentPin: String, newPin: String, context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {
        var pinCurrent: String? = ManageData().getMd5Base64(currentPin)
        var pinNew: String? = ManageData().getMd5Base64(newPin)

        var sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace(
                "BRC",
                PropertiesKotlin.branch
            ) + PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) + PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            ) + pinCurrent + pinCurrent?.let {
                ManageData().addSpace(
                    "PIN",
                    it
                )
            } + pinNew + pinNew?.let { ManageData().addSpace("NPN", it) }

        var encryptText = context.let { RSAKotlin().decryptRsa(sumText, it) }

        if (encryptText != null) {
            MobileFrontService.invoke().changePin(encryptText).enqueue(object :
                retrofit2.Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>
                ) {
                    var data = response.body()
                    when (data?.response) {
                        "SUCCESS" -> {
                            onResult(data)
                        }
                        "INVALID_CREDENTIAL" -> {
                            ManageData().checkSessionAuthority(
                                data.response.toString(),
                                context
                            )
                        }
                        else -> {
                            onResult(data)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                }
            })
        }
    }

    fun logout(context: Context) {
        var text = ManageData().getDataCoding(context)
        if (text != null) {
            CoroutineScope(Dispatchers.IO).launch {
                MobileFrontService.invoke().logout(text).enqueue(object :
                    retrofit2.Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        if (response.code() == 200) {
                            //   var data = response.body()
                            PropertiesKotlin.stopAllBackGroundService(context)
                            PropertiesKotlin.token = ""
                            val intent = Intent(context, LauncherActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)

                        }

                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("ERROR", t.toString())
                    }
                })
            }
        }
    }


    fun getCreditLimitByProductCode(
        code: String,
        context: Context,
        onResult: (CreditLimitDTO?) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)
        if (text != null) {
            MobileFrontService.invoke().products(text, code).enqueue(object :
                retrofit2.Callback<CreditLimitDTO> {
                override fun onResponse(
                    call: Call<CreditLimitDTO>,
                    response: Response<CreditLimitDTO>
                ) {

                    if (response.body() == null) {

                    } else {
                        var data = response.body()

                        when (data?.response) {
                            "SUCCESS" -> {
                                if (data.mobileCreditLimit != null) {
                                    onResult(data)
                                } else {
                                    onResult(null)
                                }
                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                onResult(data)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CreditLimitDTO>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })

        }

    }


    fun getMyProduct(context: Context, onResult: (ProductResponse?) -> Unit) {

        var text = ManageData().getDataCoding(context)
        MobileFrontService.invoke().products(text).enqueue(object :
            retrofit2.Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.code() == 200) {
                    //   Log.e("getMyProduct  response.body() ", response.body().toString())
                    var data = response.body()
                    if (data != null) {
                        when (data.response) {
                            "SUCCESS" -> {
                                onResult(data)


                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                onResult(data)
                            }

                        }
                    }
                }

            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("ERROR", t.toString())
                onResult(null)
            }
        })
    }


    fun newTrade(
        pdCode: String,
        tradeType: String,
        tradeSide: String,
        price: String,
        qtyUse: String,
        context: Context,
        onResult: (TradeDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        if (text != null) {
//            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
//            var orderDate = sdf.format(Date())

            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val orderDate = current.format(formatter)

            var param: Map<String, String> = mapOf(
                "orderId" to "0",
                "productCode" to pdCode,
                "tradeType" to tradeType,
                "tradeSide" to tradeSide,
                "price" to price,
                "quantity" to qtyUse,
                "orderDate" to orderDate.toString(),
            )
            MobileFrontService.invoke().trade(text, param).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {

                        val jsonObject = JSONObject(response.body()!!.string())
                        val gson = GsonBuilder().registerTypeHierarchyAdapter(
                            ByteArray::class.java,
                            ByteArrayToBase64Adapter()
                        ).create()
                        var data =
                            gson.fromJson(jsonObject.toString(), TradeDTO::class.java)

                        when (data?.response) {
                            "SUCCESS" -> {
                                onResult(data)
                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                onResult(data)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })
        }
    }

    fun confirmTrade(
        orderId: String, productCode: String, side: String,
        context: Context,
        onResult: (TradeConfirmDTO) -> Unit
    ) {
        val sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace("BRC", PropertiesKotlin.branch) +
                    PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) +
                    PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            ) +
                    orderId + ManageData().addSpace(
                "OID",
                orderId
            )

        val encryptText = context.let { RSAKotlin().decryptRsa(sumText, it) }
        if (encryptText != null) {
            CoroutineScope(Dispatchers.IO).launch {
                encryptText.let { MobileFrontService().tradeConfirm(it, productCode, side) }
                    .enqueue(object : retrofit2.Callback<TradeConfirmDTO> {
                        override fun onResponse(
                            call: Call<TradeConfirmDTO>,
                            response: Response<TradeConfirmDTO>
                        ) {
                            if (response.code() == 200) {
                                var data = response.body()

                                if (data != null) {
                                    when (data.response) {
                                        "SUCCESS" -> {
                                            onResult(data)
                                        }
                                        "INVALID_CREDENTIAL" -> {
                                            ManageData().checkSessionAuthority(
                                                data.response.toString(),
                                                context
                                            )
                                        }
                                        else -> {
                                            onResult(data)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<TradeConfirmDTO>, t: Throwable) {
                            Log.e("ERROR", t.message.toString())

                        }

                    })
            }
        }
    }


    fun getTradeHistory(
        firstResult: String,
        maxResult: String, context: Context,
        onResult: (TradeHistoryDTO?) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)
        if (text != null) {

            MobileFrontService.invoke().history(text, firstResult, maxResult).enqueue(object :
                retrofit2.Callback<TradeHistoryDTO> {
                override fun onResponse(
                    call: Call<TradeHistoryDTO>,
                    response: Response<TradeHistoryDTO>
                ) {
                    if (response.code() == 200) {
                        var data = response.body()

                        when (data?.response) {
                            "SUCCESS" -> {
                                onResult(data)


                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                onResult(data)
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<TradeHistoryDTO>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })
        }
    }

    fun deleteHistory(
        orderId: String,
        context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)
        if (text != null) {

            MobileFrontService.invoke().deleteHistory(text, orderId).enqueue(object :
                retrofit2.Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<
                            ResponseDTO>
                ) {
                    if (response.code() == 200) {
                        var data = response.body()

                        when (data?.response) {
                            "SUCCESS" -> {
                                onResult(data)
                            }
                            "RECORD_NOT_FOUND" -> {
                                onResult(data)
                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                onResult(data)
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })
        }
    }

    fun getNews(
        context: Context,
        onResult: (NewsListDTO) -> Unit
    ) {
        NewsService.invoke().getNewsDaily().enqueue(object :
            retrofit2.Callback<NewsListDTO> {
            override fun onResponse(call: Call<NewsListDTO>, response: Response<NewsListDTO>) {
                Log.e("onResponse getNews :", response.toString())
            }

            override fun onFailure(call: Call<NewsListDTO>, t: Throwable) {
                Log.e("onFailure getNews :", t.message.toString())
            }

        })
    }

    fun getPortfolio(
        context: Context,
        onResult: (PortfolioProductDTO) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)
        if (text != null) {
            MobileFrontService.invoke().getPortfolio(text).enqueue(object :
                retrofit2.Callback<PortfolioProductDTO> {
                override fun onResponse(
                    call: Call<PortfolioProductDTO>,
                    response: Response<PortfolioProductDTO>
                ) {
                    if (response.code() == 200) {
                        var data = response.body()

                        when (data?.response) {
                            "SUCCESS" -> {
                                onResult(data)
                            }
                            "FAILED" -> {
                                onResult(data)
                            }
                            "INVALID_CREDENTIAL" -> {
                                ManageData().checkSessionAuthority(
                                    data.response.toString(),
                                    context
                                )
                            }
                            else -> {
                                if (data != null) {
                                    onResult(data)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PortfolioProductDTO>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })
        }

    }

//    fun getPriceAlertsList(
//        context: Context,
//        onResult: (PriceAlertsDTO?) -> Unit
//    ) {
//
//        var text = ManageData().getDataCoding(context)
//        CoroutineScope(Dispatchers.IO).launch {
//            text?.let { MobileFrontService().getPriceAlertsList(it) }
//                ?.enqueue(object : retrofit2.Callback<PriceAlertsDTO> {
//                    override fun onResponse(
//                        call: Call<PriceAlertsDTO>,
//                        response: Response<PriceAlertsDTO>
//                    ) {
//                        if (response.code() == 200) {
//                            var data = response.body()
//                            if (data != null) {
//                                when (data?.response) {
//                                    "SUCCESS" -> {
//                                        onResult(data)
//                                    }
//                                    else -> {
//                                        ManageData().checkSessionAuthority(
//                                            data.response,
//                                            context
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<PriceAlertsDTO>, t: Throwable) {
//                        Log.e("ERROR", t.message.toString())
//
//                    }
//                })
//
//        }
//    }

//    fun newPriceAlert(
//        type: String,
//        code: String,
//        price: String, context: Context,
//        onResult: (PriceAlertsDTO?) -> Unit
//    ) {
//        var text = ManageData().getDataCoding(context)
//        val jsonObj = JSONObject()
//        jsonObj.put("type", type)
//        jsonObj.put("productCode", code)
//        jsonObj.put("price", ManageData().numberFormatToDecimal(price.toDouble()))
//        jsonObj.put("mobileEnable", true)
//        jsonObj.put("smsEnable", false)
//        jsonObj.put("emailEnable", false)
//        jsonObj.put("alerted", false)
//
//
//        val jsonObjectString = jsonObj.toString()
//        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
//        CoroutineScope(Dispatchers.IO).launch {
//            text?.let { MobileFrontService().addNewPriceAlert(it, requestBody) }
//                ?.enqueue(object : retrofit2.Callback<PriceAlertsDTO> {
//                    override fun onResponse(
//                        call: Call<PriceAlertsDTO>,
//                        response: Response<PriceAlertsDTO>
//                    ) {
//                        if (response.code() == 200) {
//                            var data = response.body()
//                            if (data != null) {
//                                Log.e(
//                                    "priceAlert.response ",
//                                    data.response
//                                )
//                                when (data.response) {
//
//                                    "SUCCESS" -> {
//                                        onResult(data)
//                                    }
//                                    else -> {
//                                        ManageData().checkSessionAuthority(
//                                            data?.response.toString(),
//                                            context
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<PriceAlertsDTO>, t: Throwable) {
//                        Log.e("ERROR priceAlert", t.message.toString())
//
//                    }
//
//                })
//
//        }
//
//    }


    fun deletePriceAlert(
        id: String,
        context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)

        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().deletePriceAlert(it, id) }
                .enqueue(object : retrofit2.Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
//                                Log.e(
//                                    "deletePriceAlert.response ",
//                                    data.response
//                                )
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)

                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("ERROR priceAlert", t.message.toString())

                    }

                })
        }
    }


    fun orderForMatch(
        productCode: String,
        context: Context,
        onResult: (OrderMatchDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().orderForMatch(it, productCode) }
                .enqueue(object : retrofit2.Callback<OrderMatchDTO> {
                    override fun onResponse(
                        call: Call<OrderMatchDTO>,
                        response: Response<OrderMatchDTO>
                    ) {
                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
//                                Log.e(
//                                    "orderForMatch.response ",
//                                    data.response
//                                )
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<OrderMatchDTO>, t: Throwable) {
                        Log.e("ERROR priceAlert", t.message.toString())

                    }

                })
        }
    }

    fun processClearPort(
//        payType: String,
//        buySelect: ArrayList<ProcessClearPortDTO>,
//        sellSelect: ArrayList<ProcessClearPortDTO>,
        clearPort: ProcessClearPortDTO,
        context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {
        var text = ManageData().getDataCoding(context)
//        val jsonObj = JSONObject()
//        jsonObj.put("payType", payType)
//        jsonObj.put("buySelect", buySelect)
//        jsonObj.put("sellSelect", sellSelect)
//        val jsonObjectString = jsonObj.toString()
//        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().processClearPort(it, clearPort) }
                .enqueue(object : retrofit2.Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
//                                Log.e(
//                                    "processClearPort.response ",
//                                    data.response
//                                )
                                when (data.response) {

                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("ERROR priceAlert", t.message.toString())

                    }

                })

        }

    }


    fun updateProduct(
        _productCode: List<String>,
        context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().update(it, _productCode) }
                .enqueue(object : retrofit2.Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
//                                Log.e(
//                                    "update.response ",
//                                    data.response
//                                )
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }

                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("ERROR", t.message.toString())

                    }
                })

        }
    }


    fun getNotifications(
        context: Context,
        onResult: (NotificationDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        if (text != null) {

            MobileFrontService.invoke().getNotifications(text).enqueue(object :
                retrofit2.Callback<NotificationDTO> {
                override fun onResponse(
                    call: Call<NotificationDTO>,
                    response: Response<NotificationDTO>
                ) {

                    if (response.code() == 200) {
                        var data = response.body()
                        if (data != null) {
                            when (data.response) {
                                "SUCCESS" -> {
                                    onResult(data)
                                }
                                "INVALID_CREDENTIAL" -> {
                                    ManageData().checkSessionAuthority(
                                        data.response.toString(),
                                        context
                                    )
                                }
                                else -> {
                                    onResult(data)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationDTO>, t: Throwable) {
                    Log.e("onFailure:", t.message.toString())
                }
            })


        }
    }


    fun getNoticeMessageList(
        firstResult: String,
        maxtResult: String,
        context: Context,
        onResult: (NotificationDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        if (text != null) {

            MobileFrontService.invoke().getNoticeMessageList(text, firstResult, maxtResult)
                .enqueue(object :
                    retrofit2.Callback<NotificationDTO> {
                    override fun onResponse(
                        call: Call<NotificationDTO>,
                        response: Response<NotificationDTO>
                    ) {

                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<NotificationDTO>, t: Throwable) {
                        Log.e("onFailure:", t.message.toString())
                    }
                })


        }
    }


    fun noticeRead(
        id: String,
        context: Context,
        onResult: (ResponseDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().noticeRead(it, id) }
                .enqueue(object : retrofit2.Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
//                                Log.e(
//                                    "update.response ",
//                                    data.response
//                                )
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }

                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("ERROR", t.message.toString())

                    }
                })

        }
    }


    fun noticeReadall(
        context: Context,
        onResult: (NotificationDTO?) -> Unit
    ) {

        var text = ManageData().getDataCoding(context)
        CoroutineScope(Dispatchers.IO).launch {
            text.let { MobileFrontService().noticeReadall(it) }
                .enqueue(object : retrofit2.Callback<NotificationDTO> {
                    override fun onResponse(
                        call: Call<NotificationDTO>,
                        response: Response<NotificationDTO>
                    ) {

                        if (response.code() == 200) {
                            var data = response.body()
                            if (data != null) {
                                Log.e(
                                    "update.response ",
                                    data.response
                                )
                                when (data.response) {
                                    "SUCCESS" -> {
                                        onResult(data)
                                    }
                                    "INVALID_CREDENTIAL" -> {
                                        ManageData().checkSessionAuthority(
                                            data.response.toString(),
                                            context
                                        )
                                    }
                                    else -> {
                                        onResult(data)
                                    }
                                }
                            }

                        }
                    }

                    override fun onFailure(call: Call<NotificationDTO>, t: Throwable) {
                        Log.e("ERROR", t.message.toString())

                    }
                })

        }
    }

    fun newAccount(
        name: String,
        accId: String,
        email: String, onResult: (ResponseDTO?) -> Unit
    ) {
        var param: Map<String, String> = mapOf(
            "name" to name,
            "phone" to accId,
            "email" to email
        )
        MobileFrontService.invoke().newAccount(param).enqueue(object :
            retrofit2.Callback<ResponseDTO> {
            override fun onResponse(
                call: Call<ResponseDTO>,
                response: Response<ResponseDTO>
            ) {
                if (response.code() == 200) {
                    onResult(response.body())
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.e("onFailure:", t.message.toString())
            }


        })

    }

    fun forgotPassword(
        username: String,
        email: String, onResult: (ResponseDTO?) -> Unit
    ) {

        MobileFrontService.invoke().forgotPassword(username,email).enqueue(object :
            retrofit2.Callback<ResponseDTO> {
            override fun onResponse(
                call: Call<ResponseDTO>,
                response: Response<ResponseDTO>
            ) {
                if (response.code() == 200) {
                    onResult(response.body())
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.e("onFailure:", t.message.toString())
            }


        })

    }
}
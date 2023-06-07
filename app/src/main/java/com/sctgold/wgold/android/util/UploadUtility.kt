package com.sctgold.wgold.android.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sctgold.wgold.android.R
import com.sctgold.android.tradeplus.util.ManageData
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class UploadUtility(activity: Activity) {

    var activity = activity
    var dialog: ProgressDialog? = null
    //  var serverURL: String = "https://www.sctgoldspot.com/sctgoldFO/mobile/upload"
    var serverUploadDirectoryPath: String = " /opt/goldspot/SCT_images/mobile_upload"
    val client = OkHttpClient()

//    fun uploadFile(sourceFilePath: String, uploadedFileName: String? = null) {
//        uploadFile(File(sourceFilePath), uploadedFileName)
//    }

    fun uploadFile(sourceFileUri: Uri, context: Context) {
        uploadFile(sourceFileUri, "uploadedFileName", context)
    }

    @Throws(Exception::class)
    fun createTransactionID(): String? {
        return "----------" + UUID.randomUUID().toString()
    }






    fun uploadFile(sourceFile: Uri, uploadedFileName: String? = null, context: Context) {
        Thread {

            var bos: ByteArrayOutputStream? = null
//            val mimeType = getMimeType(sourceFile);
//            if (mimeType == null) {
//                Log.e("file error", "Not able to get mime type")
//                return@Thread
//            }
            val MEDIA_TYPE_JPG = "image/jpeg".toMediaTypeOrNull()
//            val fileName: String = uploadedFileName ?: sourceFile.name
//            toggleProgressDialog(true)
            val boundary = createTransactionID()

            try {

                val filePath = URIPathHelper().getPath(context, sourceFile)
                var text = ManageData().getDataCoding(context)
                bos = ByteArrayOutputStream()

                PropertiesKotlin().scaleImage(filePath, bos)

//                        GoldSpotCache cache = GoldSpotCache.getInstance();
                val requestBody: RequestBody =  MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "filename",
                        "payment.jpg",
                        RequestBody.create(MEDIA_TYPE_JPG, bos.toByteArray())
                    )
                    .build()


                val request: Request =   Request.Builder()
                    .header("token", text)
                    .url(PropertiesKotlin.serverURL)
                    .post(requestBody)
                    .build()

                Log.e("File upload",filePath.toString())
//                val file = File(filePath)

//                val requestBody: RequestBody =
//                    MultipartBody.Builder().setType(MultipartBody.FORM)
//                        .addFormDataPart(
//                            "filename", "payment.jpg",
//                            file
//                                .asRequestBody("multipart/form-data; boundary=$boundary".toMediaTypeOrNull())
//
//                        )
//                        .build()

//                val request: Request? =
//
//                    Request.Builder().url(PropertiesKotlin.serverURL).header("token", text).post(requestBody).build()


                val response: Response? = request?.let { client.newCall(it).execute() }
                Log.e("File upload",response.toString())
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.e("File upload", "success, path: ")
                        toggleProgressDialog(true, "SUCCESS", context)
                    } else {
                        Log.e("File upload", "failed  1")
                        toggleProgressDialog(true, response.message, context)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed  2")
                toggleProgressDialog(false, "failed", context)

            }

            //    toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun toggleProgressDialog(show: Boolean, response: String, context: Context) {
        val builder: AlertDialog.Builder? = activity.let {
            AlertDialog.Builder(it)
        }
        activity.runOnUiThread {
            if (show) {
                if (response == "SUCCESS") {
                    builder?.setTitle(context.resources.getString(R.string.alert_ok))
                        ?.setMessage(context.resources.getString(R.string.upload_success))
                        ?.setPositiveButton(context.resources.getString(R.string.btn_close),
                            DialogInterface.OnClickListener { dialog, id ->
                                builder.create().dismiss()
                            })
                }else{
                    builder?.setTitle(context.resources.getString(R.string.failed))
                        ?.setMessage(context.resources.getString(R.string.send_again))
                        ?.setPositiveButton(context.resources.getString(R.string.btn_ok),
                            DialogInterface.OnClickListener { dialog, id ->
                                builder.create().dismiss()
                            })
                }

//                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                builder?.create()?.dismiss()
            }
            builder?.create()?.show()
        }
    }


}

package com.sctgold.goldinvest.android.activities

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.layoutDirection
import java.util.*

class IntentActivity  : AppCompatActivity()  {

    val TAG : String = "LANG"

    companion object{
        private val instance: IntentActivity? = null
        fun getInstance(): IntentActivity? {
            return instance
        }
    }



    fun someFunction(lang: String ,resources:Resources ) {
        Log.i(TAG , lang)
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)

//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                updateResources(context, lang)
//            } else updateResourcesLegacy(context, lang)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Will give the direction of the layout depending of the Locale you've just set
        window.decorView.layoutDirection = Locale.getDefault().layoutDirection
    }

//    fun setLocale(lang: String?) :Boolean {
//        Log.e("lang  ",lang.toString())
//        val myLocale = Locale(lang)
//        val res: Resources = resources
//        val dm: DisplayMetrics = res.displayMetrics
//        val conf: Configuration = res.configuration
//        conf.locale = myLocale
//        res.updateConfiguration(conf, dm)
//       return  true
//    }
}

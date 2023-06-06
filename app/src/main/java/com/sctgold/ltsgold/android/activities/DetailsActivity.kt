package com.sctgold.ltsgold.android.activities


import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sctgold.ltsgold.android.R


class DetailsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(HomeActivity.NOTIFICATION_ID)


    }
//    fun addRadioButton(text:String) : RadioButton {
//        val radioButton1 = RadioButton(this)
//        radioButton1.text = text
//        return radioButton1;
//
//    }
    companion object {
        private val instance: DetailsActivity? = null
        fun getInstance(): DetailsActivity? {
            return instance
        }
//
//        fun aaRadioButton(text:String) : RadioButton? {
//            return getInstance()?.addRadioButton(text)
//
//        }
    }
}
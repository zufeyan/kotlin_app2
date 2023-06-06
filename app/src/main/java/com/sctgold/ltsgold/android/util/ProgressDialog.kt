package com.sctgold.ltsgold.android.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.sctgold.ltsgold.android.R

class ProgressDialog {
    companion object {
        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.layout_progress_bar, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            dialog.setCancelable(true)

            return dialog
        }


        fun networkDialog(context: Context): Dialog {

            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.msg_check_network)
            builder.setPositiveButton("OK") { dialog, which ->
            }
            //            dialog.show()

            return builder.create()
        }

    }
}
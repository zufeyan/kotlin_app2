package com.sctgold.wgold.android.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.listener.OnClickDialogListener
import com.sctgold.wgold.android.util.AppUtil


class StandardDialog : AppCompatActivity() {
    fun showMessageDialog(
        context: Context,
        title: String?,
        message: String?,
        okTitle: String?,
        dialogListener: OnClickDialogListener
    ): Dialog? {
        val builder = AlertDialog.Builder(context)
        val viewDialog: View =
            LayoutInflater.from(context).inflate(R.layout.form_alert_simple, null)
        val body = viewDialog.findViewById<TextView>(R.id.body_alert_text)
        if (!AppUtil.isEmpty(message)) {
            body.text = message
        }
        builder.setView(viewDialog)
        builder.setPositiveButton(
            okTitle
        ) { dialog, which -> dialogListener.onClickPositiveButton(dialog) }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setOnShowListener {
            val btnPositive =
                (alertDialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextColor(ContextCompat.getColor(context, R.color.alert_button_color))
            btnPositive.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.dialog_button)
            )
        }
        return alertDialog
    }

//    fun createProgressDialog(mContext: Context): ProgressDialog? {
//        val outValue = TypedValue()
//        mContext.resources.getValue(R.dimen.refresher_, outValue, true)
//        val refresherFontSize = outValue.float
//        val title: String = mContext.getString(R.string.wait)
//        val ssTitle = SpannableString(title)
//        ssTitle.setSpan(RelativeSizeSpan(refresherFontSize), 0, ssTitle.length, 0)
//        val message: String = mContext.getString(R.string.loading)
//        val ssMessage = SpannableString(message)
//        ssMessage.setSpan(RelativeSizeSpan(refresherFontSize), 0, ssMessage.length, 0)
//        val refresher = ProgressDialog(mContext, R.style.DialogProgressStyle)
//        refresher.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        refresher.setTitle(ssTitle)
//        refresher.setMessage(ssMessage)
//        refresher.setMax(max)
//        refresher.setIndeterminate(false)
//        refresher.setCancelable(false)
//        refresher.setCanceledOnTouchOutside(false)
//        return refresher
//    }


}

package com.sctgold.goldinvest.android.listener

import android.content.Intent




interface OnActionResultListener {
    fun actionResult(requestCode: Int, resultCode: Int, data: Intent?)
}

package com.sctgold.goldinvest.android.listener

interface OnResultPermissionCallbackListener {
    fun permissionCallback(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
}

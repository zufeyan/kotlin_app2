package com.sctgold.ltsgold.android.listener

interface OnResultPermissionCallbackListener {
    fun permissionCallback(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
}
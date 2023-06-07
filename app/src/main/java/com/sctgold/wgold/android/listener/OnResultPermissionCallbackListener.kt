package com.sctgold.wgold.android.listener

interface OnResultPermissionCallbackListener {
    fun permissionCallback(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
}

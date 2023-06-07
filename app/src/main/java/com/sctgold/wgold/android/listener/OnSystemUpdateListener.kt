package com.sctgold.wgold.android.listener

import com.sctgold.wgold.android.dto.SystemDTO


interface OnSystemUpdateListener {
    fun onSystemUpdate(systemDTO: SystemDTO?)
}

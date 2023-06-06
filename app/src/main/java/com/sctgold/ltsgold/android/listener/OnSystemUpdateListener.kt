package com.sctgold.ltsgold.android.listener

import com.sctgold.ltsgold.android.dto.SystemDTO


interface OnSystemUpdateListener {
    fun onSystemUpdate(systemDTO: SystemDTO?)
}
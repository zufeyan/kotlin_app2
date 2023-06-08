package com.sctgold.goldinvest.android.listener

import com.sctgold.goldinvest.android.dto.SystemDTO


interface OnSystemUpdateListener {
    fun onSystemUpdate(systemDTO: SystemDTO?)
}

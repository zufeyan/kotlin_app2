package com.sctgold.ltsgold.android.util

import com.sctgold.ltsgold.android.fragments.ClearPortActionFragment

interface OnItemClickListener {
    fun onSumUnit(unit: Double)
    fun onListSelect(select:  ArrayList<ClearPortActionFragment.DataMatch>)
    fun onListUnSelect(unSelect:  ArrayList<ClearPortActionFragment.DataMatch>)
    fun onButtonDis(btnDisable: Boolean)
    fun onSumTotal(total: Double)
}


interface OnItemSelectListener {
    fun onCheckSwitch(btnDisable: Boolean)
}
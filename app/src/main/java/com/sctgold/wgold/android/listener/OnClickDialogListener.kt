package com.sctgold.wgold.android.listener

import android.content.DialogInterface

interface OnClickDialogListener {

    fun onClickPositiveButton(dialog: DialogInterface?)
    fun onClickNegativeButton(dialog: DialogInterface?)
}

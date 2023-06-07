package com.sctgold.wgold.android.util

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter(digits: Int) : InputFilter {
    var decimalDigits = digits

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        var dotPos = -1
        val len: Int = dest!!.count()
        for (i in 0 until len) {
            val c: Char = dest[i]
            if (c == '.' || c == ',') {
                dotPos = i
                break
            }
        }
        if (dotPos >= 0) {
            if (source?.equals(".") == true || source?.equals(",") == true) {
                return ""
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        }
        return null
    }
}

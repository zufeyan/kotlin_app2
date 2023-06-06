package com.sctgold.ltsgold.android.dto

class MobileProductSetting {

    var code: String? = null
    var name: String? = null
    var view = false
    var productColor: String? = null

    constructor(code: String?, name: String?, view: Boolean, productColor: String?) {
        this.code = code
        this.name = name
        this.view = view
        this.productColor = productColor
    }
}
package com.sctgold.wgold.android.util


import java.lang.StringBuilder


class TokenUtil {
    fun getDefaultToken(branchCode: String, deviceId: String, token: String): String? {
        if (AppUtil.isEmpty(branchCode) || AppUtil.isEmpty(deviceId) || AppUtil.isEmpty(token)) {
//            LogUtil.debug(
//                TAG,
//                "something is null: branchCode: {}, deviceId: {}, token: {}",
//                branchCode,
//                deviceId,
//                token
//            )
            return null
        }
        val builder = StringBuilder()
        builder.append(getToken(branchCode, TokenType.BRC))
        builder.append(getToken(deviceId, TokenType.DID))
        builder.append(getToken(token, TokenType.TKN))
        return builder.toString()
    }

    private fun getToken(value: Long, tokenType: TokenType): String? {
        var _tmp = value.toString()
        if (tokenType.isEncrypt) {
            _tmp = EncryptUtil.md5(_tmp)
        }
        return String.format(tokenType.paddingFormat, _tmp)
    }

    private fun getToken(value: String, tokenType: TokenType): String? {
        var _tmp = value
        if (tokenType.isEncrypt) {
            _tmp = EncryptUtil.md5(_tmp)
        }
        return String.format(tokenType.paddingFormat, _tmp)
    }

    private enum class TokenType {
        BRC(5), ACC(10), DID(40), PWD(30, java.lang.Boolean.TRUE), NPW(
            30,
            java.lang.Boolean.TRUE
        ),
        PIN(30, java.lang.Boolean.TRUE), NPN(30, java.lang.Boolean.TRUE), TKN(40), OID(10);

        private var lenght = 0
        var isEncrypt = java.lang.Boolean.FALSE
        var paddingFormat: String

        constructor(length: Int) {
            lenght = length
            paddingFormat = "%1$-" + length + "s"
        }

        constructor(length: Int, isEncrypt: Boolean) {
            lenght = length
            this.isEncrypt = isEncrypt
            paddingFormat = "%1$-" + length + "s"
        }
    }
}

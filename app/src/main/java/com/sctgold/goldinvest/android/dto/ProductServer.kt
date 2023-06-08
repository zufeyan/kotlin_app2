package com.sctgold.goldinvest.android.dto

class ProductServer {
    private var id: Long = 0
    private var code: String? = null
    private var name: String? = null
    private var nameEn: String? = null

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getCode(): String? {
        return code
    }

    fun setCode(code: String?) {
        this.code = code
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getNameEn(): String? {
        return nameEn
    }

    fun setNameEn(nameEn: String?) {
        this.nameEn = nameEn
    }

    override fun toString(): String {
        val sb = StringBuffer("ProductServer{")
        sb.append("id=").append(id)
        sb.append(", code='").append(code).append('\'')
        sb.append(", name='").append(name).append('\'')
        sb.append(", nameEn='").append(nameEn).append('\'')
        sb.append('}')
        return sb.toString()
    }
}

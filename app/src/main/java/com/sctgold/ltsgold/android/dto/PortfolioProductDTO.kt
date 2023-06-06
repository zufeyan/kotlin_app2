package com.sctgold.ltsgold.android.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.concurrent.CopyOnWriteArrayList

data class PortfolioProductDTO(
    @SerializedName("response") var response: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("timeStamp") var timeStamp: String?,
    @SerializedName("mobilePortfolioProductList") var mobilePortfolioProductList: List<PortfolioProductDetailDTO>,
    @SerializedName("mobilePortfolioList") var mobilePortfolioList: List<PortfolioDTO>,
    @SerializedName("totalProfitLoss") var totalProfitLoss: String?,
    @SerializedName("mobileCusStocks") var mobileCusStocksList: List<CusStocksDTO>,
    @SerializedName("mobileCustomerPortAccruedBacklog") var mobileCustomerPortAccruedBacklogList: CopyOnWriteArrayList<CustomerPortAccruedBacklogDTO>,
    @SerializedName("cashDepositMargin") var cashDepositMargin: String?,
    @SerializedName("cashDeposit") var cashDeposit: String?,
    @SerializedName("cashDepositBuy") var cashDepositBuy: String?,
    @SerializedName("holdMoney") var holdMoney: String?,
    @SerializedName("holdMoneyTmp") var holdMoneyTmp: String?,
    @SerializedName("arAmount") var arAmount: String?,
    @SerializedName("apAmount") var apAmount: String?,
    @SerializedName("goldDepositMargin") var goldDepositMargin: String?,
    @SerializedName("goldDepositSell") var goldDepositSell: String?,
    @SerializedName("goldDepositBuy") var goldDepositBuy: String?,
    @SerializedName("goldSaving") var goldSaving: String?,
    @SerializedName("realizeAmountSummary") var realizeAmountSummary: String?,
    @SerializedName("lastUpdated") var lastUpdated: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("mobilePortfolioProductList"),
        TODO("mobilePortfolioList"),
        parcel.readString(),
        TODO("mobileCusStocksList"),
        TODO("mobileCustomerPortAccruedBacklogList"),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(response)
        parcel.writeString(message)
        parcel.writeString(timeStamp)
        parcel.writeString(totalProfitLoss)
        parcel.writeString(cashDepositMargin)
        parcel.writeString(cashDeposit)
        parcel.writeString(cashDepositBuy)
        parcel.writeString(holdMoney)
        parcel.writeString(holdMoneyTmp)
        parcel.writeString(arAmount)
        parcel.writeString(apAmount)
        parcel.writeString(goldDepositMargin)
        parcel.writeString(goldDepositSell)
        parcel.writeString(goldDepositBuy)
        parcel.writeString(goldSaving)
        parcel.writeString(realizeAmountSummary)
        parcel.writeString(lastUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PortfolioProductDTO> {
        override fun createFromParcel(parcel: Parcel): PortfolioProductDTO {
            return PortfolioProductDTO(parcel)
        }

        override fun newArray(size: Int): Array<PortfolioProductDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class PortfolioProductDetailDTO(
    @SerializedName("productName") var productName: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productUnit") var productUnit: String?,
    @SerializedName("productColor") var productColor: String?,
    @SerializedName("portProductPercent") var portProductPercent: Float,

    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeString(productCode)
        parcel.writeString(productUnit)
        parcel.writeString(productColor)
        parcel.writeFloat(portProductPercent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PortfolioProductDetailDTO> {
        override fun createFromParcel(parcel: Parcel): PortfolioProductDetailDTO {
            return PortfolioProductDetailDTO(parcel)
        }

        override fun newArray(size: Int): Array<PortfolioProductDetailDTO?> {
            return arrayOfNulls(size)
        }
    }

}


data class PortfolioDTO(
    @SerializedName("productName") var productName: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productUnit") var productUnit: String?,
    @SerializedName("productColor") var productColor: String?,
    @SerializedName("currencySymbol") var currencySymbol: String?,
    @SerializedName("buAverageCost") var buAverageCost: String?,
    @SerializedName("seAverageCost") var seAverageCost: String?,
    @SerializedName("buMarketPrice") var buMarketPrice: String?,
    @SerializedName("seMarketPrice") var seMarketPrice: String?,
    @SerializedName("buProductAmount") var buProductAmount: String?,
    @SerializedName("seProductAmount") var seProductAmount: String?,
    @SerializedName("buProfitLoss") var buProfitLoss: Double,
    @SerializedName("seProfitLoss") var seProfitLoss: Double,
    @SerializedName("profitLoss") var profitLoss: String?,
    @SerializedName("origPointBuy") var origPointBuy: String?,
    @SerializedName("pointBuy") var pointBuy: String?,
    @SerializedName("origPointSell") var origPointSell: String?,
    @SerializedName("pointSell") var pointSell: String?,
    @SerializedName("percentBuy") var percentBuy: String?,
    @SerializedName("percentBuyRemain") var percentBuyRemain: String?,
    @SerializedName("percentSell") var percentSell: String?,
    @SerializedName("percentSellRemain") var percentSellRemain: String?,
    @SerializedName("realizePl") var realizePl: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeString(productCode)
        parcel.writeString(productUnit)
        parcel.writeString(productColor)
        parcel.writeString(currencySymbol)
        parcel.writeString(buAverageCost)
        parcel.writeString(seAverageCost)
        parcel.writeString(buMarketPrice)
        parcel.writeString(seMarketPrice)
        parcel.writeString(buProductAmount)
        parcel.writeString(seProductAmount)
        parcel.writeDouble(buProfitLoss)
        parcel.writeDouble(seProfitLoss)
        parcel.writeString(profitLoss)
        parcel.writeString(origPointBuy)
        parcel.writeString(pointBuy)
        parcel.writeString(origPointSell)
        parcel.writeString(pointSell)
        parcel.writeString(percentBuy)
        parcel.writeString(percentBuyRemain)
        parcel.writeString(percentSell)
        parcel.writeString(percentSellRemain)
        parcel.writeString(realizePl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PortfolioDTO> {
        override fun createFromParcel(parcel: Parcel): PortfolioDTO {
            return PortfolioDTO(parcel)
        }

        override fun newArray(size: Int): Array<PortfolioDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class CusStocksDTO(
    @SerializedName("depositMargin") var depositMargin: String?,
    @SerializedName("depositSell") var depositSell: String?,
    @SerializedName("depositBuy") var depositBuy: String?,
    @SerializedName("goldSaving") var goldSaving: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
    @SerializedName("productColor") var productColor: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(depositMargin)
        parcel.writeString(depositSell)
        parcel.writeString(depositBuy)
        parcel.writeString(goldSaving)
        parcel.writeString(productCode)
        parcel.writeString(productName)
        parcel.writeString(productColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CusStocksDTO> {
        override fun createFromParcel(parcel: Parcel): CusStocksDTO {
            return CusStocksDTO(parcel)
        }

        override fun newArray(size: Int): Array<CusStocksDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class CustomerPortAccruedBacklogDTO(
    @SerializedName("seqNo") var seqNo: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
    @SerializedName("productColor") var productColor: String?,
    @SerializedName("customerPortfolioAccruedDTO") var customerPortfolioAccruedDTO: CustomerPortfolioAccruedDTO?,
    @SerializedName("customerPortfolioBacklogDTO") var customerPortfolioBacklogDTO: CustomerPortfolioBacklogDTO?,

    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(CustomerPortfolioAccruedDTO::class.java.classLoader),
        parcel.readParcelable(CustomerPortfolioBacklogDTO::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(seqNo)
        parcel.writeString(productCode)
        parcel.writeString(productName)
        parcel.writeString(productColor)
        parcel.writeParcelable(customerPortfolioAccruedDTO, flags)
        parcel.writeParcelable(customerPortfolioBacklogDTO, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerPortAccruedBacklogDTO> {
        override fun createFromParcel(parcel: Parcel): CustomerPortAccruedBacklogDTO {
            return CustomerPortAccruedBacklogDTO(parcel)
        }

        override fun newArray(size: Int): Array<CustomerPortAccruedBacklogDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class CustomerPortfolioAccruedDTO(
    @SerializedName("weight") var weight: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(weight)
        parcel.writeString(productCode)
        parcel.writeString(productName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerPortfolioAccruedDTO> {
        override fun createFromParcel(parcel: Parcel): CustomerPortfolioAccruedDTO {
            return CustomerPortfolioAccruedDTO(parcel)
        }

        override fun newArray(size: Int): Array<CustomerPortfolioAccruedDTO?> {
            return arrayOfNulls(size)
        }
    }

}

data class CustomerPortfolioBacklogDTO(
    @SerializedName("weight") var weight: String?,
    @SerializedName("productCode") var productCode: String?,
    @SerializedName("productName") var productName: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(weight)
        parcel.writeString(productCode)
        parcel.writeString(productName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerPortfolioBacklogDTO> {
        override fun createFromParcel(parcel: Parcel): CustomerPortfolioBacklogDTO {
            return CustomerPortfolioBacklogDTO(parcel)
        }

        override fun newArray(size: Int): Array<CustomerPortfolioBacklogDTO?> {
            return arrayOfNulls(size)
        }
    }

}
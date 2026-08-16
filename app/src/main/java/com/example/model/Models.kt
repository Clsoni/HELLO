package com.example.model

data class Product(
    val id: String,
    val name: String,
    val buy: Double?,
    val sell: Double?,
    val buyHigh: Double? = null,
    val buyLow: Double? = null,
    val sellHigh: Double? = null,
    val sellLow: Double? = null,
    val buyPremium: Double = 0.0,
    val sellPremium: Double = 0.0,
    val isPhysical: Boolean = true,
    val isBuyHidden: Boolean = false,
    val isSellHidden: Boolean = false,
    val isRowHidden: Boolean = false
)

data class MarketQuote(
    val id: String,
    val symbol: String,
    val bid: Double,
    val ask: Double,
    val high: Double? = null,
    val low: Double? = null
)

data class BroadcastMessage(
    val id: String,
    val type: String,
    val date: String,
    val text: String
)

data class BankAccount(
    val bankName: String,
    val accountNo: String,
    val ifsc: String,
    val branch: String,
    val type: String
)

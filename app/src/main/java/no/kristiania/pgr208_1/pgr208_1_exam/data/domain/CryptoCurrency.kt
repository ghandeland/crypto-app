package no.kristiania.pgr208_1.pgr208_1_exam.data.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Outer list class since the API is serving an array
@JsonClass(generateAdapter = true)
data class CurrencyList(
    @field:Json(name = "data")
    val data: List<CryptoCurrency>
)

// Inner data class to access object properties
data class CryptoCurrency (
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val priceUsd: String,
    val changePercent24Hr: String
)
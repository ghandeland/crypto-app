package no.kristiania.pgr208_1.exam.data.api.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Outer list class since the API is serving an array
@JsonClass(generateAdapter = true)
data class CurrencyList(
    @field:Json(name = "data")
    val data: List<CryptoCurrency>
)

@JsonClass(generateAdapter = true)
data class CurrencyFetch(
        @field:Json(name = "data")
        val currency: CryptoCurrency
)

// Inner data class to access object properties
data class CryptoCurrency (
        val id: String,
        val rank: String,
        val symbol: String,
        var name: String,
        var priceUsd: String,
        val changePercent24Hr: String
)
package no.kristiania.pgr208_1.pgr208_1_exam.data.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyList(
    @field:Json(name = "data")
    val data: List<CryptoCurrency>
)

data class CryptoCurrency (
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val changePercent24Hr: String
)
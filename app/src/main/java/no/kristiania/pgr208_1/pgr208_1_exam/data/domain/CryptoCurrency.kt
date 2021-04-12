package no.kristiania.pgr208_1.pgr208_1_exam.data.domain

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CryptoCurrency (
    val id: String,
    val rank: Long,
    val symbol: String,
    val name: String,
    val changePercent24Hr: Long
)
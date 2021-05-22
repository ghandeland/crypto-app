package no.kristiania.pgr208_1.exam.data

import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency

// Both Currency types (Balance from DB and Currency from API-fetch) merged down to one object
class CurrencyComplete (
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val balance: Double
        )
package no.kristiania.pgr208_1.exam.data.api

import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.data.api.domain.CurrencyFetch
import no.kristiania.pgr208_1.exam.data.api.domain.CurrencyList
import retrofit2.http.GET
import retrofit2.http.Path


interface CoinCapService {
    @GET("v2/assets")
    suspend fun getAssets(): CurrencyList

    @GET("v2/assets/{currencyCode}")
    suspend fun getAsset(@Path("currencyCode") currencyCode: String): CurrencyFetch
}
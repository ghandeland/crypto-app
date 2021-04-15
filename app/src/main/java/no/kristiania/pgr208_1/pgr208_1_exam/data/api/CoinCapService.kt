package no.kristiania.pgr208_1.pgr208_1_exam.data.api

import no.kristiania.pgr208_1.pgr208_1_exam.data.api.domain.CurrencyList
import retrofit2.http.GET


interface CoinCapService {
    @GET("v2/assets")
    suspend fun getAssets(): CurrencyList


}
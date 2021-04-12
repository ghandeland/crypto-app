package no.kristiania.pgr208_1.pgr208_1_exam.data

import no.kristiania.pgr208_1.pgr208_1_exam.data.domain.CryptoCurrency
import retrofit2.http.GET


interface CoinCapService {
    @GET("v2/assets")
    suspend fun getAssets(): CryptoCurrency


}
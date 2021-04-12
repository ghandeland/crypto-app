package no.kristiania.pgr208_1.pgr208_1_exam.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(" https://api.coincap.io/")
    .build()

// API object
object API {
    val coinCapService: CoinCapService by lazy {
        retrofit.create(CoinCapService::class.java)
    }
}
package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.WalletCurrency

@Dao
interface WalletCurrencyDao {
    @Insert
    suspend fun insert(wallet: WalletCurrency)

    @Query("SELECT * FROM wallet_currency_table")
    suspend fun getWallet(): List<WalletCurrency>

    @Query("SELECT * FROM wallet_currency_table WHERE currencyCode = :currencyCode")
    suspend fun getCurrency(currencyCode: String): WalletCurrency
}
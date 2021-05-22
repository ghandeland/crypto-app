package no.kristiania.pgr208_1.exam.data.db

import androidx.room.*
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance

@Dao
interface CurrencyBalanceDao {
    @Insert
    suspend fun insert(balance: CurrencyBalance)

    @Query("SELECT * FROM currency_balance_table")
    suspend fun getPortfolio(): List<CurrencyBalance>

    @Query("SELECT * FROM currency_balance_table WHERE currencyId = :currencyId")
    suspend fun getCurrency(currencyId: String): CurrencyBalance

    @Update
    suspend fun update(newBalance: CurrencyBalance)

    @Delete
    suspend fun delete(currencyBalance: CurrencyBalance)

}
package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.CurrencyBalance

@Dao
interface CurrencyBalanceDao {
    @Insert
    suspend fun insert(balance: CurrencyBalance)

    @Query("SELECT * FROM currency_balance_table")
    suspend fun getWallet(): List<CurrencyBalance>

    @Query("SELECT * FROM currency_balance_table WHERE currencyId = :currencyId")
    suspend fun getCurrency(currencyId: String): CurrencyBalance

    @Update
    suspend fun update(newBalance: CurrencyBalance)

}
package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BalanceDao {
    @Query("SELECT balance_value FROM balance WHERE balanceId = :balId")
    suspend fun getBalance(balId: Int): Double
}
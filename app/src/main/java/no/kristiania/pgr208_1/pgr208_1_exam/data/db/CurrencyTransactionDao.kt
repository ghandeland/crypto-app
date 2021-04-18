package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.CurrencyTransaction

@Dao
interface CurrencyTransactionDao {
    @Query("SELECT * FROM currency_transaction_table")
    suspend fun listTransactions(): List<CurrencyTransaction>

    @Insert
    suspend fun insert(transaction: CurrencyTransaction)
}
package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.BalanceTransaction

@Dao
interface BalanceTransactionDao {
    @Query("SELECT * FROM balance_transaction_table")
    suspend fun listTransactions(): List<BalanceTransaction>

    @Insert
    suspend fun insert(transaction: BalanceTransaction)
}
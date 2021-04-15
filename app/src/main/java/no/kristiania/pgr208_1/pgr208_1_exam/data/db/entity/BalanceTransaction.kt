package no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "balance_transaction_table")
data class BalanceTransaction (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val tx_value: Double,
    val isDeposit: Boolean
    )
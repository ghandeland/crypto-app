package no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currency_transaction_table")
data class CurrencyTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val currencyId: String,
    val currencyAmount: Double,
    val currencyPrice: Double,
    val usdAmount: Double,
    val isBuy: Boolean
    )
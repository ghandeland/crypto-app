package no.kristiania.pgr208_1.exam.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*


@Entity(tableName = "currency_transaction_table")
data class CurrencyTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val currencySymbol: String,
    val currencyAmount: Double,
    val currencyPrice: Double,
    val usdAmount: Double,
    val isBuy: Boolean,
    val transactionDate: String?
    )
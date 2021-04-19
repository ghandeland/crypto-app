package no.kristiania.pgr208_1.exam.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_balance_table")
data class CurrencyBalance (
        @PrimaryKey
        val currencyId: String,
        val amount: Double,
        val symbol: String
)
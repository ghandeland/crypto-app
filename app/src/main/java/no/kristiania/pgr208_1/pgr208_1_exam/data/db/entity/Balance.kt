package no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance (
    @PrimaryKey val balanceId: Int,
    @ColumnInfo(name = "balance_value") val balanceValue: Double
)
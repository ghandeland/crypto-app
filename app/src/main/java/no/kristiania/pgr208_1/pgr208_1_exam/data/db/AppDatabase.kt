package no.kristiania.pgr208_1.pgr208_1_exam.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.BalanceTransaction
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.WalletCurrency

const val DATABASE_NAME = "crypto_app_db"

@Database(entities = [WalletCurrency::class, BalanceTransaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao() : WalletCurrencyDao
    abstract fun balanceTransactionDao() : BalanceTransactionDao

    companion object {
        private var db: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val newDb =
                db ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            return newDb.also {
                db = it
            }
        }

    }
}
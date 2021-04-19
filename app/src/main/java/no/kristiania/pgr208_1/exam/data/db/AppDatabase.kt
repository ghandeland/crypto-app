package no.kristiania.pgr208_1.exam.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyTransaction
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance

const val DATABASE_NAME = "crypto_app_db"

@Database(entities = [CurrencyBalance::class, CurrencyTransaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyTransactionDao() : CurrencyTransactionDao
    abstract fun currencyBalanceDao() : CurrencyBalanceDao

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
package no.kristiania.pgr208_1.pgr208_1_exam

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivitySplashScreenBinding

private const val TRANSACTION_FLAG_KEY = "no.kristiania.pgr208_1.exam.NEW_TRANSACTION_KEY"

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        viewModel.init(this) // Init Viewmodel for initial DB setup

        // Temporary toggle call to test first installation
        // toggleTransaction(true)

        // Check shared preferences for flag that indicates if initial transaction to DB should be executed
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val makeInitialTransaction = sharedPref.getBoolean(TRANSACTION_FLAG_KEY, true)

        Log.d("db", makeInitialTransaction.toString())

        // If stored key is not found or true, make the initial deposit to the DB
        if(makeInitialTransaction) {
            viewModel.makeInitialDeposit()
            toggleTransaction(false) // Toggle off transaction for next onCreate()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 50)
    }

    private fun toggleTransaction(makeNewTransactionNextStartup: Boolean) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(TRANSACTION_FLAG_KEY, makeNewTransactionNextStartup)
            apply()
        }
    }

}
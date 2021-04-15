package no.kristiania.pgr208_1.pgr208_1_exam

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivitySplashScreenBinding

private const val TRANSACTION_FLAG_KEY = "no.kristiania.pgr208_1.exam.NEW_TRANSACTION_KEY"

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // Check shared preferences for flag that indicates if initial transaction to DB should be executed
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val makeInitialTransaction = sharedPref.getBoolean(TRANSACTION_FLAG_KEY, true)

        if(makeInitialTransaction) {
            makeTransaction()
            toggleTransaction(false)
        }




        Log.d("sharedpref", "Does contain: $doesContain")


//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)
    }

    private fun makeTransaction() {
        TODO("Not yet implemented")
    }

    private fun toggleTransaction(makeNextTransaction: Boolean) {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(TRANSACTION_FLAG_KEY, makeNextTransaction)
            apply()
        }
    }

}
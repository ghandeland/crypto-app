package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DisplayCurrencyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_currency)

        val currencyCode = intent.getStringExtra(EXTRA_MESSAGE)
    }
}
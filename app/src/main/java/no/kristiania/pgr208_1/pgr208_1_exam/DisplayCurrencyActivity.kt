package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivityDisplayCurrencyBinding

class DisplayCurrencyActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityDisplayCurrencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()

        // TODO: Try/Catch and return to parent activity (Note)
        // Retrieve currency ID from intent and fetch fresh data with it
        val currencyId = intent.getStringExtra(EXTRA_CURRENCY_ID)
        viewModel.fetchSingleAsset(currencyId!!)


        // Retrieve currency symbol from intent and load currency logo with Glide
        val currencySymbol = intent.getStringExtra(EXTRA_CURRENCY_SYMBOL)
        Glide
                .with(this)
                .load("https://static.coincap.io/assets/icons/${currencySymbol}@2x.png")
                .into(binding.ivLogo)

    }

    private fun initObservers() {
        viewModel.currency.observe(this) { currency ->
            binding.tvCurrencyTitle.text = "${currency.name} [${currency.symbol.toUpperCase()}]"
            binding.tvCurrencyPrice.text = "Current price: ${currency.priceUsd} \$"
            // TODO: Database call to check if currency is owned + Parse and format price correctly
        }
    }
}
package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivityDisplayCurrencyBinding
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivityMainBinding

class DisplayCurrencyActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityDisplayCurrencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayCurrencyBinding.inflate(layoutInflater)
        initObservers()




        // TODO: Try/Catch and return to parent activity (Note)
        val currencyCode = intent.getStringExtra(EXTRA_MESSAGE)
        viewModel.fetchSingleAssets(currencyCode!!)


    }

    private fun initObservers() {
        viewModel.currency.observe(this) { currency ->
            binding.tvCurrencyTitle.text = currency.name
            binding.tvCurrencyPrice.text = currency.priceUsd
            // TODO: Database call to check if currency is owned + picture
        }
    }
}
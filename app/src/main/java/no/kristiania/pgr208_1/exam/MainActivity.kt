package no.kristiania.pgr208_1.exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import no.kristiania.pgr208_1.exam.databinding.ActivityMainBinding
import no.kristiania.pgr208_1.exam.ui.CurrencyAdapter

const val EXTRA_CURRENCY_ID = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_ID"
const val EXTRA_CURRENCY_SYMBOL = "no.kristiania.pgr208_1.pgr208_1_exam.CURRENCY_SYMBOL"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    // Call adapter with onClick lambda function
    private val adapter = CurrencyAdapter { currency ->
        Intent(this, DisplayCurrencyActivity::class.java).apply {
            putExtra(EXTRA_CURRENCY_ID, currency.id.toLowerCase())
            putExtra(EXTRA_CURRENCY_SYMBOL, currency.symbol.toLowerCase())
            startActivity(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.init(this)
        initObservers()
        setOnclickListeners()
        viewModel.fetchCurrencies()
        viewModel.fetchUsdBalance()


        // Using the Recyclerview with LinearLayoutManager produced a bug where the individual item width did not fill the parent, therefore it is replaced here with GridLayoutManager
        // https://stackoverflow.com/questions/35904409/item-in-recyclerview-not-filling-its-width-match-parent
        // binding.rvCurrencies.layoutManager = LinearLayoutManager(this)
        binding.rvCurrencies.adapter = adapter
        binding.rvCurrencies.layoutManager = GridLayoutManager(this, 1)
    }

    private fun setOnclickListeners() {
        binding.llBalanceDisplay.setOnClickListener {
            Intent(this, PortfolioActivity::class.java).apply { startActivity(this) }
        }
    }

    private fun initObservers() {
        viewModel.currencies.observe(this) { currencies ->
            adapter.setCurrencyList(currencies)
        }

        viewModel.usdBalance.observe(this) { balance ->
            binding.tvBalance.text = "Points: $balance $"
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCurrencies()
    }


}
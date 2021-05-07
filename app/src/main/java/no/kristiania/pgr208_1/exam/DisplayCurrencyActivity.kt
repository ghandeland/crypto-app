package no.kristiania.pgr208_1.exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.exam.databinding.ActivityDisplayCurrencyBinding
import no.kristiania.pgr208_1.exam.ui.BuyFragment
import no.kristiania.pgr208_1.exam.ui.SellFragment

class DisplayCurrencyActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityDisplayCurrencyBinding
    private lateinit var currencyId: String
    private lateinit var currencySymbol: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnclickListeners()
        initObservers()
        // Get extras to retrieve currency info from Viewmodel
        currencyId = intent.getStringExtra(EXTRA_CURRENCY_ID)!!
        currencySymbol = intent.getStringExtra(EXTRA_CURRENCY_SYMBOL)!!

        viewModel.init(this)
        viewModel.fetchUsdBalance()

        // Retrieve currency ID from intent and fetch fresh data with it
        viewModel.setCurrentCurrency(currencyId)

        // Load currency logo with Glide
        Glide
                .with(this)
                .load("https://static.coincap.io/assets/icons/${currencySymbol}@2x.png")
                .into(binding.ivLogo)
    }

    private fun setOnclickListeners() {
        binding.btnBuy.setOnClickListener {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, BuyFragment.newInstance(viewModel.currentCurrency.value!!.id))
                    .addToBackStack(null)
                    .commit()
        }

        binding.btnSell.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, SellFragment.newInstance(viewModel.currentCurrency.value!!.id))
                .addToBackStack(null)
                .commit()
        }

    }

    private fun initObservers() {

        viewModel.currentCurrency.observe(this) { currency ->
            binding.tvCurrencyTitle.text = "${currency.name} [${currency.symbol.toUpperCase()}]"
            binding.tvCurrencyPrice.text = "Current price: ${currency.priceUsd} \$"
        }

        viewModel.usdBalance.observe(this) { usd ->
            binding.btnBuy.isEnabled = usd > 0.0

            binding.tvUsdBalance.text = "USD balance: $usd $"
        }

        viewModel.currentCurrencyBalance.observe(this) { balance ->
            if(balance.currencyId == NOT_INSERTED) {
                binding.tvCurrencyOwned.visibility = View.GONE
                binding.btnSell.isEnabled = false
            } else {
                binding.tvCurrencyOwned.visibility = View.VISIBLE
                binding.tvCurrencyOwned.text = "You currently own: ${balance.amount} (${viewModel.convertCurrentCurrencyToUsd(balance.amount)} $)"
                binding.btnSell.isEnabled = true
            }
        }

    }

    override fun onBackPressed() {
        initObservers()
        viewModel.fetchUsdBalance()
        viewModel.setCurrentCurrencyBalance(currencyId)
        super.onBackPressed()
    }

    fun sell(currencyAmount: Double) {
        viewModel.makeTransactionSell(currencyAmount)
    }

    fun buy(usdAmount: Double) {
        viewModel.makeTransactionBuy(usdAmount)
    }

}
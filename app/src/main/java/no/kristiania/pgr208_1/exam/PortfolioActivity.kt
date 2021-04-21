package no.kristiania.pgr208_1.exam

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import no.kristiania.pgr208_1.exam.databinding.ActivityPortfolioBinding
import no.kristiania.pgr208_1.exam.ui.PortfolioAdapter

class PortfolioActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityPortfolioBinding
    private val adapter = PortfolioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()
        viewModel.init(this)
        viewModel.fetchTotalBalanceInUsd()
        viewModel.fetchPortfolio()

        binding.rvPortfolio.adapter = adapter
        // See MainActivity RV for explanation
        binding.rvPortfolio.layoutManager = GridLayoutManager(this, 1)
    }

    private fun initObservers() {
        viewModel.totalBalanceInUsd.observe(this) { balance ->
            binding.tvBalance.text = "Points: $balance $"
        }

        viewModel.completeCurrencies.observe(this) { portfolioBalances ->
            adapter.setPortfolioBalances(portfolioBalances)
        }

    }
}
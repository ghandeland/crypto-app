package no.kristiania.pgr208_1.exam

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        viewModel.fetchAssets()
        viewModel.fetchUsdBalance()
        viewModel.fetchPortfolio()
        // TODO: Get two lists from viewModel with Balances and equivalent Currency objects


        binding.rvPortfolio.adapter = adapter
        // See MainActivity RV for explanation
        binding.rvPortfolio.layoutManager = GridLayoutManager(this, 1)
    }

    private fun initObservers() {
        viewModel.usdBalance.observe(this) { balance ->
            binding.tvBalance.text = "Points: $balance $"
        }

        viewModel.portfolioList.observe(this) { portfolioList ->
            adapter.setPortfolioList(portfolioList)
        }

        viewModel.currencies.observe(this) { portfolioList ->
            adapter.setPortfolioCurrencies(portfolioList)
        }
    }
}
package no.kristiania.pgr208_1.exam

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import no.kristiania.pgr208_1.exam.databinding.ActivityPortfolioBinding

class PortfolioActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityPortfolioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.init(this)
        initObservers()


    }

    private fun initObservers() {
        viewModel.usdBalance.observe(this) { balance ->
            binding.tvBalance.text = "Points: $balance $"
        }
    }
}
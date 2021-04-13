package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivityMainBinding
import no.kristiania.pgr208_1.pgr208_1_exam.ui.CurrencyAdapter

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val adapter = CurrencyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.reload()

        viewModel.currencies.observe(this) { currencies ->
            adapter.setCurrencyList(currencies)
        }


        binding.rvCurrencies.adapter = adapter
        binding.rvCurrencies.layoutManager = LinearLayoutManager(this)
    }


}
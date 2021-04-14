package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
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

        viewModel.currencies.observe(this) { currencies ->
            adapter.setCurrencyList(currencies)
        }

        // Using the Recyclerview with LinearLayoutManager produced a bug where the individual item width did not fill the parent, therefore it is replaced here with GridLayoutManager
        // https://stackoverflow.com/questions/35904409/item-in-recyclerview-not-filling-its-width-match-parent
        // binding.rvCurrencies.layoutManager = LinearLayoutManager(this)

        binding.rvCurrencies.adapter = adapter
        binding.rvCurrencies.layoutManager = GridLayoutManager(this, 1)
    }

    override fun onResume() {
        super.onResume()
        viewModel.reload()
    }


}
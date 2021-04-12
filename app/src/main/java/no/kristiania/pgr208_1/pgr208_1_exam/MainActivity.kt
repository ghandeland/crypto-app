package no.kristiania.pgr208_1.pgr208_1_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()

    }

    private fun initObservers() {
        viewModel.assets.observe(this) { assets ->
            binding.tv1.text = assets[0].name
        }
    }
}
package no.kristiania.pgr208_1.exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import no.kristiania.pgr208_1.exam.databinding.ActivityTransactionsBinding
import no.kristiania.pgr208_1.exam.ui.TransactionAdapter

class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = TransactionAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()
        viewModel.init(this)
        viewModel.fetchTransactions()

        binding.rvTransactions.adapter = adapter
        binding.rvTransactions.layoutManager = GridLayoutManager(this, 1)
    }

    private fun initObservers() {
        viewModel.transactions.observe(this) { transactions ->
            adapter.setTransactionList(transactions)
        }
    }
}
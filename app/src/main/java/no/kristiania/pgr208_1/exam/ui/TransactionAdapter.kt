package no.kristiania.pgr208_1.exam.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.exam.R
import no.kristiania.pgr208_1.exam.TRANSACTION_INITIAL
import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyTransaction
import no.kristiania.pgr208_1.exam.databinding.ItemCurrencyBinding
import no.kristiania.pgr208_1.exam.databinding.ItemTransactionBinding
import no.kristiania.pgr208_1.exam.round
import java.lang.Double.parseDouble
import java.math.BigDecimal

import java.math.RoundingMode


// Adapter for listing the currency objects with Recyclerview
class TransactionAdapter() : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {


    private val transactions = mutableListOf<CurrencyTransaction>()

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding,
        private val context: Context): RecyclerView.ViewHolder(binding.root) {


        fun bind(transaction: CurrencyTransaction) {
            binding.apply {
                // Check for installation reward for custom display
                if(transaction.currencySymbol ==  TRANSACTION_INITIAL) {
                    ivLogo.setImageResource(R.drawable.ic_money_emoji)
                    tvTransactionType.text = "INSTALLATION REWARD"
                    tvTransactionType.setTextColor(Color.parseColor("#fc03c6"))
                    tvTypeAndPrice.text = "${transaction.usdAmount} $"
                    return
                }



                // Load image thumbnail with glide
                Glide
                        .with(context)
                        .load("https://static.coincap.io/assets/icons/${transaction.currencySymbol.toLowerCase()}@2x.png")
                        .into(ivLogo)

                if(transaction.isBuy) {
                    tvTransactionType.text = "BOUGHT"
                    tvTransactionType.setTextColor(Color.parseColor("#00a8a5"))
                } else {
                    tvTransactionType.text = "SOLD"
                    tvTransactionType.setTextColor(Color.parseColor("#6d00a8"))
                }
                tvTypeAndPrice.text = "${transaction.currencyAmount} ${transaction.currencySymbol.toUpperCase()} for ${transaction.usdAmount} $"
                tvDate.text = "${transaction.transactionDate}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.TransactionViewHolder {
        return TransactionViewHolder(
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: TransactionAdapter.TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setTransactionList(list: List<CurrencyTransaction>) {
        transactions.clear()
        transactions.addAll(list)
        notifyDataSetChanged()
    }


}
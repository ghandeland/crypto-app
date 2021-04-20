package no.kristiania.pgr208_1.exam.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.exam.data.CurrencyComplete
import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance
import no.kristiania.pgr208_1.exam.databinding.ItemPortfolioBinding
import java.util.*

class PortfolioAdapter() : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>(){

    private val portfolioBalances = mutableListOf<CurrencyComplete>()



    inner class PortfolioViewHolder (
        private val binding: ItemPortfolioBinding,
        private val context: Context):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: CurrencyComplete) {
            binding.apply {
                // Load image thumbnail with glide
                Glide
                    .with(context)
                    .load("https://static.coincap.io/assets/icons/${currency.id}@2x.png")
                    .into(ivLogo)

                tvName.text = "${currency.name} "
                tvAmountAndPrice.text = "${currency.balance} x ${currency.priceUsd} $"
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        return PortfolioViewHolder(
            ItemPortfolioBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.bind(portfolioBalances[position])
    }

    override fun getItemCount(): Int {
        return portfolioBalances.size
    }

    fun setPortfolioBalances(list: List<CurrencyComplete>) {
        portfolioBalances.clear()
        portfolioBalances.addAll(list)
        notifyDataSetChanged()
    }
}
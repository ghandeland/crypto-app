package no.kristiania.pgr208_1.exam.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance
import no.kristiania.pgr208_1.exam.databinding.ItemPortfolioBinding

class PortfolioAdapter() : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>(){

    private val portfolioBalances = mutableListOf<CurrencyBalance>()
    private val portfolioCurrencies = mutableListOf<CryptoCurrency>()


    inner class PortfolioViewHolder (
        private val binding: ItemPortfolioBinding,
        private val context: Context):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(balance: CurrencyBalance, currency: CryptoCurrency) {
            binding.apply {
                // Load image thumbnail with glide

                tvName.text = "id from balance ${balance.currencyId} "
                tvAmountAndPrice.text = "name from currency ${currency.name}"
//                Glide
//                    .with(context)
//                    .load("https://static.coincap.io/assets/icons/${""}@2x.png")
//                    .into(ivLogo)
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
        holder.bind(portfolioBalances[position], portfolioCurrencies[position])
    }

    override fun getItemCount(): Int {
        return portfolioBalances.size
    }

    fun setPortfolioList(list: List<CurrencyBalance>) {
        portfolioBalances.clear()
        portfolioBalances.addAll(list)
        notifyDataSetChanged()
    }

    fun setPortfolioCurrencies(list: List<CryptoCurrency>) {
        portfolioCurrencies.clear()
        portfolioCurrencies.addAll(list)
    }
}
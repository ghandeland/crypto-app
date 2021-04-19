package no.kristiania.pgr208_1.exam.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance
import no.kristiania.pgr208_1.exam.databinding.ItemPortfolioBinding

class PortfolioAdapter() : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>(){

    private val portfolioBalances = mutableListOf<CurrencyBalance>()

    inner class PortfolioViewHolder (
        private val binding: ItemPortfolioBinding,
        private val context: Context):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currencyBalance: CurrencyBalance) {

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

    fun setPortfolioList(list: List<CurrencyBalance>) {
        portfolioBalances.clear()
        portfolioBalances.addAll(list)
        notifyDataSetChanged()
    }
}
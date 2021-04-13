package no.kristiania.pgr208_1.pgr208_1_exam.ui

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208_1.pgr208_1_exam.R
import no.kristiania.pgr208_1.pgr208_1_exam.data.domain.CryptoCurrency
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ItemCurrencyBinding


// Adapter for listing the currency objects with Recyclerview
class CurrencyAdapter() :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val currencies = mutableListOf<CryptoCurrency>()

    inner class CurrencyViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: CryptoCurrency) {
            binding.tvName.text = currency.name
            binding.tvSymbol.text = currency.symbol
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    fun setCurrencyList(list: List<CryptoCurrency>) {
        currencies.clear()
        currencies.addAll(list)
        notifyDataSetChanged()
    }


}
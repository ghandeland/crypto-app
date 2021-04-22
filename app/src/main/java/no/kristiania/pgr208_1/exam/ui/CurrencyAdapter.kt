package no.kristiania.pgr208_1.exam.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.exam.R
import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.databinding.ItemCurrencyBinding
import no.kristiania.pgr208_1.exam.round
import java.lang.Double.parseDouble
import java.math.BigDecimal

import java.math.RoundingMode


// Adapter for listing the currency objects with Recyclerview
class CurrencyAdapter(private val onItemClicked: (CryptoCurrency) -> Unit) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {


    private val currencies = mutableListOf<CryptoCurrency>()

    inner class CurrencyViewHolder(
        private val binding: ItemCurrencyBinding,
        private val context: Context,
        private val onItemClicked: (Int) -> Unit
        ): RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClicked(absoluteAdapterPosition)
            }
        }

        fun bind(currency: CryptoCurrency) {
            binding.apply {
                // Load image thumbnail with glide
                Glide
                        .with(context)
                        .load("https://static.coincap.io/assets/icons/${currency.symbol.toLowerCase()}@2x.png")
                        .into(ivLogo)

                tvName.text = currency.name
                tvSymbol.text = currency.symbol
                tvPrice.text = currency.priceUsd

                // Parse the large percentage string to a double, then round it down correctly using java.math.BigDecimal
                val percentage =  round(currency.changePercent24Hr.toDouble(), 2)
                tvPercentage.text = "$percentage%"
                // Conditional styling based on percentage (color and arrow icon)
                when {
                    percentage > 0 -> {
                        tvPercentage.setTextColor(Color.parseColor("#1ebf06"))
                        ivArrow.setImageResource(R.drawable.ic_arrow_gr)
                        ivArrow.visibility = View.VISIBLE
                    }
                    percentage < 0 -> {
                        tvPercentage.setTextColor(Color.parseColor("#cc0700"))
                        ivArrow.setImageResource(R.drawable.ic_arrow_re)
                        ivArrow.visibility = View.VISIBLE
                    }
                    else -> {
                        tvPercentage.setTextColor(Color.parseColor("#FFFFFF"))
                        ivArrow.visibility = View.INVISIBLE
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        ) { adapterPosition ->
            onItemClicked(currencies[adapterPosition])
        }
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
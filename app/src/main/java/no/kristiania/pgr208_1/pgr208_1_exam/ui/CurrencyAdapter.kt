package no.kristiania.pgr208_1.pgr208_1_exam.ui

import android.content.ClipData
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208_1.pgr208_1_exam.R
import no.kristiania.pgr208_1.pgr208_1_exam.data.domain.CryptoCurrency
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ItemCurrencyBinding
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat
import java.math.BigDecimal
import java.math.RoundingMode


// Adapter for listing the currency objects with Recyclerview
class CurrencyAdapter() :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val currencies = mutableListOf<CryptoCurrency>()

    inner class CurrencyViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: CryptoCurrency) {
            binding.tvName.text = currency.name
            binding.tvSymbol.text = currency.symbol



            // Parse the large percentage string to a double, then round it down correctly using java.math.BigDecimal
            val percentage =  parseDouble(currency.changePercent24Hr)
            val decimal = BigDecimal(percentage).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            binding.tvPercentage.text = "$decimal%"
            when {
                decimal > 0 -> {
                    binding.apply {
                        tvPercentage.setTextColor(Color.parseColor("#1ebf06"))
                        ivArrow.setImageResource(R.drawable.ic_arrow_gr)
                        ivArrow.visibility = View.VISIBLE
                    }

                }
                decimal < 0 -> {
                    binding.apply {
                        tvPercentage.setTextColor(Color.parseColor("#cc0700"))
                        ivArrow.setImageResource(R.drawable.ic_arrow_re)
                        ivArrow.visibility = View.VISIBLE
                    }

                }
                else -> {
                    binding.tvPercentage.setTextColor(Color.parseColor("#000000"))
                    binding.ivArrow.visibility = View.INVISIBLE
                }
            }
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
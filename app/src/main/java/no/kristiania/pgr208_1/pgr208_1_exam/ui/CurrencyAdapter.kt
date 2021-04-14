package no.kristiania.pgr208_1.pgr208_1_exam.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208_1.pgr208_1_exam.R
import no.kristiania.pgr208_1.pgr208_1_exam.data.domain.CryptoCurrency
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.ItemCurrencyBinding
import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode


// Adapter for listing the currency objects with Recyclerview
class CurrencyAdapter() :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val currencies = mutableListOf<CryptoCurrency>()

    inner class CurrencyViewHolder(val binding: ItemCurrencyBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        // TODO: Encapsulate the function statements with a scope function?
        fun bind(currency: CryptoCurrency) {

            Glide
                    .with(context)
                    .load("https://static.coincap.io/assets/icons/${currency.symbol.toLowerCase()}@2x.png")
                    .into(binding.ivLogo)


            binding.tvName.text = currency.name
            binding.tvSymbol.text = currency.symbol

            // TODO: Parse price and list amount of decimal numbers accordingly
            val price = parseDouble(currency.priceUsd)
            val priceRounded = BigDecimal(price).setScale(2, RoundingMode.HALF_EVEN)
            binding.tvPrice.text = "$priceRounded$"


            // Parse the large percentage string to a double, then round it down correctly using java.math.BigDecimal
            val percentage =  parseDouble(currency.changePercent24Hr)
            val percentageRounded = BigDecimal(percentage).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            binding.tvPercentage.text = "$percentageRounded%"
            when {
                percentageRounded > 0 -> {
                    binding.apply {
                        tvPercentage.setTextColor(Color.parseColor("#1ebf06"))
                        ivArrow.setImageResource(R.drawable.ic_arrow_gr)
                        ivArrow.visibility = View.VISIBLE
                    }

                }
                percentageRounded < 0 -> {
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
        return CurrencyViewHolder(ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context)), parent.context)
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
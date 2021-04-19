package no.kristiania.pgr208_1.pgr208_1_exam.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import no.kristiania.pgr208_1.pgr208_1_exam.*
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.FragmentSellBinding
import java.lang.Double

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

private const val ARG_CURRENCY_ID = "currencySymbol"

class SellFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var currencyId: String? = null

    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencyId = it.getString(ARG_CURRENCY_ID)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewModel.init(requireContext())
        viewModel.setCurrentCurrency(currencyId!!)
        viewModel.fetchUsdBalance() // Not necessary?

        _binding = FragmentSellBinding.inflate(inflater, container, false)

        // Observe fetched currency data
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            binding.tvCurrencyLabel.text = currency.symbol

            // TODO: Database call to check if currency is owned + Parse and format price correctly
        }

        // onChangeListener to calculate crypto -> USD
        binding.etCurrency.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if input field is empty before parsing
                val currencyString = binding.etCurrency.text.toString()
                if (currencyString == "") {
                    binding.tvUsdCalculated.text = ""
                    return
                }

                // Not empty - Parse to double and check if number is negative or 0
                val currencyAmount = Double.parseDouble(binding.etCurrency.text.toString())
                if (currencyAmount <= 0.0) {
                    binding.tvUsdCalculated.text = ""
                    return
                }

                // Convert to currency amount with newly fetched currency (fetched in onCreate)
                val convertedToCurrency = viewModel.convertCurrentCurrencyToUsd(currencyAmount)
                binding.tvUsdCalculated.text = convertedToCurrency.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSell.setOnClickListener {
            sell()
        }

        binding.btnSellAll.setOnClickListener {
            sellAll()
        }

        return binding.root

    }

    private fun sellAll() {
        if(viewModel.currentCurrencyBalance.value!!.amount == 0.0) {
            showToast("Transaction error: You do not own any of this currency")
            return
        }

        viewModel.sellAllOfCurrentCurrency()

        val currentCurrency = viewModel.currentCurrency.value!!
        Intent(activity, DisplayCurrencyActivity::class.java).apply {
            putExtra(EXTRA_CURRENCY_ID, currentCurrency.id.toLowerCase())
            putExtra(EXTRA_CURRENCY_SYMBOL, currentCurrency.symbol.toLowerCase())
            startActivity(this)
        }
    }

    private fun sell() {
        // Check if input field is empty before parsing
        val usdString = binding.etCurrency.text.toString()
        if (usdString.isEmpty()) {
            binding.tvUsdCalculated.text = ""
            showToast("Transaction error: Currency input field is empty")
            return
        }

        // Not empty - Parse to double and check if number is negative or 0
        val currencyAmount = Double.parseDouble(binding.etCurrency.text.toString())
        if (currencyAmount <= 0.0) {
            binding.tvUsdCalculated.text = ""
            showToast("Transaction error: Currency sum cannot be 0 or negative")
            return
        } else if(currencyAmount > viewModel.currentCurrencyBalance.value!!.amount) {
            showToast("Transaction error: Given currency sum is bigger than your balance")
            return
        }

        viewModel.makeTransactionSell(currencyAmount)

        // Retrieve currency data and send it back to parent activity to restart
        val currentCurrency = viewModel.currentCurrency.value!!
        Intent(activity, DisplayCurrencyActivity::class.java).apply {
            putExtra(EXTRA_CURRENCY_ID, currentCurrency.id.toLowerCase())
            putExtra(EXTRA_CURRENCY_SYMBOL, currentCurrency.symbol.toLowerCase())
            startActivity(this)
        }
    }

    // Destroy binding, so that  the field only is valid between onCreateView and onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(currencyId: String) =
                SellFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_CURRENCY_ID, currencyId)
                    }
                }
        // BuyFragment()

    }

    private fun showToast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
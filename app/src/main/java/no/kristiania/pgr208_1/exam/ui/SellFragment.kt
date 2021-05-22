package no.kristiania.pgr208_1.exam.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import no.kristiania.pgr208_1.exam.*
import no.kristiania.pgr208_1.exam.databinding.FragmentSellBinding

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

        viewModel.init(requireContext())
        viewModel.setCurrentCurrency(currencyId!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSellBinding.inflate(inflater, container, false)

        // Observe fetched currency data
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            binding.tvCurrencyLabel.text = currency.symbol

            // TODO: Database call to check if currency is owned + Parse and format price correctly
        }

        // Limit decimal amounts
        binding.etCurrency.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(4))


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
                val currencyAmount = binding.etCurrency.text.toString().toDouble()
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

        binding.btnInsertAll.setOnClickListener {
            binding.etCurrency.setText(viewModel.currentCurrencyBalance.value!!.amount.toString())
        }

        return binding.root

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
        val currencyAmount = binding.etCurrency.text.toString().toDouble()
        if (currencyAmount <= 0.0) {
            binding.tvUsdCalculated.text = ""
            showToast("Transaction error: Currency sum cannot be 0 or negative")
            return
        } else if(currencyAmount > viewModel.currentCurrencyBalance.value!!.amount) {
            showToast("Transaction error: Given currency sum is bigger than your balance")
            return
        }

        // Sell method in activity because fragment is destroyed before Viewmodel method is called
        (activity as DisplayCurrencyActivity).sell(currencyAmount);
        requireActivity().onBackPressed()
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
    }

    private fun showToast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
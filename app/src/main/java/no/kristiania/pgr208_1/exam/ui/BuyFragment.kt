package no.kristiania.pgr208_1.exam.ui

import android.content.Intent
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
import no.kristiania.pgr208_1.exam.databinding.FragmentBuyBinding
import java.lang.Double.parseDouble


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CURRENCY_ID = "currencySymbol"

class BuyFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var currencyId: String? = null

    private var _binding: FragmentBuyBinding? = null
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
        viewModel.fetchUsdBalance()

        _binding = FragmentBuyBinding.inflate(inflater, container, false)

        // Observe fetched currency data
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            binding.tvCurrencyLabel.text = currency.symbol
        }

        binding.etUSD.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))

        // onChangeListener to calculate USD -> crypto
        binding.etUSD.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if input field is empty before parsing
                val usdString = binding.etUSD.text.toString()
                if (usdString == "") {
                    binding.tvCurrencyCalculated.text = ""
                    return
                }

                // Not empty - Parse to double and check if number is negative or 0
                val usdAmount = parseDouble(binding.etUSD.text.toString())
                if (usdAmount <= 0.0) {
                    binding.tvCurrencyCalculated.text = ""
                    return
                }

                // Convert to currency amount with newly fetched currency (fetched in onCreate)
                val convertedToCurrency = viewModel.convertUsdToCurrentCurrency(usdAmount)
                binding.tvCurrencyCalculated.text = convertedToCurrency.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnBuy.setOnClickListener {
            buy()
        }


        return binding.root

    }

    private fun buy() {
        // Check if input field is empty before parsing
        val usdString = binding.etUSD.text.toString()
        if (usdString.isEmpty()) {
            binding.tvCurrencyCalculated.text = ""
            showToast("Transaction error: USD input field is empty")
            return
        }

        // Not empty - Parse to double and check if number is negative or 0
        val usdAmount = parseDouble(binding.etUSD.text.toString())
        if (usdAmount <= 0.0) {
            binding.tvCurrencyCalculated.text = ""
            showToast("Transaction error: USD sum cannot be 0 or negative")
            return
        } else if(usdAmount > viewModel.usdBalance.value!!) {
            showToast("Transaction error: Insufficient balance")
            return
        }



        // Retrieve currency data and send it back to parent activity to restart
//        val currentCurrency = viewModel.currentCurrency.value!!
//        Intent(activity, DisplayCurrencyActivity::class.java).apply {
//            putExtra(EXTRA_CURRENCY_ID, currentCurrency.id.toLowerCase())
//            putExtra(EXTRA_CURRENCY_SYMBOL, currentCurrency.symbol.toLowerCase())
//            startActivity(this)
//        }

        // Buy method in activity because fragment is destroyed before Viewmodel method is called
        (activity as DisplayCurrencyActivity).buy(usdAmount);
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
                BuyFragment().apply {
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
package no.kristiania.pgr208_1.pgr208_1_exam.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import no.kristiania.pgr208_1.pgr208_1_exam.MainViewModel
import no.kristiania.pgr208_1.pgr208_1_exam.R
import no.kristiania.pgr208_1.pgr208_1_exam.databinding.FragmentBuyBinding
import java.lang.Double.parseDouble

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CURRENT_PRICE = "currencyPrice"
private const val ARG_CURRENCY_SYMBOL = "currencySymbol"

class BuyFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentBuyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        arguments?.let {
//            currencyPrice = it.getDouble(ARG_CURRENT_PRICE)
//            currencySymbol = it.getString(ARG_CURRENCY_SYMBOL)
//        }


    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyBinding.inflate(inflater, container, false)
        //Log.d("displayc", "onCreateView")
        // https://stackoverflow.com/questions/6188584/viewmodel-and-singleton-pattern#:~:text=No.,the%20run%20of%20the%20application.&text=In%20MVVM%2C%20the%20lifespan%20of,open%20and%20finishes%20their%20changes.
        //
        binding.tvCurrencyLabel.text = "Testcoin"
        return binding.root

    }

    // Destroy binding, so that  the field only is valid between onCreateView and onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
//                BuyFragment().apply {
//                    arguments = Bundle().apply {
//                        putDouble(ARG_CURRENT_PRICE, currentPrice)
//                        putString(ARG_CURRENCY_SYMBOL, symbol)
//                    }
//                }
            BuyFragment()

    }
}
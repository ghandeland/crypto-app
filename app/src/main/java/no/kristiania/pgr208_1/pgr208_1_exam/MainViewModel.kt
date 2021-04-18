package no.kristiania.pgr208_1.pgr208_1_exam

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.pgr208_1.pgr208_1_exam.data.api.API
import no.kristiania.pgr208_1.pgr208_1_exam.data.api.CoinCapService
import no.kristiania.pgr208_1.pgr208_1_exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.AppDatabase
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.WalletCurrencyDao
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.BalanceTransactionDao
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.WalletCurrency
import java.lang.Double.parseDouble

import java.lang.Exception

class MainViewModel : ViewModel() {
    private val coinCapService: CoinCapService = API.coinCapService
    private lateinit var transactionDao: BalanceTransactionDao
    private lateinit var walletCurrencyDao: WalletCurrencyDao

    private val _currencies = MutableLiveData<List<CryptoCurrency>>()
    val currencies: LiveData<List<CryptoCurrency>> get() = _currencies

    private val _currentCurrency = MutableLiveData<CryptoCurrency>()
    val currentCurrency: LiveData<CryptoCurrency> get() = _currentCurrency

    // Todo: Error handling
    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> get() = _error

    private val _usdBalance = MutableLiveData<Double>()
    val usdBalance: LiveData<Double> get() = _usdBalance

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _error.postValue(Unit)
    }

    fun init(context: Context) {
        transactionDao = AppDatabase.getDatabase(context).balanceTransactionDao() // Retrieve transactions from DB
        walletCurrencyDao = AppDatabase.getDatabase(context).walletCurrencyDao() // Retrieve transactions from DB
    }

    fun fetchAssets() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyList = coinCapService.getAssets()
            _currencies.postValue(currencyList.data)
        }
    }

    fun fetchSingleAsset(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyFetch = coinCapService.getAsset(currencyId)
            _currentCurrency.postValue(currencyFetch.currency)
        }
    }

    fun convertCurrentUsdToCurrency(usdAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return usdAmount / currencyPrice
    }

    fun makeInitialDeposit() {
        viewModelScope.launch {
            try {

                walletCurrencyDao.insert(WalletCurrency(currencyCode = "usd", amount = 10_000.0))
                val usdBalance = walletCurrencyDao.getCurrency("usd")
                _usdBalance.postValue(usdBalance.amount);
                Log.d("db", usdBalance.amount.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("db", "catchInit")
            } finally {
                Log.d("db", "finallyInit")
            }
        }
    }

    // TODO: MAKE NEW METHOD: Convert to calculate sum of all currencies to USD
    fun fetchUsdBalance() {
        viewModelScope.launch {
            try {
                val usdBalance = walletCurrencyDao.getCurrency("usd")
                _usdBalance.postValue(usdBalance.amount);
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun makeTransactionBuy() {

    }

}
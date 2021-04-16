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

import java.lang.Exception

class MainViewModel : ViewModel() {
    private val coinCapService: CoinCapService = API.coinCapService
    private lateinit var walletCurrencyDao: WalletCurrencyDao
    private lateinit var transactionDao: BalanceTransactionDao

    private val _currencies = MutableLiveData<List<CryptoCurrency>>()
    val currencies: LiveData<List<CryptoCurrency>> get() = _currencies

    private val _currency = MutableLiveData<CryptoCurrency>()
    val currency: LiveData<CryptoCurrency> get() = _currency

    // Todo: Error handling
    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> get() = _error

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _error.postValue(Unit)
    }

    fun init(context: Context) {

        walletCurrencyDao = AppDatabase.getDatabase(context).balanceDao() // Retrieve balance from DB
        transactionDao = AppDatabase.getDatabase(context).balanceTransactionDao() // Retrieve transactions from DB
        fetchAssets() // Fetch currency data from API
    }

    fun reload() {
        //fetchAssets()
    }

    private fun fetchAssets() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyList = coinCapService.getAssets()
            _currencies.postValue(currencyList.data)
        }
    }

    fun fetchSingleAssets(currencyCode: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currency = coinCapService.getAsset(currencyCode)
            _currency.postValue(currency)
        }
    }

    fun makeInitialDeposit() {
        viewModelScope.launch {
            try {

                walletCurrencyDao.insert(WalletCurrency(currencyCode = "usd", amount = 10_000.0))
                val usdBalance = walletCurrencyDao.getCurrency("usd")
                _balance.postValue(usdBalance.amount);
                Log.d("db", usdBalance.amount.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("db", "catchInit")
            } finally {
                Log.d("db", "finallyInit")
            }
        }
    }

    fun calculateBalanceInUsd() {
        viewModelScope.launch {
            try {
                val usdBalance = walletCurrencyDao.getCurrency("usd")
                _balance.postValue(usdBalance.amount);
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

}
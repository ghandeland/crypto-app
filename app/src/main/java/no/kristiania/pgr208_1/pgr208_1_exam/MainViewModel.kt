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
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.CurrencyBalanceDao
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.CurrencyTransactionDao
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.CurrencyTransaction
import no.kristiania.pgr208_1.pgr208_1_exam.data.db.entity.CurrencyBalance
import java.lang.Double.parseDouble

import java.lang.Exception

private const val NOT_INSERTED = "rownotinsertedearlier"

class MainViewModel : ViewModel() {
    private val coinCapService: CoinCapService = API.coinCapService
    private lateinit var transactionDao: CurrencyTransactionDao
    private lateinit var balanceDao: CurrencyBalanceDao

    private val _currencies = MutableLiveData<List<CryptoCurrency>>()
    val currencies: LiveData<List<CryptoCurrency>> get() = _currencies

    private val _currentCurrency = MutableLiveData<CryptoCurrency>()
    val currentCurrency: LiveData<CryptoCurrency> get() = _currentCurrency

    private val _currentCurrencyBalance = MutableLiveData<CurrencyBalance>()
    val currentCurrencyBalance: LiveData<CurrencyBalance> get() = _currentCurrencyBalance

    private val _usdBalance = MutableLiveData<Double>()
    val usdBalance: LiveData<Double> get() = _usdBalance

    // Todo: Error handling
    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> get() = _error

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _error.postValue(Unit)
    }

    fun init(context: Context) {
        // Init data access objects
        transactionDao = AppDatabase.getDatabase(context).currencyTransactionDao()
        balanceDao = AppDatabase.getDatabase(context).currencyBalanceDao()
    }

    fun fetchAssets() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyList = coinCapService.getAssets()
            _currencies.postValue(currencyList.data)
        }
    }

    fun setCurrentCurrency(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyFetch = coinCapService.getAsset(currencyId)
            _currentCurrency.postValue(currencyFetch.currency)
            setCurrentCurrencyBalance(currencyFetch.currency.id)
        }
    }

    fun convertCurrentUsdToCurrency(usdAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return usdAmount / currencyPrice
    }

    fun makeInitialDeposit() {
        viewModelScope.launch {
            try {

                balanceDao.insert(CurrencyBalance(currencyId = "usd", amount = 10_000.0))
                val usdBalance = balanceDao.getCurrency("usd")
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

    fun makeTransactionBuy(usdAmount: Double) {
        viewModelScope.launch {
            try {
                val currency = currentCurrency.value!!
                val currencyPrice = parseDouble(currency.priceUsd)
                val currencyAmount = usdAmount / currencyPrice


                // Insert into transaction table
                transactionDao.insert(CurrencyTransaction(
                        currencyId = currency.id,
                        currencyAmount = currencyAmount,
                        currencyPrice = currencyPrice,
                        usdAmount = usdAmount,
                        isBuy = true))


                insertBalance()


            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    // TODO: MAKE NEW METHOD: Convert to calculate sum of all currencies to USD
    fun fetchUsdBalance() {
        viewModelScope.launch {
            try {
                val usdBalance = balanceDao.getCurrency("usd")
                _usdBalance.postValue(usdBalance.amount);
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun setCurrentCurrencyBalance(currencyId: String) {
        viewModelScope.launch {
            try {
                val balance = balanceDao.getCurrency(currencyId)

                if(balance == null) {
                    _currentCurrencyBalance.postValue(CurrencyBalance(currencyId = NOT_INSERTED, amount = 0.0))
                } else {
                    _currentCurrencyBalance.postValue(balance)
                }

                //_currentCurrencyBalance.postValue(balance)
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

    // Insert or update into CurrencyBalance table
    fun insertBalance(amount: Double, currencyId: String) {
        viewModelScope.launch {
            try {
                // Fetch and set currency balance to liveData
                // TODO: Make this universal: Should be able to update/insert balance, negative and positive with every currency

                val currentBalance = currentCurrencyBalance.value!!
                if(currentBalance.currencyId == NOT_INSERTED) {
                    balanceDao.insert(CurrencyBalance(currencyId = currentCurrency.value!!.id, amount))
                } else {
                    val newBalance = currentCurrencyBalance.value!!.amount + amount
                    balanceDao.update(CurrencyBalance(currencyId = currentBalance.currencyId, amount = newBalance))
                }
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }



}
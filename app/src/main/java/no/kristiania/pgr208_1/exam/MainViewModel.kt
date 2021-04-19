package no.kristiania.pgr208_1.exam

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.pgr208_1.exam.data.api.API
import no.kristiania.pgr208_1.exam.data.api.CoinCapService
import no.kristiania.pgr208_1.exam.data.api.domain.CryptoCurrency
import no.kristiania.pgr208_1.exam.data.db.AppDatabase
import no.kristiania.pgr208_1.exam.data.db.CurrencyBalanceDao
import no.kristiania.pgr208_1.exam.data.db.CurrencyTransactionDao
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyTransaction
import no.kristiania.pgr208_1.exam.data.db.entity.CurrencyBalance
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

    fun convertCurrentCurrencyToUsd(currencyAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return currencyAmount * currencyPrice
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
                transactionDao.insert(
                    CurrencyTransaction(
                        currencyId = currency.id,
                        currencyAmount = currencyAmount,
                        currencyPrice = currencyPrice,
                        usdAmount = usdAmount,
                        isBuy = true)
                )


                insertBalance(currencyId = currency.id, amount = currencyAmount)
                insertBalance(currencyId = "usd", amount = (-usdAmount))


            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    fun makeTransactionSell(currencyAmount: Double) {
        viewModelScope.launch {
            try {
                val currency = currentCurrency.value!!
                val currencyPrice = parseDouble(currency.priceUsd)
                val usdAmount = currencyAmount * currencyPrice


                // Insert into transaction table
                transactionDao.insert(
                    CurrencyTransaction(
                    currencyId = currency.id,
                    currencyAmount = currencyAmount,
                    currencyPrice = currencyPrice,
                    usdAmount = usdAmount,
                    isBuy = false)
                )


                insertBalance(currencyId = currency.id, amount = -currencyAmount)
                insertBalance(currencyId = "usd", amount = (usdAmount))


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
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

    // Insert or update into CurrencyBalance table
    fun insertBalance(currencyId: String, amount: Double) {
        viewModelScope.launch {
            try {
                val balance = balanceDao.getCurrency(currencyId)

                // Balance does not exist in DB
                if(balance == null) {
                    balanceDao.insert(CurrencyBalance(currencyId, amount))
                //  Update existing balance
                } else {
                    val newBalance = balance.amount + amount
                    balanceDao.update(CurrencyBalance(currencyId = currencyId, amount = newBalance))
                }
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    fun sellAllOfCurrentCurrency() {
        viewModelScope.launch {
            try {
                val balance = currentCurrencyBalance.value!!
                makeTransactionSell(balance.amount)
                balanceDao.delete(balance)

            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }



}
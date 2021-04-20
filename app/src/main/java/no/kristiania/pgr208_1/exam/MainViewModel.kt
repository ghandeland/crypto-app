package no.kristiania.pgr208_1.exam

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.kristiania.pgr208_1.exam.data.CurrencyComplete
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

    private val _completeCurrencies = MutableLiveData<List<CurrencyComplete>>()
    val completeCurrencies: LiveData<List<CurrencyComplete>> get() = _completeCurrencies

    private val _usdBalance = MutableLiveData<Double>()
    val usdBalance: LiveData<Double> get() = _usdBalance

    // Todo: Error handling
    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> get() = _error

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _error.postValue(Unit)
    }


    // Init data access objects
    fun init(context: Context) {
        transactionDao = AppDatabase.getDatabase(context).currencyTransactionDao()
        balanceDao = AppDatabase.getDatabase(context).currencyBalanceDao()
    }

    // Fetch all currencies from API
    fun fetchCurrencies(): Job {
        return viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyList = coinCapService.getAssets()
            _currencies.postValue(currencyList.data)
        }
    }

    // Fetch portfolio (all owned currencies from DB)
    fun fetchPortfolio() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val portfolio = balanceDao.getPortfolio()

            val currencyFetch = fetchCurrencies()
            currencyFetch.join()

            // Merge currency and balance into CompleteCurrency object
            val portfolioIds = portfolio.map { it.currencyId } // Get all portfolio IDs
            val completeCurrencies = currencies.value!!
                .filter { portfolioIds.contains(it.id) } // Filter currencies by portfolio IDs
                .map { c ->
                    CurrencyComplete(
                    id = c.id,
                    rank = c.rank,
                    symbol = c.symbol,
                    name = c.name,
                    priceUsd = c.priceUsd,
                    changePercent24Hr = c.changePercent24Hr,
                    // Get balance fom portfolio by corresponding ID
                    balance = portfolio.first { it.currencyId == c.id }.amount
                ) }.toMutableList()
            // Manually add USD as it is not fetched from USD
            completeCurrencies.add(0, CurrencyComplete(
                id = "usd",
                rank = "-",
                symbol = "usd",
                name = "US Dollars",
                priceUsd = "-",
                changePercent24Hr = "-",
                // Get balance fom portfolio by corresponding ID
                balance = portfolio.first { it.currencyId == "usd" }.amount
                )
            )
            _completeCurrencies.postValue(completeCurrencies)
        }
    }

    // Set current currency
    fun setCurrentCurrency(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currency = coinCapService.getAsset(currencyId).currency
            _currentCurrency.postValue(currency)
            setCurrentCurrencyBalance(currency.id)
        }
    }

    // If currency exists in balance table, set current balance
    private fun setCurrentCurrencyBalance(currencyId: String) {
        viewModelScope.launch {
            try {
                val balance = balanceDao.getCurrency(currencyId)

                if(balance == null) {
                    // TODO: Activity check for NOT_INSERTED
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


    // Buy usdAmount of current currency, persists transaction and new balance
    fun makeTransactionBuy(usdAmount: Double) {
        viewModelScope.launch {
            try {
                // Retrieve currency data
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

                // Persist balance
                insertBalance(currencyId = currency.id, amount = currencyAmount)
                insertBalance(currencyId = "usd", amount = (-usdAmount))
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    // Sell currencyAmount of current currency, persists transaction and new balance
    fun makeTransactionSell(currencyAmount: Double) {
        viewModelScope.launch {
            try {
                // Retrieve currency data

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

                // Persist balance
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

    // Insert or update into CurrencyBalance table
    private fun insertBalance(currencyId: String, amount: Double) {
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

    // Retrieve balance, sell and delete row from DB
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

    // Make initial deposit when opening app for the first time
    // Handled by shared preference flag in SplashActivity
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

    fun convertCurrentUsdToCurrency(usdAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return usdAmount / currencyPrice
    }

    fun convertCurrentCurrencyToUsd(currencyAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return currencyAmount * currencyPrice
    }

}

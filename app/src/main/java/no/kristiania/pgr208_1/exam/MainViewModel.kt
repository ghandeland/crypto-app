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
import java.time.LocalDateTime


import java.lang.Double.parseDouble
import java.lang.Exception
import java.time.LocalDate
import java.util.*




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

    private val _totalBalanceInUsd = MutableLiveData<Double>()
    val totalBalanceInUsd: LiveData<Double> get() = _totalBalanceInUsd

    private val _usdBalance = MutableLiveData<Double>()
    val usdBalance: LiveData<Double> get() = _usdBalance

    private val _transactions = MutableLiveData<List<CurrencyTransaction>>()
    val transactions: LiveData<List<CurrencyTransaction>> get() = _transactions

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
            // Loop through currencies and round decimal numbers with custom function
            for(currency in currencyList.data) {
                currency.priceUsd = round(currency.priceUsd, null)
            }
            _currencies.postValue(currencyList.data)
        }
    }

    // Fetch portfolio (all owned currencies from DB)
    fun fetchPortfolio(): Job {
        return viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchCurrencies().join() // Wait for API-fetch

            val portfolio = balanceDao.getPortfolio()
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
                    balance = portfolio.find { it.currencyId == c.id }!!.amount
                ) }.toMutableList()

            // Manually add USD as it is not fetched from API
            completeCurrencies.add(0, CurrencyComplete(
                            "usd",
                            "-",
                            "usd",
                            "US Dollars",
                            "1",
                            "-",
                            portfolio.find { it.currencyId == "usd" }!!.amount))
            _completeCurrencies.postValue(completeCurrencies)
        }
    }

    // Set current currency
    fun setCurrentCurrency(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            // Fetch currency, round decimal numbers and post to livedata
            val currency = coinCapService.getAsset(currencyId).currency
            currency.priceUsd = round(currency.priceUsd, null)
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
                    // Post to livedata with custom currencyId to signal that currency is not owned
                    _currentCurrencyBalance.postValue(CurrencyBalance(currencyId = NOT_INSERTED, amount = 0.0))
                } else {
                    _currentCurrencyBalance.postValue(balance)
                }
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    // Calculate total portfolio balance
    fun fetchTotalBalanceInUsd() {
        viewModelScope.launch {
            try {
                fetchPortfolio().join()
                val totalBalance = completeCurrencies.value!!.map {
                    round(it.priceUsd.toDouble() * it.balance, 2)
                }.sum()

                _totalBalanceInUsd.postValue(round(totalBalance, 2))
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        }
    }

    // Calculate total portfolio balance
    fun fetchUsdBalance() {
        viewModelScope.launch {
            try {
                val usd = balanceDao.getCurrency("usd")
                _usdBalance.postValue(round(usd.amount, 2))
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
                val currencyPrice = currency.priceUsd.toDouble()
                // Dynamic round
                val currencyAmount = round(usdAmount / currencyPrice, null)

                // Persist balance
                insertBalance(currency.id, currencyAmount)
                insertBalance("usd", -usdAmount)

                // Insert into transaction table
                transactionDao.insert(
                    CurrencyTransaction(
                        currencySymbol = currency.symbol,
                        currencyAmount = currencyAmount,
                        currencyPrice = currencyPrice,
                        usdAmount = usdAmount,
                        isBuy = true,
                        transactionDate = DateConverters.toDateString(LocalDateTime.now())
                    )
                )
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
                val usdAmount = round(currencyAmount * currencyPrice, 2)

                // Persist balance
                insertBalance(currencyId = currency.id, amount = -currencyAmount)
                insertBalance(currencyId = "usd", amount = (usdAmount))

                // Insert into transact ion table
                transactionDao.insert(
                    CurrencyTransaction(
                    currencySymbol = currency.symbol,
                    currencyAmount = currencyAmount,
                    currencyPrice = currencyPrice,
                    usdAmount = usdAmount,
                    isBuy = false,
                    transactionDate = DateConverters.toDateString(LocalDateTime.now())
                        )
                )
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    // Insert or update into CurrencyBalance table
    private fun insertBalance(currencyId: String, amount: Double) {
        viewModelScope.launch {
            try {
                val balance = balanceDao.getCurrency(currencyId)

                if(balance == null) { // Balance does not exist in DB
                    balanceDao.insert(CurrencyBalance(currencyId, amount))
                } else { //  Update existing balance
                    val newBalance = balance.amount + amount
                    if(newBalance <= 0 && currencyId !== "usd") { // If no more is owned, delete from portfolio
                        balanceDao.delete(balance)
                    } else {
                        balanceDao.update(CurrencyBalance(currencyId, round(newBalance, null)))
                    }
                }
            } catch (e: Exception) {
                Log.d("db", e.toString())
            }
        }
    }

    // Make initial deposit when opening app for the first time. Called if shared preference flag is true (SplashActivity)
    fun makeInitialDeposit() {
        viewModelScope.launch {
            try {
                balanceDao.insert(CurrencyBalance(currencyId = "usd", amount = 10_000.0))
                transactionDao.insert(
                    CurrencyTransaction(
                        currencySymbol = TRANSACTION_INITIAL,
                        currencyAmount = 10000.0,
                        currencyPrice = 1.0,
                        usdAmount = 10000.0,
                        isBuy = false,
                        transactionDate = DateConverters.toDateString(LocalDateTime.now())
                    )
                )

            } catch (e: Exception) {
                Log.d("db", "catchInit")
            }
        }
    }


    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val transactions = transactionDao.listTransactions()
                _transactions.postValue(transactions)
            } catch (e: Exception) {
                Log.d("db", e.toString())
                e.printStackTrace()
            }
        } }

    fun convertUsdToCurrentCurrency(usdAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return round(usdAmount / currencyPrice, null)
    }

    fun convertCurrentCurrencyToUsd(currencyAmount: Double): Double {
        val currencyPrice = parseDouble(currentCurrency.value!!.priceUsd)
        return round(currencyAmount * currencyPrice, 2)
    }

}

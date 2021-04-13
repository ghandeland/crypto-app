package no.kristiania.pgr208_1.pgr208_1_exam

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.pgr208_1.pgr208_1_exam.data.API
import no.kristiania.pgr208_1.pgr208_1_exam.data.CoinCapService
import no.kristiania.pgr208_1.pgr208_1_exam.data.domain.CryptoCurrency

class MainViewModel : ViewModel() {
    val coinCapService: CoinCapService = API.coinCapService

    private val _currencies = MutableLiveData<List<CryptoCurrency>>()
    val currencies: LiveData<List<CryptoCurrency>> get() = _currencies

    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> get() = _error

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _error.postValue(Unit)
    }

    init {
        reload()
    }

    private fun reload() {
        loadAssets()
    }

    private fun loadAssets() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currencyList = coinCapService.getAssets()
            _currencies.postValue(currencyList.data)
        }
    }

}
package fr.uca.bitcoinchecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uca.bitcoinchecker.model.Endpoints
import fr.uca.bitcoinchecker.model.Quote
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.utils.api.HttpRequestExecutor
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToListOfQuoteSuggestionsConverter
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToQuotesConverter
import fr.uca.bitcoinchecker.view.activity.MainActivity
import kotlinx.coroutines.launch

class MainActivityViewModel(private val mainActivity: MainActivity): ViewModel(), HttpRequestExecutor.Callback {
    val DEFAULT_CRYPTO = "bitcoin"
    private val RESPONSE_ALL_QUOTES = "fr.uca.bitcoinchecker.mainactivity.allquotes"
    private val RESPONSE_CURRENT_QUOTE = "fr.uca.bitcoinchecker.mainactivity.currentquote"

    val listOfSuggestions = MutableLiveData<List<QuoteSuggestion>>()
    val currentQuote = MutableLiveData<Quote>()

    fun loadData(){
        setCurrentCryptoName(DEFAULT_CRYPTO)
        viewModelScope.launch {
            HttpRequestExecutor.executeUrlResolution(
                this@MainActivityViewModel,
                Endpoints.API_ENDPOINT_ALL_QUOTES.prefix,
                JsonToListOfQuoteSuggestionsConverter.Companion,
                RESPONSE_ALL_QUOTES
            )
        }
    }

    override fun onDataReceived(item: Any, responseKey: String) {
        if(responseKey == RESPONSE_ALL_QUOTES) {
            mainActivity.runOnUiThread {
                listOfSuggestions.value = item as List<QuoteSuggestion>
            }
        }
        else if(responseKey == RESPONSE_CURRENT_QUOTE){
            with(item as Quote)
            {
                mainActivity.runOnUiThread {
                    currentQuote.value = item
                }
            }
        }
    }

    fun setCurrentCryptoName(id: String) {
        viewModelScope.launch {
            HttpRequestExecutor.executeUrlResolution(
                this@MainActivityViewModel,
                Endpoints.API_ENDPOINT_CURRENT_QUOTE.prefix + id + Endpoints.API_ENDPOINT_CURRENT_QUOTE.suffix,
                JsonToQuotesConverter.Companion,
                RESPONSE_CURRENT_QUOTE
            )
        }
    }
}
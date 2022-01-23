package fr.uca.bitcoinchecker.view.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.view.fragment.NotificationListFragment
import androidx.cursoradapter.widget.SimpleCursorAdapter
import android.provider.BaseColumns

import android.database.MatrixCursor
import android.graphics.BitmapFactory
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.uca.bitcoinchecker.model.Endpoints
import fr.uca.bitcoinchecker.model.Quote
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.utils.api.HttpRequestExecutor
import fr.uca.bitcoinchecker.utils.api.HttpRequestExecutor.Callback
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToListOfQuoteSuggestionsConverter
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToQuotesConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*


class MainActivity : SimpleFragmentActivity(), SearchView.OnQueryTextListener,
    Callback {
    private val DEFAULT_CRYPTO = "bitcoin"
    private val RESPONSE_ALL_QUOTES = "fr.uca.bitcoinchecker.mainactivity.allquotes"
    private val RESPONSE_CURRENT_QUOTE = "fr.uca.bitcoinchecker.mainactivity.currentquote"

    private lateinit var suggestions: Array<QuoteSuggestion?>
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    private var currentSuggestions = mutableListOf<String>()
    private lateinit var currentQuote: Quote
    private lateinit var imageCrypto: ImageView
    private lateinit var textCrypto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageCrypto = findViewById(R.id.cryptoImage)
        textCrypto = findViewById(R.id.textCrypto)
        runBlocking(Dispatchers.Default) {
            HttpRequestExecutor.executeUrlResolution(
                this@MainActivity,
                Endpoints.API_ENDPOINT_CURRENT_QUOTE.prefix + DEFAULT_CRYPTO + Endpoints.API_ENDPOINT_CURRENT_QUOTE.suffix,
                JsonToQuotesConverter.Companion,
                RESPONSE_CURRENT_QUOTE
            )
            HttpRequestExecutor.executeUrlResolution(
                this@MainActivity,
                Endpoints.API_ENDPOINT_ALL_QUOTES.prefix,
                JsonToListOfQuoteSuggestionsConverter.Companion,
                RESPONSE_ALL_QUOTES
            )
        }
        handleIntent(intent)
    }

    override fun isFragmentLateinitialized()=true

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_searchbar, menu)

        createSuggestionAdapter()

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(this)
        searchView.suggestionsAdapter = suggestionAdapter
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                searchView.setQuery(currentSuggestions[position],false)
                return true
            }
        })


        return true
    }

    private fun createSuggestionAdapter() {
        val from = arrayOf("text")
        val to = intArrayOf(android.R.id.text1)

        suggestionAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_1,
            null, from, to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }


    override fun createFragment() = NotificationListFragment.newInstance(currentQuote.name ?: DEFAULT_CRYPTO)

    override fun getLayoutResId() = R.layout.activity_main

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(currentSuggestions.contains(query)) {
            var id: String = DEFAULT_CRYPTO
            suggestions.forEach {
                if (it!!.name == query) {
                    id = it.id
                }
            }
            runBlocking(Dispatchers.Default) {
                HttpRequestExecutor.executeUrlResolution(
                    this@MainActivity,
                    Endpoints.API_ENDPOINT_CURRENT_QUOTE.prefix + id + Endpoints.API_ENDPOINT_CURRENT_QUOTE.suffix,
                    JsonToQuotesConverter.Companion,
                    RESPONSE_CURRENT_QUOTE
                )
            }
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(!suggestions.isEmpty() && newText?.length!! >= 2) {
            currentSuggestions.clear()
            val c = MatrixCursor(arrayOf(BaseColumns._ID, "text"))
            for (i in suggestions.indices) {
                if (suggestions[i]!!.name.lowercase(Locale.getDefault())
                        .startsWith(newText.lowercase(Locale.getDefault()))
                ){
                    c.addRow(arrayOf<Any>(i, suggestions[i]!!.name))
                    currentSuggestions.add(suggestions[i]!!.name)
                }
            }
            suggestionAdapter.changeCursor(c)
        }
        else{
            val c = MatrixCursor(arrayOf(BaseColumns._ID, "text"))
            suggestionAdapter.changeCursor(c)
        }
        return false
    }

    override fun onDataReceived(item: Any, responseKey: String) {
        if(responseKey == RESPONSE_ALL_QUOTES) {
            with(item as List<QuoteSuggestion>)
            {
                suggestions = arrayOfNulls(item.size)
                for ((i, quote) in item.withIndex()) {
                    suggestions[i] = quote
                }
            }
        }
        else if(responseKey == RESPONSE_CURRENT_QUOTE){
            with(item as Quote)
            {
                currentQuote = item
                val image = BitmapFactory.decodeStream(URL(currentQuote.imageUrl).openConnection().getInputStream())
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    imageCrypto.setImageBitmap(image)
                    textCrypto.text =
                        String.format("1 %s = %f $", currentQuote.name, currentQuote.currentPrice)
                })
                startFragmentOrReplace()
            }
        }
    }


}
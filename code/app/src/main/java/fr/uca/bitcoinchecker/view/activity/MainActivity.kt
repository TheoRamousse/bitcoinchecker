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
import android.util.Log
import android.widget.CursorAdapter
import fr.uca.bitcoinchecker.model.Endpoints
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.utils.api.HttpRequestExecutor
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToListOfQuoteSuggestionsConverter
import java.util.*


class MainActivity : SimpleFragmentActivity(), SearchView.OnQueryTextListener,
    HttpRequestExecutor.Callback<List<QuoteSuggestion>> {
    private var selectedContainerName: String = "bitcoin"
    private lateinit var suggestions: Array<String?>
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    private var currentSuggestions = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HttpRequestExecutor.executeUrlResolution(this, Endpoints.API_ENDPOINT_ALL_QUOTES.prefix, JsonToListOfQuoteSuggestionsConverter.Companion)

        handleIntent(intent)
    }

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


    override fun createFragment() = NotificationListFragment.newInstance(selectedContainerName)

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
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText?.length!! >= 2) {
            currentSuggestions.clear()
            val c = MatrixCursor(arrayOf(BaseColumns._ID, "text"))
            for (i in suggestions.indices) {
                if (suggestions[i]!!.lowercase(Locale.getDefault())
                        .startsWith(newText.lowercase(Locale.getDefault()))
                ){
                    c.addRow(arrayOf<Any>(i, suggestions[i]!!))
                    currentSuggestions.add(suggestions[i]!!)
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

    override fun onDataReceived(item: List<QuoteSuggestion>) {
        Log.d("TOTO", "Start")
        suggestions = arrayOfNulls(item.size)
        for((i, quote) in item.withIndex()){
            suggestions[i]=quote.name
        }
        Log.d("TOTO", "End")
    }

}
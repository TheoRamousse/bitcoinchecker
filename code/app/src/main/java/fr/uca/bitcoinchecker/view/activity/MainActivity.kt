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
import android.widget.CursorAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import fr.uca.bitcoinchecker.databinding.ActivityMainBinding
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.view.adapter.NotificationRecyclerViewAdapter
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import kotlinx.coroutines.*
import java.util.*


class MainActivity : SimpleFragmentActivity(), SearchView.OnQueryTextListener, NotificationRecyclerViewAdapter.Callback{
    val mainScope = MainScope()

    private lateinit var suggestions: Array<QuoteSuggestion?>
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    private var currentSuggestions = mutableListOf<String>()
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this@MainActivity, ViewModelFactory.createViewModel { MainActivityViewModel(this@MainActivity) }).get(
            MainActivityViewModel::class.java)

        viewModel.listOfSuggestions.observe(this@MainActivity) {
            suggestions = arrayOfNulls(it.size)
            for ((n, quote) in it.withIndex()) {
                suggestions[n] = quote
            }
        }

        viewModel.currentQuote.observe(this@MainActivity) {
            startFragmentOrReplace()
        }



        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        handleIntent(intent)

        mainScope.launch {
            viewModel.loadData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
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


    override fun createFragment() = NotificationListFragment.newInstance(viewModel.currentQuote.value!!.name ?: viewModel.DEFAULT_CRYPTO, viewModel.currentQuote.value!!.id, this)

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
            var id: String = ""
            suggestions.forEach {
                if (it!!.name == query) {
                    id = it.id
                }
            }
            runBlocking(Dispatchers.Default) {
                launch {
                    viewModel.setCurrentCryptoName(id)
                }}
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

    override fun receiveRemove(notification: NotificationItem) {
        viewModel.removeNotification(notification)
    }


}
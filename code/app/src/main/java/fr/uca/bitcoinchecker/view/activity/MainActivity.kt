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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import fr.uca.bitcoinchecker.databinding.ActivityMainBinding
import fr.uca.bitcoinchecker.model.dto.QuoteSuggestion
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import kotlinx.coroutines.*
import java.net.URL
import java.util.*


class MainActivity : SimpleFragmentActivity(), SearchView.OnQueryTextListener{
    val mainScope = MainScope()

    private lateinit var suggestions: Array<QuoteSuggestion?>
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    private var currentSuggestions = mutableListOf<String>()
    private lateinit var viewModel: MainActivityViewModel
    private var counterEasterEgg = 0
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this@MainActivity, ViewModelFactory.createViewModel { MainActivityViewModel(this@MainActivity) }).get(
            MainActivityViewModel::class.java)

        viewModel.listOfSuggestions.observe(this@MainActivity, {
            suggestions = arrayOfNulls(it.size)
            for ((n, quote) in it.withIndex()) {
                suggestions[n] = quote
            }
        })

        mediaPlayer = MediaPlayer.create(this, R.raw.drift)

        viewModel.currentQuote.observe(this@MainActivity, {
            startFragmentOrReplace()
        })



        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        mainScope.launch {
            Glide.with(this@MainActivity).load(R.drawable.gif_background).into(binding.backgroundMoney)
        }



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

    fun incrementCountEasterEgg(v: View){
        counterEasterEgg++
        if(counterEasterEgg == 15){
            counterEasterEgg = 0
            mediaPlayer?.start()
            var t : Thread = Thread {
                    mainScope.launch {
                        Glide.with(this@MainActivity).clear(binding.backgroundMoney)
                        Glide.with(this@MainActivity).load(R.drawable.easter_egg)
                            .into(binding.backgroundMoney)
                        binding.cryptoImage.visibility = GONE
                        binding.cryptoImage.visibility = GONE
                    }
                    Thread.sleep(4000)
                    mainScope.launch {
                        Glide.with(this@MainActivity).clear(binding.backgroundMoney)
                        Glide.with(this@MainActivity).load(R.drawable.gif_background).into(binding.backgroundMoney)
                        binding.cryptoImage.visibility = VISIBLE
                        binding.cryptoImage.visibility = VISIBLE
                    }
            }

            t.start()
        }
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


    override fun createFragment() = NotificationListFragment.newInstance(viewModel.currentQuote.value!!.name ?: viewModel.DEFAULT_CRYPTO)

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


}
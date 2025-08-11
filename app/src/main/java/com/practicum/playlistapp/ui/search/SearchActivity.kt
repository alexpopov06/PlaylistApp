package com.practicum.playlistapp.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View

import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistapp.Creator
import com.practicum.playlistapp.ui.media.MediatekActivity
import com.practicum.playlistapp.R
import com.practicum.playlistapp.domain.models.Track




import com.practicum.playlistapp.domain.api.TracksInteractor

import com.practicum.playlistapp.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.domain.usecase.GetHistoryUseCase


class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private var inputEditText: EditText? = null
    private var lastFailedSearchQuery: String? = null

    private lateinit var rvTrack: RecyclerView
    private lateinit var emptyImage: ImageView
    private lateinit var emptyText: TextView
    private lateinit var nowifiImage: ImageView
    private lateinit var nowifiText1: TextView
    private lateinit var nowifiText2: TextView
    private lateinit var nowifiText3: TextView
    private lateinit var update: MaterialButton
    private lateinit var adapter: TrackAdapter
    private lateinit var youSearch: TextView
    private val tracksInteractor: TracksInteractor = Creator.provideTracksInteractor()
    private lateinit var clearHistory: Button
    private lateinit var progBar: ProgressBar
    private val tracks = mutableListOf<Track>()
    private lateinit var addToHistoryUseCase: AddToHistoryUseCase
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase
    private var isClickable = true
    private val handler = Handler(Looper.getMainLooper())
    val CLICK_DEBOUNCE_DELAY = 1000L
    val SEARCH_DEBOUNCE_DELAY = 2000L

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
        private const val LAST_FAILED_QUERY_KEY = "last_failed_query_key"
        fun moveToMediatek(context: Context, track: Track) {
            val intent = Intent(context, MediatekActivity::class.java).apply {
                val gson = Gson()
                val trackJson = gson.toJson(track)
                putExtra("TRACK_EXTRA", trackJson)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        addToHistoryUseCase = Creator.provideAddToHistoryUseCase(this)
        getHistoryUseCase = Creator.provideGetHistoryUseCase(this)
        clearHistoryUseCase = Creator.provideClearHistoryUseCase(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }


        initViews()
        setupAdapter()
        restoreState(savedInstanceState)
        setupListeners()
    }

    private fun initViews() {
        progBar = findViewById<ProgressBar>(R.id.progressBar)
        rvTrack = findViewById(R.id.RecycleTracks)
        inputEditText = findViewById(R.id.inputText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val back = findViewById<MaterialButton>(R.id.button_back)
        emptyImage = findViewById(R.id.EmptyImage)
        emptyText = findViewById(R.id.EmptyText)
        nowifiImage = findViewById(R.id.NoWifiImage)
        nowifiText1 = findViewById(R.id.NoWifiText1)
        nowifiText2 = findViewById(R.id.NoWifiText2)
        nowifiText3 = findViewById(R.id.NoWifiText3)
        youSearch = findViewById<TextView>(R.id.historySearch)
        clearHistory = findViewById<Button>(R.id.clearHistory)
        update = findViewById(R.id.update)
        inputEditText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText?.text?.isEmpty() == true) {
                showSearchHistory()
            }
        }
        clearHistory.setOnClickListener {
            tracks.clear()
            adapter.notifyDataSetChanged()
            youSearch.visibility = View.GONE
            clearHistory.visibility = View.GONE
            clearHistoryUseCase.execute()

        }


        back.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            progBar.visibility = View.GONE
            inputEditText?.text?.clear()
            searchText = ""
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
            if (inputEditText?.hasFocus() == true) {
                showSearchHistory()
            }

        }
    }

    private fun setupAdapter() {

        adapter = TrackAdapter(tracks, addToHistoryUseCase, ::delayDebounce)
        rvTrack.adapter = adapter
        rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            lastFailedSearchQuery = savedInstanceState.getString(LAST_FAILED_QUERY_KEY, null)
            inputEditText?.setText(searchText)
        }
    }

    private fun setupListeners() {
        inputEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""


                findViewById<ImageView>(R.id.clearIcon).visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    searchDebounce(searchText)
                    tracks.clear()
                    adapter.notifyDataSetChanged()

                }else{
                    lastFailedSearchQuery = null
                    hideNoWifi()
                    showSearchHistory()
                }


            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        update.setOnClickListener {
            if (lastFailedSearchQuery != null) {
                performSearch(lastFailedSearchQuery!!)
            }else{
                hideNoWifi()

            }
        }


    }
    private var lastRunnable: Runnable? = null

    fun searchDebounce(query: String) {
        if (lastRunnable != null) {
            handler.removeCallbacks(lastRunnable!!)
        }
        lastRunnable = Runnable {
            performSearch(query)
        }
        handler.postDelayed(lastRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch(query: String) {
        progBar.visibility = View.VISIBLE
        rvTrack.visibility = View.GONE
        emptyImage.visibility = View.GONE
        emptyText.visibility = View.GONE
        hideNoWifi()

        tracksInteractor.searchTrack(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progBar.visibility = View.GONE


                    tracks.clear()
                    tracks.addAll(foundTracks)
                    adapter.notifyDataSetChanged()


                    if (foundTracks.isNotEmpty()) {
                        NoEmptyList()
                    } else {
                        ShowEmptyList()
                    }

                    lastFailedSearchQuery = if (foundTracks.isEmpty()) query else null
                }
            }
        })
    }

    fun ShowEmptyList() {
        rvTrack.visibility = View.GONE
        emptyImage.visibility = View.VISIBLE
        emptyText.visibility = View.VISIBLE
        nowifiImage.visibility = View.GONE
        nowifiText1.visibility = View.GONE
        nowifiText2.visibility = View.GONE
        nowifiText3.visibility = View.GONE
        update.visibility = View.GONE
        youSearch.visibility= View.GONE
        clearHistory.visibility= View.GONE
    }

    fun NoEmptyList() {
        rvTrack.visibility = View.VISIBLE
        emptyImage.visibility = View.GONE
        emptyText.visibility = View.GONE
        nowifiImage.visibility = View.GONE
        nowifiText1.visibility = View.GONE
        nowifiText2.visibility = View.GONE
        nowifiText3.visibility = View.GONE
        update.visibility = View.GONE
        youSearch.visibility= View.GONE
        clearHistory.visibility= View.GONE
    }
    private fun hideNoWifi() {
        nowifiImage.visibility = View.GONE
        nowifiText1.visibility = View.GONE
        nowifiText2.visibility = View.GONE
        nowifiText3.visibility = View.GONE
        update.visibility = View.GONE
    }

    fun showNoWifi() {
        rvTrack.visibility = View.GONE
        emptyImage.visibility = View.GONE
        emptyText.visibility = View.GONE
        nowifiImage.visibility = View.VISIBLE
        nowifiText1.visibility = View.VISIBLE
        nowifiText2.visibility = View.VISIBLE
        nowifiText3.visibility = View.VISIBLE
        update.visibility = View.VISIBLE
        youSearch.visibility= View.GONE
        clearHistory.visibility= View.GONE
    }
    private fun showSearchHistory() {
        emptyImage.visibility = View.GONE
        emptyText.visibility = View.GONE
        val historyTracks = getHistoryUseCase.execute()
        if (historyTracks.isNotEmpty()){
            youSearch.visibility= View.VISIBLE
            clearHistory.visibility= View.VISIBLE
            tracks.clear()
            tracks.addAll(historyTracks)
            rvTrack.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }else{
            youSearch.visibility= View.GONE
            clearHistory.visibility= View.GONE
            rvTrack.visibility = View.GONE
            val historyTracks = getHistoryUseCase.execute()
            tracks.clear()
            tracks.addAll(historyTracks)
            adapter.notifyDataSetChanged()
        }

    }
    fun delayDebounce(): Boolean{
        val current = isClickable
        if (isClickable) {
            isClickable = false
            handler.postDelayed({ isClickable = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
        outState.putString(LAST_FAILED_QUERY_KEY, lastFailedSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        lastFailedSearchQuery = savedInstanceState.getString(LAST_FAILED_QUERY_KEY, null)
        inputEditText?.setText(searchText)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
    }
}
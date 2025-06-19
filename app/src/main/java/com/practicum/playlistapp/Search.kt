package com.practicum.playlistapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : AppCompatActivity() {
    private var searchText: String = ""
    private var inputEditText: EditText? = null
    private var lastFailedSearchQuery: String? = null
    private val BaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private lateinit var rvTrack: RecyclerView
    private lateinit var emptyImage: ImageView
    private lateinit var emptyText: TextView
    private lateinit var nowifiImage: ImageView
    private lateinit var nowifiText1: TextView
    private lateinit var nowifiText2: TextView
    private lateinit var nowifiText3: TextView
    private lateinit var update: MaterialButton
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var youSearch: TextView
    private lateinit var clearHistory: Button
    private val tracks = mutableListOf<Track>()

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
        private const val LAST_FAILED_QUERY_KEY = "last_failed_query_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        youSearch = findViewById<TextView>(R.id.historySearch)
        clearHistory = findViewById<Button>(R.id.clearHistory)

        initViews()
        setupAdapter()
        restoreState(savedInstanceState)
        setupListeners()

    }

    private fun initViews() {
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
            searchHistory.clearHistory()

        }


        back.setOnClickListener { finish() }

        clearButton.setOnClickListener {
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
        searchHistory = SearchHistory(
            getSharedPreferences("SearchHistoryPrefs", MODE_PRIVATE)
        )
        adapter = TrackAdapter(tracks, searchHistory)
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
                findViewById<ImageView>(R.id.clearIcon).visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    youSearch.visibility = View.GONE
                    clearHistory.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""
                if (s.isNullOrEmpty()) {
                    showSearchHistory()
                }else{
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    youSearch.visibility = View.GONE
                    clearHistory.visibility = View.GONE

                }

            }
        })

        update.setOnClickListener {
            if (lastFailedSearchQuery != null) {
                performSearch(lastFailedSearchQuery!!)
            }
        }

        inputEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchText.isNotEmpty()) {
                performSearch(searchText)
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        val movieApi = retrofit.create(TrackInterface::class.java)
        movieApi.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body() != null && response.body()!!.results != null) {
                        tracks.addAll(response.body()!!.results!!)
                    }
                    NoEmptyList()
                    adapter.notifyDataSetChanged()

                    if (tracks.isNotEmpty()) {
                        NoEmptyList()
                    } else {
                        ShowEmptyList()
                    }
                    lastFailedSearchQuery = null
                } else {
                    lastFailedSearchQuery = query
                    showNoWifi()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                lastFailedSearchQuery = query
                showNoWifi()
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
        youSearch.visibility= View.VISIBLE
        clearHistory.visibility= View.VISIBLE
        val historyTracks = searchHistory.getHistory()
        tracks.clear()
        tracks.addAll(historyTracks)
        adapter.notifyDataSetChanged()
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
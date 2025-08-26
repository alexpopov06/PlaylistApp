package com.practicum.playlistapp.ui.search

import SearchState
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.android.material.button.MaterialButton
import com.practicum.playlistapp.R
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.search.presentation.SearchView

import com.practicum.playlistapp.ui.media.MediatekActivity

class SearchActivity : AppCompatActivity(), SearchView {

    private lateinit var searchPresenter: SearchPresenter
    private lateinit var adapter: TrackAdapter

    private lateinit var inputEditText: EditText
    private lateinit var rvTrack: RecyclerView
    private lateinit var emptyImage: ImageView
    private lateinit var emptyText: TextView
    private lateinit var nowifiImage: ImageView
    private lateinit var nowifiText1: TextView
    private lateinit var nowifiText2: TextView
    private lateinit var nowifiText3: TextView
    private lateinit var update: Button
    private lateinit var youSearch: TextView
    private lateinit var clearHistory: Button
    private lateinit var clearIcon: ImageView
    private lateinit var progBar: ProgressBar
    private lateinit var buttonBack: MaterialButton

    companion object {
        fun moveToMediatek(context: Context, track: Track) {
            val intent = Intent(context, MediatekActivity::class.java)
            val gson = Gson()
            val trackJson = gson.toJson(track)
            intent.putExtra("TRACK_EXTRA", trackJson)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val rootView = findViewById<View>(R.id.search)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }

        // findViewById
        inputEditText = findViewById(R.id.inputText)
        rvTrack = findViewById(R.id.RecycleTracks)
        emptyImage = findViewById(R.id.EmptyImage)
        emptyText = findViewById(R.id.EmptyText)
        nowifiImage = findViewById(R.id.NoWifiImage)
        nowifiText1 = findViewById(R.id.NoWifiText1)
        nowifiText2 = findViewById(R.id.NoWifiText2)
        nowifiText3 = findViewById(R.id.NoWifiText3)
        update = findViewById(R.id.update)
        youSearch = findViewById(R.id.historySearch)
        clearHistory = findViewById(R.id.clearHistory)
        clearIcon = findViewById(R.id.clearIcon)
        progBar = findViewById(R.id.progressBar)
        buttonBack = findViewById(R.id.button_back)

        adapter = TrackAdapter(mutableListOf(), Creator.provideAddToHistoryUseCase(this)) {
            searchPresenter.delayDebounce()
        }

        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = adapter

        searchPresenter = Creator.provideSearchPresenter(this, this)

        inputEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchPresenter.onTextChanged(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchPresenter.onFocusChanged(hasFocus, inputEditText.text.toString())
        }

        clearIcon.setOnClickListener { searchPresenter.clearSearch() }
        update.setOnClickListener { searchPresenter.onUpdateClicked() }
        clearHistory.setOnClickListener { searchPresenter.clearHistory() }
        buttonBack.setOnClickListener { finish() }

        // показать историю при старте если поле пустое
        inputEditText.post {
            if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                searchPresenter.showSearchHistory()
            }
        }
    }

    override fun showClearButton(show: Boolean) {
        clearIcon.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun clearSearchInput() {
        inputEditText.text.clear()
    }

    override fun render(state: SearchState) {
        // Сначала скрываем всё
        rvTrack.visibility = View.GONE
        progBar.visibility = View.GONE
        emptyImage.visibility = View.GONE
        emptyText.visibility = View.GONE
        nowifiImage.visibility = View.GONE
        nowifiText1.visibility = View.GONE
        nowifiText2.visibility = View.GONE
        nowifiText3.visibility = View.GONE
        update.visibility = View.GONE
        youSearch.visibility = View.GONE
        clearHistory.visibility = View.GONE

        when (state) {
            is SearchState.Loading -> progBar.visibility = View.VISIBLE
            is SearchState.Content -> {
                rvTrack.visibility = View.VISIBLE
                adapter.updateTracks(state.tracks)
            }
            is SearchState.Empty -> {
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Ничего не найдено"
            }
            is SearchState.Error -> {
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Что-то пошло не так"
            }
            is SearchState.NoWifi -> {
                nowifiImage.visibility = View.VISIBLE
                nowifiText1.visibility = View.VISIBLE
                nowifiText2.visibility = View.VISIBLE
                nowifiText3.visibility = View.VISIBLE
                update.visibility = View.VISIBLE
            }
            is SearchState.History -> {
                val historyTracks = Creator.provideGetHistoryUseCase(this).execute()
                if (historyTracks.isNotEmpty()) {
                    rvTrack.visibility = View.VISIBLE
                    youSearch.visibility = View.VISIBLE
                    clearHistory.visibility = View.VISIBLE
                    adapter.updateTracks(historyTracks)
                }
            }
            is SearchState.Idle -> Unit
        }
    }
}

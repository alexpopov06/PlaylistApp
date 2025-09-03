package com.practicum.playlistapp.search.ui

import SearchState
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.presentation.TracksViewModel
import com.practicum.playlistapp.player.ui.MediatekActivity


class SearchActivity : AppCompatActivity() {

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

    private var viewModel: TracksViewModel? = null

    companion object {
        private const val TAG = "SearchActivity"

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
        Log.d(TAG, "onCreate")
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



        viewModel = ViewModelProvider(this, TracksViewModel.Companion.getFactory())
            .get(TracksViewModel::class.java)


        viewModel?.observeState()?.observe(this) {
            Log.d(TAG, "State observed: $it")
            render(it)
        }


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

        adapter = TrackAdapter(mutableListOf(), Creator.provideAddToHistoryUseCase()) {
            Log.d(TAG, "Track clicked, debouncing")
            viewModel!!.delayDebounce()
        }

        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = adapter
        clearIcon.setOnClickListener {
            Log.d(TAG, "clearIcon clicked")

            inputEditText.setText("")

            viewModel!!.clearSearch()
        }


        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString() ?: ""
                Log.d(TAG, "onTextChanged: '$text', start=$start, before=$before, count=$count")


                showClearButton(text.isNotEmpty())

                viewModel?.searchDebounce(changedText = text)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val currentText = inputEditText.text.toString()
            Log.d(TAG, "onFocusChange: hasFocus=$hasFocus, text='$currentText'")
            viewModel!!.onFocusChanged(hasFocus, currentText)
        }



        update.setOnClickListener {
            Log.d(TAG, "update clicked")
            viewModel!!.onUpdateClicked()
        }

        clearHistory.setOnClickListener {
            Log.d(TAG, "clearHistory clicked")
            viewModel!!.clearHistory()
        }

        buttonBack.setOnClickListener {
            Log.d(TAG, "buttonBack clicked")
            finish()
        }


        inputEditText.post {
            val hasFocus = inputEditText.hasFocus()
            val isEmpty = inputEditText.text.isEmpty()
            Log.d(TAG, "Initial check: hasFocus=$hasFocus, isEmpty=$isEmpty")
            if (hasFocus && isEmpty) {
                viewModel!!.showSearchHistory()
            }
        }
    }

    private fun showClearButton(show: Boolean) {
        Log.d(TAG, "showClearButton: $show")
        clearIcon.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun clearSearchInput() {
        Log.d(TAG, "clearSearchInput")
        inputEditText.text.clear()
    }

    private fun render(state: SearchState) {
        Log.d(TAG, "render: $state")


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
            is SearchState.Loading -> {
                Log.d(TAG, "Showing progress bar")
                progBar.visibility = View.VISIBLE
                showClearButton(inputEditText.text.isNotEmpty())
            }

            is SearchState.Content -> {
                Log.d(TAG, "Showing content: ${state.tracks.size} tracks")
                rvTrack.visibility = View.VISIBLE
                adapter.updateTracks(state.tracks)
                showClearButton(true)
            }

            is SearchState.Empty -> {
                Log.d(TAG, "Showing empty state")
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Ничего не найдено"
                showClearButton(true)
            }

            is SearchState.Error -> {
                Log.d(TAG, "Showing error state")
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Что-то пошло не так"
                showClearButton(true)
            }

            is SearchState.NoWifi -> {
                Log.d(TAG, "Showing no wifi state")
                nowifiImage.visibility = View.VISIBLE
                nowifiText1.visibility = View.VISIBLE
                nowifiText2.visibility = View.VISIBLE
                nowifiText3.visibility = View.VISIBLE
                update.visibility = View.VISIBLE
                showClearButton(inputEditText.text.isNotEmpty())
            }

            is SearchState.History -> {
                Log.d(TAG, "Showing history state")
                val historyTracks = Creator.provideGetHistoryUseCase().execute()
                Log.d(TAG, "History tracks count: ${historyTracks.size}")

                if (historyTracks.isNotEmpty()) {
                    rvTrack.visibility = View.VISIBLE
                    youSearch.visibility = View.VISIBLE
                    clearHistory.visibility = View.VISIBLE
                    adapter.updateTracks(historyTracks)
                }
                showClearButton(false)
            }

            is SearchState.Idle -> {
                Log.d(TAG, "Showing idle state")
                showClearButton(false)
            }
        }
    }
}
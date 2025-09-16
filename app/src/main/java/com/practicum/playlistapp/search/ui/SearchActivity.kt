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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.player.ui.MediatekActivity
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.presentation.TracksViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    // ✅ ИСПРАВЛЕНО: Все зависимости инжектируются
    private val viewModel: TracksViewModel by viewModel()
    private val addToHistoryUseCase: AddToHistoryUseCase by inject()
    private val gson: Gson by inject()

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s?.toString() ?: ""
            showClearButton(text.isNotEmpty())
            viewModel.searchDebounce(changedText = text)
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    companion object {
        private const val TAG = "SearchActivity"


        fun moveToMediatek(context: Context, track: Track, gson: Gson) {
            val intent = Intent(context, MediatekActivity::class.java)
            val trackJson = gson.toJson(track)
            intent.putExtra("TRACK_EXTRA", trackJson)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_search)

        setupWindowInsets()
        initViews()
        setupRecyclerView()
        setupListeners()
        setupInitialState()
    }

    override fun onDestroy() {
        super.onDestroy()
        inputEditText.removeTextChangedListener(textWatcher)
    }

    private fun setupWindowInsets() {
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
    }

    private fun initViews() {
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
    }

    private fun setupRecyclerView() {
        // ✅ ИСПРАВЛЕНО: Передаем инжектированный Gson в адаптер
        adapter = TrackAdapter(
            tracks = mutableListOf(),
            addToHistoryUseCase = addToHistoryUseCase,
            delayDebounce = {
                viewModel.delayDebounce()
                true
            },
            gson = gson
        )

        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = adapter
    }

    private fun setupListeners() {
        clearIcon.setOnClickListener {
            Log.d(TAG, "clearIcon clicked")
            inputEditText.setText("")
            viewModel.clearSearch()
        }

        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val currentText = inputEditText.text.toString()
            viewModel.onFocusChanged(hasFocus, currentText)
        }

        update.setOnClickListener {
            Log.d(TAG, "update clicked")
            viewModel.onUpdateClicked()
        }

        clearHistory.setOnClickListener {
            Log.d(TAG, "clearHistory clicked")
            viewModel.clearHistory()
        }

        buttonBack.setOnClickListener {
            Log.d(TAG, "buttonBack clicked")
            finish()
        }
    }

    private fun setupInitialState() {
        inputEditText.post {
            val hasFocus = inputEditText.hasFocus()
            val isEmpty = inputEditText.text.isEmpty()
            if (hasFocus && isEmpty) {
                viewModel.showSearchHistory()
            }
        }

        viewModel.observeState().observe(this) { state ->
            Log.d(TAG, "State observed: $state")
            render(state)
        }
    }

    private fun showClearButton(show: Boolean) {
        Log.d(TAG, "showClearButton: $show")
        clearIcon.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun render(state: SearchState) {
        Log.d(TAG, "render: $state")

        listOf(rvTrack, progBar, emptyImage, emptyText, nowifiImage,
            nowifiText1, nowifiText2, nowifiText3, update, youSearch, clearHistory)
            .forEach { it.visibility = View.GONE }

        when (state) {
            is SearchState.Loading -> {
                progBar.visibility = View.VISIBLE
                showClearButton(inputEditText.text.isNotEmpty())
            }
            is SearchState.Content -> {
                rvTrack.visibility = View.VISIBLE
                adapter.updateTracks(state.tracks)
                showClearButton(true)
            }
            is SearchState.Empty -> {
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Ничего не найдено"
                showClearButton(true)
            }
            is SearchState.Error -> {
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Что-то пошло не так"
                showClearButton(true)
            }
            is SearchState.NoWifi -> {
                nowifiImage.visibility = View.VISIBLE
                nowifiText1.visibility = View.VISIBLE
                nowifiText2.visibility = View.VISIBLE
                nowifiText3.visibility = View.VISIBLE
                update.visibility = View.VISIBLE
                showClearButton(inputEditText.text.isNotEmpty())
            }
            is SearchState.History -> {
                if (state.tracks.isNotEmpty()) {
                    rvTrack.visibility = View.VISIBLE
                    youSearch.visibility = View.VISIBLE
                    clearHistory.visibility = View.VISIBLE
                    adapter.updateTracks(state.tracks)
                }
                showClearButton(false)
            }
            is SearchState.Idle -> {
                showClearButton(false)
            }
        }
    }
}
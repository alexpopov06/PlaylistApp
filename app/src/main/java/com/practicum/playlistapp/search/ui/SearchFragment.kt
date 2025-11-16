package com.practicum.playlistapp.search.ui

import SearchState
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.FragmentSearchBinding
import com.practicum.playlistapp.player.ui.PlayerFragment
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.presentation.TracksViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

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

    private val viewModel: TracksViewModel by viewModel()
    private val addToHistoryUseCase: AddToHistoryUseCase by inject()
    private val gson: Gson by inject()

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s?.toString() ?: ""
            showClearButton(text.isNotEmpty())
            viewModel.searchDebounce(text)
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    companion object {
        private const val TAG = "SearchFragment"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        setupWindowInsets()
        initViews()
        setupRecyclerView()
        setupListeners()
        setupInitialState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputEditText.removeTextChangedListener(textWatcher)
    }

    private fun setupWindowInsets() {
        val rootView = binding.root
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
        inputEditText = binding.inputText
        rvTrack = binding.RecycleTracks
        emptyImage = binding.EmptyImage
        emptyText = binding.EmptyText
        nowifiImage = binding.NoWifiImage
        nowifiText1 = binding.NoWifiText1
        nowifiText2 = binding.NoWifiText2
        nowifiText3 = binding.NoWifiText3
        update = binding.update
        youSearch = binding.historySearch
        clearHistory = binding.clearHistory
        clearIcon = binding.clearIcon
        progBar = binding.progressBar

    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(
            tracks = mutableListOf(),
            addToHistoryUseCase = addToHistoryUseCase,
            delayDebounce = {
                viewModel.clickDebounce()
            },
            gson = gson,
            onTrackClick = { track ->

                Log.d("SearchFragment", "Navigating to player fragment")
                try {
                    findNavController().navigate(
                        R.id.action_searchFragment2_to_playerFragment,
                        PlayerFragment.createArgs(track, gson)
                    )
                    Log.d("SearchFragment", "Navigation successful")
                } catch (e: Exception) {
                    Log.e("SearchFragment", "Navigation failed", e)
                    Toast.makeText(requireContext(), "Ошибка навигации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        rvTrack.layoutManager = LinearLayoutManager(requireContext())
        rvTrack.adapter = adapter
    }

    private fun setupListeners() {
        clearIcon.setOnClickListener {
            Log.d(TAG, "clearIcon clicked")
            inputEditText.setText("")
            viewModel.clearSearch()
        }

        inputEditText.addTextChangedListener(textWatcher)



        update.setOnClickListener {
            Log.d(TAG, "update clicked")
            viewModel.onUpdateClicked()
        }

        clearHistory.setOnClickListener {
            Log.d(TAG, "clearHistory clicked")
            viewModel.clearHistory()
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

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
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
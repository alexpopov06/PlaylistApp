package com.practicum.playlistapp.search.ui


import com.practicum.playlistapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistapp.databinding.FragmentFavoritesBinding
import com.practicum.playlistapp.player.ui.PlayerFragment
import com.practicum.playlistapp.search.presentation.FavoriteTracksState
import com.practicum.playlistapp.search.presentation.FavoriteTracksViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private val gson: Gson by inject()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observeState()
    }

    private fun setupRecycler() {
        adapter = TrackAdapter(
            tracks = mutableListOf(),
            addToHistoryUseCase = null,
            delayDebounce = { true },
            gson = gson,
            onTrackClick = { track ->
                val args = PlayerFragment.createArgs(track, gson)
                findNavController().navigate(R.id.playerFragment, args)

            }
        )

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter
    }

    private fun observeState() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            android.util.Log.d("FAV_DEBUG", "FavoritesFragment.observeState: $state")

            when (state) {
                is FavoriteTracksState.Empty -> {
                    android.util.Log.d("FAV_DEBUG", "FavoritesFragment: show EMPTY placeholder")
                    binding.placeholderGroup.isVisible = true
                    binding.recyclerFavorites.isVisible = false
                }
                is FavoriteTracksState.Content -> {
                    android.util.Log.d("FAV_DEBUG", "FavoritesFragment: show LIST size=${state.tracks.size}")
                    binding.placeholderGroup.isVisible = false
                    binding.recyclerFavorites.isVisible = true
                    adapter.updateTracks(state.tracks)
                }
            }
        }
    }

}

package com.practicum.playlistapp.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.ActivityPlayerBinding
import com.practicum.playlistapp.player.presentation.PlayerViewModel
import com.practicum.playlistapp.search.domain.model.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerFragment : Fragment() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var image: ImageView
    private lateinit var artist: TextView
    private lateinit var trackName: TextView
    private lateinit var longing: TextView
    private lateinit var year: TextView
    private lateinit var type: TextView
    private lateinit var country: TextView
    private lateinit var albom: TextView
    private lateinit var back: ImageButton
    private lateinit var time: TextView
    private lateinit var playButton: ImageView
    private lateinit var favoriteButton: ImageView
    private lateinit var plusButton: ImageView

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottomSheetAdapter: BottomSheetPlaylistsAdapter

    private val timeTrack = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val gson: Gson by inject()
    private val track: Track by lazy {
        val trackJson = arguments?.getString("TRACK_EXTRA") ?: ""
        gson.fromJson(trackJson, Track::class.java)
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()
        initViews()
        setupBottomSheet()
        viewModel.preparePlayer()
        setupObservers()
        setupClickListeners()
        updateUI(track)
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
        image = binding.imageTrack
        artist = binding.ArtistName
        trackName = binding.TrackName
        longing = binding.longing2
        year = binding.year2
        type = binding.type2
        country = binding.country2
        albom = binding.albom2
        back = binding.backButton
        time = binding.time
        playButton = binding.playButton
        favoriteButton = binding.favButton
        plusButton = binding.plusButton
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSheetAdapter = BottomSheetPlaylistsAdapter(emptyList()) { playlist ->
            viewModel.onPlaylistSelected(playlist)
        }
        binding.playlistsBottomSheetRecycler.layoutManager =
            LinearLayoutManager(requireContext())
        binding.playlistsBottomSheetRecycler.adapter = bottomSheetAdapter

        binding.newPlaylistButtonBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupObservers() {
        viewModel.observePlayerState().observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                PlayerViewModel.STATE_PLAYING -> playButton.setImageResource(R.drawable.pause)
                PlayerViewModel.STATE_PAUSED -> playButton.setImageResource(R.drawable.play)
                PlayerViewModel.STATE_PREPARED -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.play)
                }
                PlayerViewModel.STATE_DEFAULT -> playButton.isEnabled = false
            }
        })

        viewModel.observeProgressTime().observe(viewLifecycleOwner) { progressTime ->
            time.text = progressTime
        }

        viewModel.observeTrack().observe(viewLifecycleOwner) { track ->
            updateUI(track)
        }


        viewModel.observeIsFavorite().observe(viewLifecycleOwner) { isFavorite ->
            favoriteButton.setImageResource(
                if (isFavorite) R.drawable.favnight
                else R.drawable.favbutton
            )
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            bottomSheetAdapter.submitList(playlists)
        }

        viewModel.observePlaylistAddStatus().observe(viewLifecycleOwner) { result ->
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
            if (result.added) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun setupClickListeners() {
        back.setOnClickListener {
            findNavController().navigateUp()
        }

        playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }


        favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        plusButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun updateUI(track: Track) {
        artist.text = track.artistName
        trackName.text = track.trackName
        albom.text = track.collectionName
        val milliseconds = track.trackTimeMillis
        longing.text = timeTrack.format(Date(milliseconds))
        year.text = track.releaseDate?.substring(0, 4)
        type.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholdersvg)
            .into(image)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.observePlayerState().value == PlayerViewModel.STATE_PLAYING) {
            viewModel.onPlayButtonClicked()
        }
    }

    companion object {
        fun createArgs(track: Track, gson: Gson): Bundle {
            val args = Bundle()
            val trackJson = gson.toJson(track)
            args.putString("TRACK_EXTRA", trackJson)
            return args
        }
    }
}

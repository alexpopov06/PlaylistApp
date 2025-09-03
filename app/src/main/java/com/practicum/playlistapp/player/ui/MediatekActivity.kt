package com.practicum.playlistapp.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.player.presentation.MediaPlayerViewModel
import com.practicum.playlistapp.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediatekActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var artist: TextView
    private lateinit var trackName: TextView
    private lateinit var longing: TextView
    private lateinit var year: TextView
    private lateinit var type: TextView
    private lateinit var country: TextView
    private lateinit var albom: TextView
    private val timeTrack = SimpleDateFormat("mm:ss", Locale.getDefault())
    private lateinit var back: ImageButton
    private lateinit var time: TextView
    private lateinit var playButton: ImageView

    private lateinit var viewModel: MediaPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediatek)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.med)) { view, insets ->
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

        val trackJson = intent.getStringExtra("TRACK_EXTRA")
        val track = Gson().fromJson(trackJson, Track::class.java)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this, MediaPlayerViewModel.Companion.getFactory(track))
            .get(MediaPlayerViewModel::class.java)

        setupObservers()
        setupClickListeners()
        updateUI(track)
    }

    private fun setupObservers() {
        viewModel.observePlayerState().observe(this, Observer { state ->
            when (state) {
                MediaPlayerViewModel.Companion.STATE_PLAYING -> {
                    playButton.setImageResource(R.drawable.pause)
                }

                MediaPlayerViewModel.Companion.STATE_PAUSED -> {
                    playButton.setImageResource(R.drawable.play)
                }

                MediaPlayerViewModel.Companion.STATE_PREPARED -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.play)
                }

                MediaPlayerViewModel.Companion.STATE_DEFAULT -> {
                    playButton.isEnabled = false
                }
            }
        })

        viewModel.observeProgressTime().observe(this, Observer { progressTime ->
            time.text = progressTime
        })

        viewModel.observeTrack().observe(this, Observer { track ->
            updateUI(track)
        })
    }

    private fun setupClickListeners() {
        back.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    fun updateUI(track: Track) {
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

    fun initViews() {
        image = findViewById(R.id.imageTrack)
        artist = findViewById(R.id.ArtistName)
        trackName = findViewById(R.id.TrackName)
        longing = findViewById(R.id.longing2)
        year = findViewById(R.id.year2)
        type = findViewById(R.id.type2)
        country = findViewById(R.id.country2)
        albom = findViewById(R.id.albom2)
        back = findViewById(R.id.backButton)
        time = findViewById(R.id.time)
        playButton = findViewById(R.id.playButton)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.observePlayerState().value == MediaPlayerViewModel.Companion.STATE_PLAYING) {
            viewModel.onPlayButtonClicked()
        }
    }
}
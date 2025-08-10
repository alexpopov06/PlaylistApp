package com.practicum.playlistapp.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.practicum.playlistapp.Creator
import com.practicum.playlistapp.R
import com.practicum.playlistapp.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.domain.media.MediaPlayerInteractor
import com.practicum.playlistapp.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediatekActivity : AppCompatActivity(), MediaPlayerInteractor.PlayerListener {
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
    private lateinit var url: String
    lateinit var handler: Handler
    private lateinit var player: MediaPlayerInteractor

//    companion object {
//        private const val STATE_DEFAULT = 0
//        private const val STATE_PREPARED = 1
//        private const val STATE_PLAYING = 2
//        private const val STATE_PAUSED = 3
//    }

//    private var playerState = STATE_DEFAULT

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
        player = Creator.createMediaPlayerInteractor()
        player.setListener(this)
        initViews()
        back.setOnClickListener {
            finish()
        }
        val trackJson = intent.getStringExtra("TRACK_EXTRA")
        val track = Gson().fromJson(trackJson, Track::class.java)
        updateUI(track)
        url = track.previewUrl
        player.preparePlayer(url) //keymoment

        playButton.setOnClickListener {
            player.playbackControl() //
        }


    }
    fun updateUI(track: Track){
        artist.text = track.artistName
        trackName.text = track.trackName
        albom.text = track.collectionName
        val milliseconds = track.trackTimeMillis
       longing.text = timeTrack.format(Date(milliseconds))
        year.text = track.releaseDate?.substring(0, 4)
        type.text = track.primaryGenreName
        country.text= track.country
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholdersvg)
            .into(image)

    }
    fun initViews(){
        image = findViewById<ImageView>(R.id.imageTrack)
        artist = findViewById<TextView>(R.id.ArtistName)
        trackName = findViewById<TextView>(R.id.TrackName)
        longing = findViewById<TextView>(R.id.longing2)
        year = findViewById<TextView>(R.id.year2)
        type = findViewById<TextView>(R.id.type2)
        country = findViewById<TextView>(R.id.country2)
        albom = findViewById<TextView>(R.id.albom2)
        back = findViewById<ImageButton>(R.id.backButton)
        time = findViewById<TextView>(R.id.time)
        playButton = findViewById<ImageView>(R.id.playButton)
        handler = Handler(Looper.getMainLooper())

    }
    private val progresRun = object: Runnable{
        override fun run() {
            time.setText(
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.getCurrentPosition())
            )
            handler.postDelayed(this, 500)
        }
    }

//    private fun preparePlayer() {
//        mediaPlayer.setDataSource(url)
//        mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener {
//            playButton.isEnabled = true
//            playerState = STATE_PREPARED
//        }
//        mediaPlayer.setOnCompletionListener {
//            playButton.setImageResource(R.drawable.play)
//            playerState = STATE_PREPARED
//            handler.removeCallbacks(progresRun)
//            time.setText("0:00")
//
//        }
//    }

    override fun onPrepared() {
        playButton.isEnabled = true
    }

    override fun onPlaybackCompleted() {
        playButton.setImageResource(R.drawable.play)
        handler.removeCallbacks(progresRun)
        time.setText("0:00")
    }
//    private fun startPlayer() {
//        mediaPlayer.start()/////
//        playButton.setImageResource(R.drawable.pause)
//        playerState = STATE_PLAYING/////
//        handler.postDelayed(progresRun, 500)
//    }

    override fun startingPlayer() {
        playButton.setImageResource(R.drawable.pause)
        handler.postDelayed(progresRun, 500)
    }

//    private fun pausePlayer() {
//        mediaPlayer.pause() ///
//        playButton.setImageResource(R.drawable.play)
//        playerState = STATE_PAUSED ///
//        handler.removeCallbacks(progresRun)
//    }

    override fun pausingPlayer() {
        playButton.setImageResource(R.drawable.play)
        handler.removeCallbacks(progresRun)
    }
//    private fun playbackControl() { //убрать
//        when(playerState) {
//            STATE_PLAYING -> {
//                pausePlayer()
//            }
//            STATE_PREPARED, STATE_PAUSED -> {
//                startPlayer()
//            }
//        }
//    }
    override fun onPause() {
        super.onPause()
        player.pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        player.releasePlayer()
    }


}
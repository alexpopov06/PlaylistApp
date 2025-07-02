package com.practicum.playlistapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediatek)
        initViews()
        back.setOnClickListener {
            finish()
        }
        val trackJson = intent.getStringExtra("TRACK_EXTRA")
        val track = Gson().fromJson(trackJson, Track::class.java)
        updateUI(track)




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

    }



}
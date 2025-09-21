package com.practicum.playlistapp.search.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackAdapter(
    var tracks: List<Track>,
    private val addToHistoryUseCase: AddToHistoryUseCase,
    private val delayDebounce: () -> Boolean,
    private val gson: Gson
): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view, addToHistoryUseCase, delayDebounce, gson)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(
        itemView: View,
        private val addToHistoryUseCase: AddToHistoryUseCase,
        private val delayDebounce: () -> Boolean,
        private val gson: Gson //
    ): RecyclerView.ViewHolder(itemView) {

        private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
        private val trackName: TextView = itemView.findViewById(R.id.nameTrack)
        private val groupName: TextView = itemView.findViewById(R.id.nameGroup)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val timeTrack = SimpleDateFormat("mm:ss", Locale.getDefault())

        fun bind(item: Track) {
            trackName.text = item.trackName
            groupName.text = item.artistName
            val milliseconds = item.trackTimeMillis.toLong()
            trackTime.text = timeTrack.format(Date(milliseconds))

            Glide.with(itemView).load(item.artworkUrl100)
                .centerCrop().transform(RoundedCorners(10))
                .placeholder(R.drawable.image_track)
                .into(imageTrack)

            itemView.setOnClickListener {
                try {
                    if (delayDebounce()) {
                        addToHistoryUseCase.execute(item)

                        SearchActivity.moveToMediatek(itemView.context, item, gson)
                    }
                } catch (e: Exception) {
                    Toast.makeText(itemView.context, "Ошибка открытия трека", Toast.LENGTH_SHORT).show()
                    Log.e("TrackAdapter", "Error opening track", e)
                }
            }
        }
    }
}
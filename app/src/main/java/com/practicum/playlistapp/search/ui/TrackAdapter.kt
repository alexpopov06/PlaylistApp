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
    private val gson: Gson,
    private val onTrackClick: (Track) -> Unit
): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        Log.d("TrackAdapter", "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(
            view,
            addToHistoryUseCase,
            delayDebounce,
            gson,
            onTrackClick
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        Log.d("TrackAdapter", "onBindViewHolder position: $position, track: ${tracks[position].trackName}")
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        Log.d("TrackAdapter", "getItemCount: ${tracks.size}")
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        Log.d("TrackAdapter", "updateTracks: ${newTracks.size} tracks")
        tracks = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(
        itemView: View,
        private val addToHistoryUseCase: AddToHistoryUseCase,
        private val delayDebounce: () -> Boolean,
        private val gson: Gson,
        private val onTrackClick: (Track) -> Unit
    ): RecyclerView.ViewHolder(itemView) {

        private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
        private val trackName: TextView = itemView.findViewById(R.id.nameTrack)
        private val groupName: TextView = itemView.findViewById(R.id.nameGroup)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val timeTrack = SimpleDateFormat("mm:ss", Locale.getDefault())

        fun bind(item: Track) {
            Log.d("TrackAdapter", "Binding track: ${item.trackName}")

            trackName.text = item.trackName
            groupName.text = item.artistName
            val milliseconds = item.trackTimeMillis.toLong()
            trackTime.text = timeTrack.format(Date(milliseconds))

            Glide.with(itemView).load(item.artworkUrl100)
                .centerCrop().transform(RoundedCorners(10))
                .placeholder(R.drawable.image_track)
                .into(imageTrack)

            itemView.setOnClickListener {
                Log.d("TrackAdapter", "CLICK DETECTED on track: ${item.trackName}")
                try {
                    val debounceResult = delayDebounce()
                    Log.d("TrackAdapter", "Debounce result: $debounceResult")

                    if (debounceResult) {
                        Log.d("TrackAdapter", "Executing click logic")
                        addToHistoryUseCase.execute(item)
                        Log.d("TrackAdapter", "Calling onTrackClick callback")
                        onTrackClick(item)
                    } else {
                        Log.d("TrackAdapter", "Click blocked by debounce")
                    }
                } catch (e: Exception) {
                    Log.e("TrackAdapter", "Error in click handler", e)
                    Toast.makeText(itemView.context, "Ошибка открытия трека", Toast.LENGTH_SHORT).show()
                }
            }


            itemView.isClickable = true
            itemView.isFocusable = true
            Log.d("TrackAdapter", "View clickable: ${itemView.isClickable}, focusable: ${itemView.isFocusable}")
        }
    }

    companion object {
        private const val TAG = "TrackAdapter"
    }
}
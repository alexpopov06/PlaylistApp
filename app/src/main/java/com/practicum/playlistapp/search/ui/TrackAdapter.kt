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
    private val addToHistoryUseCase: AddToHistoryUseCase? = null,
    private val delayDebounce: () -> Boolean,
    private val gson: Gson,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)

        return TrackViewHolder(
            view,
            addToHistoryUseCase,
            delayDebounce,
            gson,
            onTrackClick,
            onTrackLongClick
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(
        itemView: View,
        private val addToHistoryUseCase: AddToHistoryUseCase?,
        private val delayDebounce: () -> Boolean,
        private val gson: Gson,
        private val onTrackClick: (Track) -> Unit,
        private val onTrackLongClick: ((Track) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
        private val trackName: TextView = itemView.findViewById(R.id.nameTrack)
        private val groupName: TextView = itemView.findViewById(R.id.nameGroup)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

        fun bind(item: Track) {
            trackName.text = item.trackName
            groupName.text = item.artistName

            trackTime.text = timeFormat.format(Date(item.trackTimeMillis))

            val artUrl = item.artworkUrl100.takeIf { it.isNotBlank() }
            Glide.with(itemView)
                .load(artUrl)
                .transform(RoundedCorners(10))
                .placeholder(R.drawable.placeholdersvg)
                .error(R.drawable.placeholdersvg)
                .into(imageTrack)

            itemView.setOnClickListener {
                try {
                    if (delayDebounce()) {


                        addToHistoryUseCase?.execute(item)

                        onTrackClick(item)
                    }
                } catch (e: Exception) {
                    Toast.makeText(itemView.context, "Ошибка открытия трека", Toast.LENGTH_SHORT).show()
                    Log.e("TrackAdapter", "Error: ${e.message}")
                }
            }

            itemView.setOnLongClickListener(
                onTrackLongClick?.let { longClick ->
                    View.OnLongClickListener {
                        longClick(item)
                        true
                    }
                }
            )
        }
    }
}

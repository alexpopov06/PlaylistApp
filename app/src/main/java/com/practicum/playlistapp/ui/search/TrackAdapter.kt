package com.practicum.playlistapp.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.practicum.playlistapp.R
import com.practicum.playlistapp.ui.history.SearchHistory
import com.practicum.playlistapp.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackAdapter(private val tlist:List<Track>, private val searchHistory: SearchHistory, private val delayDebounce:()-> Boolean):RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view, searchHistory, delayDebounce)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tlist[position])
    }

    override fun getItemCount(): Int {
        return tlist.size
    }

    class TrackViewHolder(itemView: View, private val searchHistory: SearchHistory, private val delayDebounce:()-> Boolean ): RecyclerView.ViewHolder(itemView){
        private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)
        private val trackName: TextView = itemView.findViewById(R.id.nameTrack)
        private val groupName: TextView = itemView.findViewById(R.id.nameGroup)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        val timeTrack = SimpleDateFormat("mm:ss", Locale.getDefault())
        fun bind(item: Track){
            trackName.text=item.trackName
            groupName.text=item.artistName
            val milliseconds = item.trackTimeMillis.toLong()
           trackTime.text = timeTrack.format(Date(milliseconds))
            Glide.with(itemView).load(item.artworkUrl100)
                .centerCrop().transform(RoundedCorners(10))
                .placeholder(R.drawable.image_track)
                .into(imageTrack)
            itemView.setOnClickListener {
                try {
                    if (delayDebounce()){
                        searchHistory.AddTrackToHistory(item)
                        SearchActivity.moveToMediatek(itemView.context, item)
                    }


                } catch (e: Exception) {
                    Toast.makeText(itemView.context, "Ошибка открытия трека", Toast.LENGTH_SHORT).show()
                    Log.e("TrackAdapter", "Error opening track", e)
                }
            }
        }
    }
}
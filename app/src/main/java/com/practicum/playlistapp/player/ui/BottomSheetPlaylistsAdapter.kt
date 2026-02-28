package com.practicum.playlistapp.player.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistapp.R
import com.practicum.playlistapp.playlist.domain.model.Playlist
import java.io.File

class BottomSheetPlaylistsAdapter(
    private var playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistsAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun submitList(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(
        itemView: View,
        private val onClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cover: ImageView = itemView.findViewById(R.id.cover)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val count: TextView = itemView.findViewById(R.id.count)

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            count.text = formatCount(playlist.trackCount)

            val model = playlist.coverPath?.let { File(it) }
            Glide.with(itemView)
                .load(model)
                .transform(RoundedCorners(8))
                .placeholder(R.drawable.placeholdersvg)
                .error(R.drawable.placeholdersvg)
                .into(cover)

            itemView.setOnClickListener { onClick(playlist) }
        }

        private fun formatCount(trackCount: Int): String {
            val lastTwo = trackCount % 100
            val last = trackCount % 10
            val word = when {
                lastTwo in 11..14 -> "треков"
                last == 1 -> "трек"
                last in 2..4 -> "трека"
                else -> "треков"
            }
            return "$trackCount $word"
        }
    }
}


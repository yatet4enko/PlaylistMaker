package com.practicum.playlistmaker.features.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.features.media.ui.models.PlaylistVO

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val tracks: TextView = itemView.findViewById(R.id.playlist_tracks)
    private val artwork: ImageView = itemView.findViewById(R.id.playlist_artwork)

    fun bind(playlist: PlaylistVO) {
        name.text = playlist.name
        tracks.text = itemView.context.resources.getQuantityString(
            R.plurals.n_tracks,
            playlist.trackIds.size,
            playlist.trackIds.size,
        )

        playlist.artworkUri?.let {
            artwork.setImageURI(it)
        }
    }
}
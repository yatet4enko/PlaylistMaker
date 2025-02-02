package com.practicum.playlistmaker.features.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.features.media.ui.models.PlaylistVO

class PlaylistsAdapter(
    private val clickListener: PlaylistClickListener,
): RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {
    val playlists = ArrayList<PlaylistVO>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return PlaylistsViewHolder(
            PlaylistViewBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    inner class PlaylistsViewHolder(private val binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: PlaylistVO) {
            binding.name.text = playlist.name
            binding.tracks.text = itemView.context.resources.getQuantityString(
                R.plurals.n_tracks,
                playlist.trackIds.size,
                playlist.trackIds.size,
            )

            playlist.artworkUri?.let {
                binding.artwork.setImageURI(it)
            }

            itemView.setOnClickListener {
                clickListener.onClick(playlist)
            }
        }
    }

    fun interface PlaylistClickListener {
        fun onClick(playlist: PlaylistVO)
    }
}

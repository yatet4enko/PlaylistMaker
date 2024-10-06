package com.practicum.playlistmaker.features.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.features.search.domain.models.Track

class SearchResultsAdapter(
    private val tracks: List<Track>,
    private val clickListener: TrackClickListener,
): RecyclerView.Adapter<SearchResultsAdapter.SearchResultsItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultsItemViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.track_view, parent, false)

        return SearchResultsItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: SearchResultsItemViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    inner class SearchResultsItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val artwork: ImageView = itemView.findViewById(R.id.track_artwork)
        private val title: TextView = itemView.findViewById(R.id.track_title)
        private val artist: TextView = itemView.findViewById(R.id.track_artist)
        private val duration: TextView = itemView.findViewById(R.id.track_duration)

        fun bind(trackDto: Track) {
            title.text = trackDto.trackName
            artist.text = "${trackDto.artistName}"
            duration.text = "  •  ${trackDto.trackTime}"

            itemView.setOnClickListener {
                clickListener.onTrackClick(trackDto)
            }

            Glide
                .with(itemView)
                .load(trackDto.artworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .error(R.drawable.track_placeholder)
                .fitCenter()
                .transform(RoundedCorners(dpToPx(2F, itemView.context)))
                .into(artwork)
        }
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

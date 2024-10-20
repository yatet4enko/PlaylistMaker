package com.practicum.playlistmaker.features.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.features.search.domain.models.Track

class SearchResultsAdapter(
    private val clickListener: TrackClickListener,
): RecyclerView.Adapter<SearchResultsAdapter.SearchResultsItemViewHolder>() {
    val tracks = ArrayList<Track>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultsItemViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return SearchResultsItemViewHolder(
            TrackViewBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: SearchResultsItemViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    inner class SearchResultsItemViewHolder(private val binding: TrackViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(trackDto: Track) {
            binding.trackTitle.text = trackDto.trackName
            binding.trackArtist.text = "${trackDto.artistName}"
            binding.trackDuration.text = "  •  ${trackDto.trackTime}"

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
                .into(binding.trackArtwork)
        }
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

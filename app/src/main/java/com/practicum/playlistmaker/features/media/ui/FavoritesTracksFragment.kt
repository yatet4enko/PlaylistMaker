package com.practicum.playlistmaker.features.media.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.practicum.playlistmaker.features.media.ui.models.FavoriteTracksStateVO
import org.koin.androidx.viewmodel.ext.android.viewModel

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.gson.Gson
import com.practicum.playlistmaker.features.player.ui.PlayerActivity
import com.practicum.playlistmaker.features.player.ui.PlayerActivity.Companion.TRACK_PARAM
import com.practicum.playlistmaker.features.search.ui.SearchResultsAdapter

class FavoritesTracksFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesTracksBinding

    private val viewModel: FavoritesTracksViewModel by viewModel()

    private val gson = Gson()

    private val favoriteTracksAdapter = SearchResultsAdapter {
        viewModel.onTrackClick(it)
    }

    // Костыли для актуализации данных о добавленных в избранные
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteTracksItems.adapter = favoriteTracksAdapter

        viewModel.state.observe(viewLifecycleOwner) {
            renderContent(it)
        }

        viewModel.observeNavigateTrack().observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)

            intent.putExtra(TRACK_PARAM, gson.toJson(it))

            startActivity(intent)
        }
    }

    private fun renderContent(state: FavoriteTracksStateVO) {
        binding.empty.visibility = if (state == FavoriteTracksStateVO.Empty) VISIBLE else INVISIBLE
        binding.favoriteTracksItems.visibility = if (state == FavoriteTracksStateVO.Empty) INVISIBLE else VISIBLE

        if (state is FavoriteTracksStateVO.Default) {
            favoriteTracksAdapter.tracks.clear()
            favoriteTracksAdapter.tracks.addAll(state.tracks)
            favoriteTracksAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance() =
            FavoritesTracksFragment()
    }
}
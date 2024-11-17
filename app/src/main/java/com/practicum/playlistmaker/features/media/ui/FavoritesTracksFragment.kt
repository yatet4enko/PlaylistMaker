package com.practicum.playlistmaker.features.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesTracksBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesTracksBinding

    private val viewModel: FavoritesTracksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance() =
            FavoritesTracksFragment()
    }
}
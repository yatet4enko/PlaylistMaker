package com.practicum.playlistmaker.features.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {

    private lateinit var binding: FragmentMediaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
    }

    private fun setupTabs() {
        binding.viewPager.adapter = MediaViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.favorites_tracks)
                1 -> tab.text = resources.getString(R.string.playlists)
            }
        }

        tabMediator.attach()
    }
}
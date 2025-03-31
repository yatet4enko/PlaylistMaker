package com.practicum.playlistmaker.features.media.ui

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment: CreatePlaylistFragment() {
    private var id: Int? = null

    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getInt(ARG_ID)
        }

        id?.let {
            viewModel.loadPlaylist(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.text = getString(R.string.save)
        binding.toolbar.title = getString(R.string.edit)

        viewModel.observeInitForm().observe(viewLifecycleOwner) {
            binding.nameEditText.setText(it.name)
            binding.descriptionEditText.setText(it.description)
            binding.artwork.setImageURI(it.artworkUri)
            binding.submitButton.isEnabled = true
        }
    }

    override fun onBackClick() {
        goBack()
    }

}
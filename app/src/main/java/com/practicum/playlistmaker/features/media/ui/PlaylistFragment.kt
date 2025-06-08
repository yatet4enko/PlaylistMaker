package com.practicum.playlistmaker.features.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.features.player.ui.PlayerActivity
import com.practicum.playlistmaker.features.player.ui.PlayerActivity.Companion.TRACK_PARAM
import com.practicum.playlistmaker.features.search.domain.models.Track
import com.practicum.playlistmaker.features.search.ui.SearchResultsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ARG_ID = "id"

class PlaylistFragment : Fragment() {
    private var id: Int? = null

    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel: PlaylistViewModel by viewModel()

    private val gson = Gson()

    private val tracksAdapter = SearchResultsAdapter(
        clickListener = {
            val intent = Intent(requireContext(), PlayerActivity::class.java)

            intent.putExtra(TRACK_PARAM, gson.toJson(it))

            startActivity(intent)
        },
        longClickListener = this::onTrackLongClick
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_ID)
        }

        id?.let {
            viewModel.loadPlaylist(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val moreSheet = BottomSheetBehavior.from(binding.moreBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.tracks.adapter = tracksAdapter

        binding.shareIcon.setOnClickListener {
            viewModel.onSharePlaylistClick()
        }

        binding.moreIcon.setOnClickListener {
            moreSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playlistShare.setOnClickListener {
            viewModel.onSharePlaylistClick()
        }

        binding.playlistEdit.setOnClickListener {
            onEditClick()
        }

        binding.playlistDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.want_to_remove_playlist, viewModel.data.value?.name))
                .setNegativeButton(requireContext().getString(R.string.no)) { dialog, which ->

                }.setPositiveButton(requireContext().getString(R.string.yes)) { dialog, which ->
                    viewModel.onDeletePlaylistClick()
                }
                .show()
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            if (data == null) {
                return@observe
            }

            binding.playlistName.text = data.name
            binding.playlistYear.text = data.year
            binding.playlistChtotoZa4emto.text = "${data.minutesDescription} • ${data.tracksDescription}"
            binding.artwork.setImageURI(data.artworkUri)

            binding.playlistArtwork.setImageURI(data.artworkUri)
            binding.playlistTitle.text = data.name
            binding.playlistTracks.text = data.tracksDescription

            fulfillTracks(data.tracks)
        }

        viewModel.observeShowShareDialog().observe(viewLifecycleOwner) { message ->
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");

            val result = runCatching {
                startActivity(intent)
            }

            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.open_messenger_error),
                    Toast.LENGTH_LONG,
                ).show();
            }
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { message ->
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG,
            ).show();
        }

        viewModel.observeClose().observe(viewLifecycleOwner) {
            goBack()
        }
    }

    private fun fulfillTracks(state: List<Track>) {
        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(state)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun onTrackLongClick(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.want_to_remove_track))
            .setNegativeButton(requireContext().getString(R.string.no)) { dialog, which ->

            }.setPositiveButton(requireContext().getString(R.string.yes)) { dialog, which ->
                deleteTrack(track)
            }
            .show()
    }

    private fun deleteTrack(track: Track) {
        viewModel.onDeleteTrack(track)
    }

    private fun onEditClick() {
        id?.let {
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                Bundle().apply {
                    putInt(ARG_ID, it)
                }
            )
        }
    }

    private fun goBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    companion object {
        fun newInstance(id: Int) =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                }
            }
    }
}
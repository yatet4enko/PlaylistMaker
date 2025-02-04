package com.practicum.playlistmaker.features.media.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private lateinit var backConfirmDialog: MaterialAlertDialogBuilder

    private var isActivityClose = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        backConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.finish_create_playlist_title))
            .setMessage(requireContext().getString(R.string.finish_create_playlist_message))
            .setNegativeButton(requireContext().getString(R.string.finish_create_playlist_no)) { dialog, which ->

            }.setPositiveButton(requireContext().getString(R.string.finish_create_playlist_yes)) { dialog, which ->
                goBack()
            }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbarUI()
        initTextEditUI()
        initButtonUI()
        initPickImage()
        initSubmitUI()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isFormEmpty() || isActivityClose) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    backConfirmDialog.show()
                }
            }
        })

        viewModel.state.observe(viewLifecycleOwner) {
            binding.artwork.setImageURI(it.artworkUri)

            if (it.isSubmitButtonDisabled) {
                binding.submitButton.isEnabled = false
                binding.submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
            } else {
                binding.submitButton.isEnabled = true
                binding.submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
        }

        viewModel.observeShowSuccessToast().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.playlist_created, it), Toast.LENGTH_LONG).show()
        }

        viewModel.observeClose().observe(viewLifecycleOwner) {
            goBack()
        }
    }

    private fun initToolbarUI() {
        binding.toolbar.setNavigationIcon(R.drawable.back_icon)
        binding.toolbar.setNavigationOnClickListener {
            if (viewModel.isFormEmpty()) {
                goBack()
            } else {
                backConfirmDialog.show()
            }
        }
    }

    private fun initTextEditUI() {
        binding.nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nameFocusedLabel.visibility = View.VISIBLE
                binding.nameEditText.hint = ""
            } else {
                binding.nameFocusedLabel.visibility = View.GONE
                binding.nameEditText.hint = requireContext().getString(R.string.playlist_name)
            }
        }

        binding.descriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.descriptionFocusedLabel.visibility = View.VISIBLE
                binding.descriptionEditText.hint = ""
            } else {
                binding.descriptionFocusedLabel.visibility = View.GONE
                binding.descriptionEditText.hint = requireContext().getString(R.string.playlist_description)
            }
        }
    }

    private fun initButtonUI() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()

                viewModel.onChangeName(value)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()

                viewModel.onChangeDescription(value)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initPickImage() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.onPickImage(uri)
                }
            }

        binding.artwork.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun goBack() {
        val navController = try {
            findNavController()
        } catch (e: IllegalStateException) {
            null
        }

        if (navController != null) {
            navController.popBackStack()
        } else {
            isActivityClose = true
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initSubmitUI() {
        binding.submitButton.setOnClickListener {
            viewModel.onSubmit()
        }
    }
}
package com.practicum.playlistapp.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistapp.R
import com.practicum.playlistapp.playlist.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val playlistId: Long by lazy {
        requireArguments().getLong(ARG_PLAYLIST_ID)
    }

    override val formViewModel: EditPlaylistViewModel by viewModel { parametersOf(playlistId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = getString(R.string.playlist_edit_title)
        binding.createButton.text = getString(R.string.playlist_save)

        if (savedInstanceState == null) {
            formViewModel.observeInitialPlaylist().observe(viewLifecycleOwner) { playlist ->
                binding.titleEditText.setText(playlist.name)
                binding.descriptionEditText.setText(playlist.description.orEmpty())
                val path = playlist.coverPath
                val file = path?.let { File(it) }
                if (file != null && file.exists()) {
                    binding.coverImage.visibility = View.VISIBLE
                    binding.addCoverIcon.visibility = View.GONE
                    Glide.with(this)
                        .load(file)
                        .centerCrop()
                        .into(binding.coverImage)
                } else {
                    binding.coverImage.visibility = View.GONE
                    binding.addCoverIcon.visibility = View.VISIBLE
                }
                binding.createButton.isEnabled = playlist.name.isNotBlank()
            }
        }
    }

    override fun onSubmit(name: String, description: String?, coverUri: Uri?) {
        formViewModel.savePlaylist(name, description, coverUri)
    }

    override fun onFormFinished(name: String) {
        findNavController().navigateUp()
    }

    override fun handleClose() {
        findNavController().navigateUp()
    }

    override fun shouldConfirmDiscard(): Boolean = false

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}

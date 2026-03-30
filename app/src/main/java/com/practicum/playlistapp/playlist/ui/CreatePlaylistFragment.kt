package com.practicum.playlistapp.playlist.ui

import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistapp.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistapp.playlist.presentation.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {

    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    private var coverUri: Uri? = null
    private var isFinished = false

    protected open val formViewModel: CreatePlaylistViewModel by viewModel()

    private val pickCover =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverUri = uri
                renderCover(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            val savedCover = savedInstanceState.getString("cover_uri")
            if (savedCover != null) {
                coverUri = Uri.parse(savedCover)
                renderCover(coverUri!!)
            }
        }

        binding.toolbar.setNavigationOnClickListener { handleClose() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        binding.coverContainer.setOnClickListener {
            pickCover.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.titleEditText.addTextChangedListener(textWatcher)
        binding.descriptionEditText.addTextChangedListener(textWatcher)

        binding.createButton.isEnabled = false
        binding.createButton.setOnClickListener {
            val name = binding.titleEditText.text?.toString().orEmpty()
            val description = binding.descriptionEditText.text?.toString()
            onSubmit(name, description, coverUri)
        }

        formViewModel.observeFinished().observe(viewLifecycleOwner) { name ->
            isFinished = true
            onFormFinished(name)
        }
    }

    protected open fun onSubmit(name: String, description: String?, coverUri: Uri?) {
        formViewModel.createPlaylist(name, description, coverUri)
    }

    protected open fun onFormFinished(name: String) {
        Toast.makeText(requireContext(), "Плейлист $name создан", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleClose()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.createButton.isEnabled = !binding.titleEditText.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    protected fun renderCover(uri: Uri) {
        binding.coverImage.visibility = View.VISIBLE
        binding.addCoverIcon.visibility = View.GONE

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.coverImage)
    }

    protected open fun handleClose() {
        if (!isFinished && shouldConfirmDiscard() && hasUnsavedChanges()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Завершить") { _, _ ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    protected open fun shouldConfirmDiscard(): Boolean = true

    protected open fun hasUnsavedChanges(): Boolean {
        return coverUri != null ||
            !binding.titleEditText.text.isNullOrBlank() ||
            !binding.descriptionEditText.text.isNullOrBlank()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("cover_uri", coverUri?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

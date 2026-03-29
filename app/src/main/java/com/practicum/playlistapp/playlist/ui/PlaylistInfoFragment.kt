package com.practicum.playlistapp.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.FragmentPlaylistInfoBinding
import com.practicum.playlistapp.databinding.PlaylistOptionsBottomSheetBinding
import com.practicum.playlistapp.player.ui.PlayerFragment
import com.practicum.playlistapp.playlist.domain.model.Playlist
import com.practicum.playlistapp.playlist.presentation.PlaylistInfoViewModel
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.ui.TrackAdapter
import java.io.File
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val gson: Gson by inject()

    private val playlistId: Long by lazy {
        requireArguments().getLong(ARG_PLAYLIST_ID)
    }

    private val viewModel: PlaylistInfoViewModel by viewModel { parametersOf(playlistId) }

    private var lastDurationText: String = ""
    private var lastTrackCount: Int = 0

    private lateinit var tracksAdapter: TrackAdapter

    private var optionsBottomSheetDialog: BottomSheetDialog? = null

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()
        setupTracksBottomSheet()

        binding.backButton.bringToFront()
        ViewCompat.setElevation(binding.backButton, 12f)

        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            addToHistoryUseCase = null,
            delayDebounce = { true },
            gson = gson,
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistInfoFragment_to_playerFragment,
                    PlayerFragment.createArgs(track, gson)
                )
            },
            onTrackLongClick = { track -> showDeleteTrackDialog(track) }
        )
        binding.tracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecycler.adapter = tracksAdapter

        binding.playlistMeta.text = "00 минут · ${formatTrackCount(0)}"

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            if (playlist == null) return@observe
            currentPlaylist = playlist
            lastTrackCount = playlist.trackCount
            binding.playlistTitle.text = playlist.name
            if (!playlist.description.isNullOrBlank()) {
                binding.playlistDescription.text = playlist.description
                binding.playlistDescription.visibility = View.VISIBLE
            } else {
                binding.playlistDescription.visibility = View.GONE
            }
            renderCover(playlist.coverPath)
            updatePlaylistMeta()
        }

        viewModel.observeTotalDurationText().observe(viewLifecycleOwner) { durationText ->
            lastDurationText = durationText
            updatePlaylistMeta()
        }

        viewModel.observeTracks().observe(viewLifecycleOwner) { list ->
            currentTracks = list
            tracksAdapter.updateTracks(list)
            if (list.isEmpty()) {
                binding.emptyTracksMessage.visibility = View.VISIBLE
                binding.tracksRecycler.visibility = View.GONE
            } else {
                binding.emptyTracksMessage.visibility = View.GONE
                binding.tracksRecycler.visibility = View.VISIBLE
            }
        }

        viewModel.observePlaylistDeleted().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener { sharePlaylist() }
        binding.menuButton.setOnClickListener { openOptionsMenu() }

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backCallback
        )
    }

    private fun openOptionsMenu() {
        val playlist = currentPlaylist ?: return
        optionsBottomSheetDialog?.dismiss()
        val sheetBinding = PlaylistOptionsBottomSheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()).apply {
            setContentView(sheetBinding.root)
            behavior.isHideable = true
            setOnDismissListener { optionsBottomSheetDialog = null }
        }
        optionsBottomSheetDialog = dialog
        bindMenuHeader(sheetBinding, playlist)
        sheetBinding.menuActionShare.setOnClickListener {
            dialog.dismiss()
            sharePlaylist()
        }
        sheetBinding.menuActionEdit.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle().apply {
                putLong(EditPlaylistFragment.ARG_PLAYLIST_ID, playlistId)
            }
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_editPlaylistFragment,
                bundle
            )
        }
        sheetBinding.menuActionDelete.setOnClickListener {
            dialog.dismiss()
            showDeletePlaylistDialog()
        }
        dialog.show()
    }

    private fun bindMenuHeader(sheetBinding: PlaylistOptionsBottomSheetBinding, playlist: Playlist) {
        sheetBinding.menuSheetTitle.text = playlist.name
        sheetBinding.menuSheetTrackCount.text = formatTrackCount(playlist.trackCount)
        val file = playlist.coverPath?.let { File(it) }
        if (file != null && file.exists()) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .into(sheetBinding.menuSheetCover)
        } else {
            sheetBinding.menuSheetCover.setImageResource(R.drawable.placeholdersvg)
        }
    }

    private fun sharePlaylist() {
        val playlist = currentPlaylist ?: return
        if (currentTracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                R.string.playlist_share_no_tracks,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val text = buildShareText(playlist, currentTracks)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(send, getString(R.string.playlist_share_chooser)))
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val sb = StringBuilder()
        sb.appendLine(playlist.name)
        sb.appendLine(playlist.description.orEmpty())
        sb.appendLine(formatTrackCount(playlist.trackCount))
        tracks.forEachIndexed { index, track ->
            val artist = track.artistName.orEmpty()
            val name = track.trackName.orEmpty()
            val dur = Track.formatMillisToMmSs(track.trackTimeMillis)
            sb.appendLine("${index + 1}. $artist - $name ($dur)")
        }
        return sb.toString().trimEnd()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_delete_playlist_title)
            .setMessage(R.string.playlist_delete_playlist_message)
            .setNegativeButton(R.string.playlist_delete_dialog_no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.playlist_delete_dialog_yes) { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePlaylist()
            }
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        val id = track.trackId ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.playlist_track_delete_message)
            .setNegativeButton(R.string.playlist_track_delete_no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.playlist_track_delete_yes) { dialog, _ ->
                dialog.dismiss()
                viewModel.removeTrackFromPlaylist(id)
            }
            .show()
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (optionsBottomSheetDialog?.isShowing == true) {
                optionsBottomSheetDialog?.dismiss()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, 0)
            val extraBottom = resources.getDimensionPixelSize(R.dimen.s8)
            binding.tracksSheetContent.updatePadding(bottom = systemBars.bottom + extraBottom)
            insets
        }
    }

    private fun setupTracksBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            isHideable = false
            isFitToContents = false
            peekHeight =
                resources.getDimensionPixelSize(R.dimen.playlist_tracks_bottom_sheet_peek)
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun renderCover(coverPath: String?) {
        val file = coverPath?.let { File(it) }
        binding.coverImage.visibility = View.VISIBLE

        if (file != null && file.exists()) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .into(binding.coverImage)
        } else {
            binding.coverImage.setImageResource(R.drawable.placeholdersvg)
        }
    }

    private fun updatePlaylistMeta() {
        val durationPart = if (lastDurationText.isEmpty()) "00" else lastDurationText
        binding.playlistMeta.text = "$durationPart минут · ${formatTrackCount(lastTrackCount)}"
    }

    private fun formatTrackCount(trackCount: Int): String {
        val lastTwo = trackCount % 100
        val last = trackCount % 10
        val word = when {
            lastTwo in 11..14 -> "треков"
            last == 1 -> "трек"
            last in 2..4 -> "трека"
            else -> "треков"
        }
        return "$trackCount $word"
    }

    override fun onDestroyView() {
        optionsBottomSheetDialog?.dismiss()
        optionsBottomSheetDialog = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}

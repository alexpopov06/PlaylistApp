package com.practicum.playlistapp.media

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.FragmentSecondBinding
import com.practicum.playlistapp.playlist.ui.PlaylistInfoFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private val viewModel: SecondFragmentViewModel by viewModel()
    private val adapter = PlaylistAdapter(emptyList()) { playlist ->
        val bundle = Bundle().apply {
            putLong(PlaylistInfoFragment.ARG_PLAYLIST_ID, playlist.id)
        }

        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistInfoFragment,
            bundle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        binding.newPlaylistButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)

            val isEmpty = playlists.isEmpty()
            binding.playlistsRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.emptyImage.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecycler() {
        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecycler.adapter = adapter

        if (binding.playlistsRecycler.itemDecorationCount == 0) {
            binding.playlistsRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val column = position % 2
                    if (column == 0) outRect.right = 4.dp else outRect.left = 4.dp

                    outRect.bottom = 16.dp
                }
            })
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}

package com.practicum.playlistapp.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.FragmentSecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SecondFragment: Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private val viewModel: SecondFragmentViewModel by viewModel()
    private val adapter = PlaylistAdapter(emptyList())
    companion object {
        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecycler.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)

            val isEmpty = playlists.isEmpty()
            binding.playlistsRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.emptyImage.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
}
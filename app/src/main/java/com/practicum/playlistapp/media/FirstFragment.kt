package com.practicum.playlistapp.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistapp.databinding.FragmentFirstBinding

class FirstFragment: Fragment() {

    private lateinit var binding: FragmentFirstBinding
    companion object {
        fun newInstance(): FirstFragment {
            return FirstFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
}
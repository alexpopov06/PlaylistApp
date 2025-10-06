package com.practicum.playlistapp.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistapp.databinding.FragmentSecondBinding

class SecondFragment: Fragment() {

    private lateinit var binding: FragmentSecondBinding
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
}
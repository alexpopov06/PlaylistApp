package com.practicum.playlistapp.main.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistapp.media.FirstFragment
import com.practicum.playlistapp.media.SecondFragment
import com.practicum.playlistapp.search.ui.FavoritesFragment

class FragmentsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> FavoritesFragment()
        else -> SecondFragment()

    }
}
package com.practicum.playlistapp.search.presentation

import SearchState


interface SearchView {
    fun render(state: SearchState)
    fun showClearButton(show: Boolean)
    fun clearSearchInput()
}

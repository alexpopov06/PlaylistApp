package com.practicum.playlistapp.ui.history

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistapp.domain.models.Track

class SearchHistory(val sharedPreferences: SharedPreferences) {
    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_ITEMS = 10
    }
    fun AddTrackToHistory(track: Track){
        val history = getHistory().toMutableList()
        val iterator = history.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().trackId == track.trackId) {
                iterator.remove()
            }
        }
        history.add(0, track)
        if (history.size > MAX_HISTORY_ITEMS){
            history.removeAt(history.size - 1)
        }
        sharedPreferences.edit()
            .putString(HISTORY_KEY, Gson().toJson(history))
            .apply()



    }
    fun getHistory():List<Track>{
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val arrayType = Array<Track>::class.java
        val array = Gson().fromJson(json, arrayType)
        return array?.toList() ?: emptyList()


    }
    fun clearHistory(){
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()

    }



}
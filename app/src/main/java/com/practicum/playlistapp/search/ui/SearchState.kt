import com.practicum.playlistapp.search.domain.model.Track

sealed class SearchState {

    data object Loading : SearchState()
    data class Content(val tracks: List<Track>, val showClearButton: Boolean = true) : SearchState()
    data class Empty(val showClearButton: Boolean = true) : SearchState()
    data class Error(val showClearButton: Boolean = true) : SearchState()
    data object NoWifi : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    data object Idle : SearchState()
}
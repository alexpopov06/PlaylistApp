import com.practicum.playlistapp.domain.models.Track

sealed interface SearchState {
    object Idle : SearchState
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    object Empty : SearchState
    object Error : SearchState
    object NoWifi : SearchState
    object History : SearchState
}

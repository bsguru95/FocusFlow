package com.focus.flow.presentation.state

import com.focus.flow.domain.model.Anime

data class AnimeDetailState(
    val anime: Anime? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVideoPlaying: Boolean = false
)

sealed class AnimeDetailEvent {
    object LoadAnime : AnimeDetailEvent()
    object ToggleFavorite : AnimeDetailEvent()
    object PlayTrailer : AnimeDetailEvent()
    object PauseTrailer : AnimeDetailEvent()
    object Retry : AnimeDetailEvent()
}

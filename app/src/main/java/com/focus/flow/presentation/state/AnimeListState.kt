package com.focus.flow.presentation.state

import com.focus.flow.domain.model.Anime

data class AnimeListState(
    val animeList: List<Anime> = emptyList(),
    val filteredAnimeList: List<Anime> = emptyList(),
    val availableGenres: List<String> = emptyList(),
    val selectedGenre: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

sealed class AnimeListEvent {
    object LoadAnime : AnimeListEvent()
    object RefreshAnime : AnimeListEvent()
    object ShuffleAnime : AnimeListEvent()
    data class FilterByGenre(val genre: String) : AnimeListEvent()
    data class OnAnimeClick(val anime: Anime) : AnimeListEvent()
    data class OnFavoriteToggle(val anime: Anime) : AnimeListEvent()
    object Retry : AnimeListEvent()
}

package com.focus.flow.domain.usecase

import com.focus.flow.domain.model.Anime
import com.focus.flow.domain.repository.AnimeRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(anime: Anime) {
        repository.toggleFavorite(anime)
    }
}

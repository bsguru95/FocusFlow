package com.focus.flow.domain.usecase

import com.focus.flow.domain.model.Anime
import com.focus.flow.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(page: Int): Flow<Result<List<Anime>>> {
        return repository.getTopAnime(page)
    }
}

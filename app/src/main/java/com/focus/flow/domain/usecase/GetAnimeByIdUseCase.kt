package com.focus.flow.domain.usecase

import com.focus.flow.domain.model.Anime
import com.focus.flow.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeByIdUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(id: Int): Flow<Result<Anime>> {
        return repository.getAnimeById(id)
    }
}

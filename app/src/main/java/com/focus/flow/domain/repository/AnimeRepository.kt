package com.focus.flow.domain.repository

import com.focus.flow.domain.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getTopAnime(page: Int): Flow<Result<List<Anime>>>
    fun getAnimeById(id: Int): Flow<Result<Anime>>
    fun getFavoriteAnime(): Flow<List<Anime>>
    suspend fun toggleFavorite(anime: Anime)
    suspend fun refreshAnimeData()
    suspend fun clearCache()
}

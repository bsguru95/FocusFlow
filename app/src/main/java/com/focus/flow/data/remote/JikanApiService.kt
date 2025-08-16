package com.focus.flow.data.remote

import com.focus.flow.domain.model.Anime
import com.focus.flow.domain.model.AnimeListResponse
import com.focus.flow.domain.model.AnimeDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface JikanApiService {
    @GET("top/anime")
    suspend fun getTopAnime(): AnimeListResponse
    
    @GET("anime/{id}")
    suspend fun getAnimeById(@Path("id") id: Int): AnimeDetailResponse
}

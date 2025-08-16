package com.focus.flow.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.focus.flow.domain.model.Anime
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime ORDER BY rank ASC")
    fun getAllAnime(): Flow<List<Anime>>
    
    @Query("SELECT * FROM anime ORDER BY rank ASC")
    fun getAnimePagingSource(): PagingSource<Int, Anime>
    
    @Query("SELECT * FROM anime WHERE id = :id")
    suspend fun getAnimeById(id: Int): Anime?
    
    @Query("SELECT * FROM anime WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteAnime(): Flow<List<Anime>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeList(animeList: List<Anime>)
    
    @Update
    suspend fun updateAnime(anime: Anime)
    
    @Query("UPDATE anime SET isFavorite = :isFavorite WHERE id = :animeId")
    suspend fun updateFavoriteStatus(animeId: Int, isFavorite: Boolean)
    
    @Query("DELETE FROM anime")
    suspend fun clearAllAnime()
    
    @Query("SELECT COUNT(*) FROM anime")
    suspend fun getAnimeCount(): Int
}

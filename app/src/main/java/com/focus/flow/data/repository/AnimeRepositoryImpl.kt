package com.focus.flow.data.repository

import com.focus.flow.data.local.AnimeDao
import com.focus.flow.data.remote.JikanApiService
import com.focus.flow.domain.model.Anime
import com.focus.flow.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import android.util.Log
import javax.inject.Inject

class AnimeRepositoryImpl @Inject constructor(
    private val api: JikanApiService,
    private val dao: AnimeDao
) : AnimeRepository {
    
    companion object {
        private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L // 1 hour in milliseconds
        private const val TAG = "AnimeRepositoryImpl"
    }
    
    private fun isCacheExpired(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val ageInMinutes = (currentTime - lastUpdated) / (60 * 1000)
        val isExpired = currentTime - lastUpdated > CACHE_EXPIRY_TIME
        Log.d(TAG, "Cache age: ${ageInMinutes} minutes, expired: $isExpired")
        return isExpired
    }
    
    override fun getTopAnime(page: Int): Flow<Result<List<Anime>>> = flow {
        Log.d(TAG, "getTopAnime() called - checking cache first")
        
        // First, check cached data
        var cachedAnime: List<Anime>? = null
        var shouldFetchFromApi = true
        
        try {
            cachedAnime = dao.getAllAnime().first()
            if (cachedAnime.isNotEmpty()) {
                val oldestItem = cachedAnime.minByOrNull { it.lastUpdated }
                val isCacheStale = oldestItem?.let { isCacheExpired(it.lastUpdated) } ?: true
                
                Log.d(TAG, "Found ${cachedAnime.size} cached anime items, cache stale: $isCacheStale")
                
                // Always emit cached data first for immediate UI response
                emit(Result.success(cachedAnime))
                
                // Only fetch from API if cache is stale
                shouldFetchFromApi = isCacheStale
                
                if (!shouldFetchFromApi) {
                    Log.d(TAG, "Cache is fresh, skipping API call")
                    return@flow // Exit early if cache is fresh
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "No cached data available: ${e.message}")
        }
        
        // Fetch from API if needed (cache is stale or empty)
        if (shouldFetchFromApi) {
            try {
                val response = api.getTopAnime()
                val animeList = response.data
                
                Log.d(TAG, "Fetching fresh data from API...")
                Log.d(TAG, "Successfully fetched ${animeList.size} anime items from API")
                animeList.take(3).forEach { anime ->
                    Log.d(TAG, "Sample anime: ${anime.displayTitle} with ID: ${anime.id}")
                }
                
                // Log any invalid IDs for debugging
                val invalidIds = animeList.filter { it.id <= 0 }
                if (invalidIds.isNotEmpty()) {
                    Log.w(TAG, "Found ${invalidIds.size} anime items with invalid IDs: ${invalidIds.map { "${it.displayTitle} (ID: ${it.id})" }}")
                }
                
                // Update local database with fresh data
                dao.clearAllAnime() // Clear old data
                dao.insertAnimeList(animeList) // Insert fresh data
                
                // Only emit if we don't have cached data (to prevent duplicate emissions)
                if (cachedAnime.isNullOrEmpty()) {
                    emit(Result.success(animeList))
                }
                Log.d(TAG, "Updated cache with fresh API data")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching top anime from API: ${e.message}", e)
                
                // If API fails and we don't have cached data, emit error
                if (cachedAnime.isNullOrEmpty()) {
                    Log.e(TAG, "No cached data available and API failed")
                    emit(Result.failure(e))
                } else {
                    Log.d(TAG, "API failed but using cached data")
                    // We already emitted cached data above, so no need to emit again
                }
            }
        }
    }
    
    override fun getAnimeById(id: Int): Flow<Result<Anime>> = flow {
        if (id <= 0) {
            Log.e("AnimeRepositoryImpl", "Attempted to fetch anime with invalid ID: $id")
            emit(Result.failure(IllegalArgumentException("Invalid anime ID: $id")))
            return@flow
        }
        
        // First, check cached data
        var cachedAnime: Anime? = null
        var shouldFetchFromApi = true
        
        try {
            cachedAnime = dao.getAnimeById(id)
            if (cachedAnime != null) {
                val isCacheStale = isCacheExpired(cachedAnime.lastUpdated)
                Log.d("AnimeRepositoryImpl", "Found cached anime by ID $id: ${cachedAnime.displayTitle}, cache stale: $isCacheStale")
                
                // Always emit cached data first for immediate UI response
                emit(Result.success(cachedAnime))
                
                // Only fetch from API if cache is stale
                shouldFetchFromApi = isCacheStale
            }
        } catch (e: Exception) {
            Log.d("AnimeRepositoryImpl", "No cached anime found for ID $id: ${e.message}")
        }
        
        // Fetch from API if needed (cache is stale or not found)
        if (shouldFetchFromApi) {
            try {
                val response = api.getAnimeById(id)
                val anime = response.data
                Log.d("AnimeRepositoryImpl", "Successfully fetched anime by ID $id from API: ${anime.displayTitle}")
                Log.d("AnimeRepositoryImpl", "Title fields - title: '${anime.title}', titleEnglish: '${anime.titleEnglish}', titleJapanese: '${anime.titleJapanese}'")
                
                // Update cache with fresh data
                dao.insertAnime(anime)
                
                // Emit fresh data (will update UI if different from cached)
                emit(Result.success(anime))
            } catch (e: Exception) {
                Log.e("AnimeRepositoryImpl", "Error fetching anime by ID from API: ${e.message}", e)
                
                // If API fails and we don't have cached data, emit error
                if (cachedAnime == null) {
                    Log.e("AnimeRepositoryImpl", "No cached anime available for ID $id and API failed")
                    emit(Result.failure(e))
                } else {
                    Log.d("AnimeRepositoryImpl", "API failed but using cached data for ID $id")
                    // We already emitted cached data above, so no need to emit again
                }
            }
        }
    }
    
    override fun getFavoriteAnime(): Flow<List<Anime>> {
        return dao.getFavoriteAnime()
    }
    
    override suspend fun toggleFavorite(anime: Anime) {
        val updatedAnime = anime.copy(isFavorite = !anime.isFavorite)
        dao.updateAnime(updatedAnime)
    }
    
    override suspend fun refreshAnimeData() {
        try {
            val response = api.getTopAnime()
            dao.insertAnimeList(response.data)
        } catch (e: Exception) {
            // Handle refresh error
            throw e
        }
    }
    
    override suspend fun clearCache() {
        dao.clearAllAnime()
    }
}

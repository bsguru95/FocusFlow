package com.focus.flow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.flow.domain.usecase.GetTopAnimeUseCase
import com.focus.flow.domain.usecase.ToggleFavoriteUseCase
import com.focus.flow.presentation.state.AnimeListEvent
import com.focus.flow.presentation.state.AnimeListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val getTopAnimeUseCase: GetTopAnimeUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "AnimeListViewModel"
    }
    
    private val _state = MutableStateFlow(AnimeListState())
    val state: StateFlow<AnimeListState> = _state.asStateFlow()
    
    init {
        Log.d(TAG, "ViewModel initialized - calling loadAnime()")
        loadAnime()
    }
    
    fun onEvent(event: AnimeListEvent) {
        when (event) {
            is AnimeListEvent.LoadAnime -> loadAnime()
            is AnimeListEvent.RefreshAnime -> refreshAnime()
            is AnimeListEvent.ShuffleAnime -> shuffleAnime()
            is AnimeListEvent.FilterByGenre -> filterByGenre(event.genre)
            is AnimeListEvent.OnAnimeClick -> {
                // Navigation will be handled by the UI
            }
            is AnimeListEvent.OnFavoriteToggle -> toggleFavorite(event.anime)
            is AnimeListEvent.Retry -> loadAnime()
        }
    }
    
    private fun loadAnime() {
        Log.d(TAG, "loadAnime() called")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getTopAnimeUseCase(1).collect { result ->
                result.fold(
                    onSuccess = { animeList ->
                        val distinctList = animeList.distinctBy { "${it.id}-${it.displayTitle}" }
                        val genres = extractGenres(distinctList)
                        
                        _state.update {
                            it.copy(
                                animeList = distinctList,
                                filteredAnimeList = distinctList, // Initially show all
                                availableGenres = genres,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "An error occurred"
                            )
                        }
                    }
                )
            }
        }
    }
    
    private fun refreshAnime() {
        Log.d(TAG, "refreshAnime() called")
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            
            getTopAnimeUseCase(1).collect { result ->
                result.fold(
                    onSuccess = { animeList ->
                        val distinctList = animeList.distinctBy { "${it.id}-${it.displayTitle}" }
                        val genres = extractGenres(distinctList)
                        
                        _state.update {
                            val currentGenre = it.selectedGenre
                            it.copy(
                                animeList = distinctList,
                                filteredAnimeList = if (currentGenre == "All") distinctList else filterAnimeByGenre(distinctList, currentGenre),
                                availableGenres = genres,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isRefreshing = false,
                                error = exception.message ?: "An error occurred"
                            )
                        }
                    }
                )
            }
        }
    }
    
    private fun shuffleAnime() {
        _state.update { 
            it.copy(
                animeList = it.animeList.shuffled(),
                filteredAnimeList = it.filteredAnimeList.shuffled()
            ) 
        }
    }
    
    private fun filterByGenre(genre: String) {
        _state.update { currentState ->
            val filteredList = if (genre == "All") {
                currentState.animeList
            } else {
                filterAnimeByGenre(currentState.animeList, genre)
            }
            
            currentState.copy(
                selectedGenre = genre,
                filteredAnimeList = filteredList
            )
        }
    }
    
    private fun extractGenres(animeList: List<com.focus.flow.domain.model.Anime>): List<String> {
        val allGenres = mutableSetOf<String>()
        
        animeList.forEach { anime ->
            anime.genre?.forEach { genre ->
                allGenres.add(genre.name)
            }
            anime.theme?.forEach { theme ->
                allGenres.add(theme.name)
            }
            anime.demographic?.forEach { demo ->
                allGenres.add(demo.name)
            }
        }
        
        // Return "All" first, then sorted genres
        return listOf("All") + allGenres.sorted()
    }
    
    private fun filterAnimeByGenre(animeList: List<com.focus.flow.domain.model.Anime>, genre: String): List<com.focus.flow.domain.model.Anime> {
        return animeList.filter { anime ->
            val hasGenre = anime.genre?.any { it.name == genre } == true
            val hasTheme = anime.theme?.any { it.name == genre } == true
            val hasDemographic = anime.demographic?.any { it.name == genre } == true
            
            hasGenre || hasTheme || hasDemographic
        }
    }
    
    private fun toggleFavorite(anime: com.focus.flow.domain.model.Anime) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(anime)
                // Update the local state
                val updatedList = _state.value.animeList.map { 
                    if ("${it.id}-${it.displayTitle}" == "${anime.id}-${anime.displayTitle}") it.copy(isFavorite = !it.isFavorite) else it 
                }
                val updatedFilteredList = _state.value.filteredAnimeList.map { 
                    if ("${it.id}-${it.displayTitle}" == "${anime.id}-${anime.displayTitle}") it.copy(isFavorite = !it.isFavorite) else it 
                }
                _state.update { it.copy(animeList = updatedList, filteredAnimeList = updatedFilteredList) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(error = "Failed to update favorite status") 
                }
            }
        }
    }
}

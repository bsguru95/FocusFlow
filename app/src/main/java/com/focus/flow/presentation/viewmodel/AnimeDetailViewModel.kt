package com.focus.flow.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.flow.domain.usecase.GetAnimeByIdUseCase
import com.focus.flow.domain.usecase.ToggleFavoriteUseCase
import com.focus.flow.presentation.state.AnimeDetailEvent
import com.focus.flow.presentation.state.AnimeDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val getAnimeByIdUseCase: GetAnimeByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val animeId: Int = checkNotNull(savedStateHandle["animeId"]).toString().toInt()
    
    private val _state = MutableStateFlow(AnimeDetailState())
    val state: StateFlow<AnimeDetailState> = _state.asStateFlow()
    
    init {
        loadAnime()
    }
    
    fun onEvent(event: AnimeDetailEvent) {
        when (event) {
            is AnimeDetailEvent.LoadAnime -> loadAnime()
            is AnimeDetailEvent.ToggleFavorite -> toggleFavorite()
            is AnimeDetailEvent.PlayTrailer -> playTrailer()
            is AnimeDetailEvent.PauseTrailer -> pauseTrailer()
            is AnimeDetailEvent.Retry -> loadAnime()
        }
    }
    
    private fun loadAnime() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getAnimeByIdUseCase(animeId).collect { result ->
                result.fold(
                    onSuccess = { anime ->
                        _state.update {
                            it.copy(
                                anime = anime,
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
    
    private fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentAnime = _state.value.anime
                if (currentAnime != null) {
                    toggleFavoriteUseCase(currentAnime)
                    _state.update { 
                        it.copy(anime = currentAnime.copy(isFavorite = !currentAnime.isFavorite)) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(error = "Failed to update favorite status") 
                }
            }
        }
    }
    
    private fun playTrailer() {
        _state.update { it.copy(isVideoPlaying = true) }
    }
    
    private fun pauseTrailer() {
        _state.update { it.copy(isVideoPlaying = false) }
    }
}

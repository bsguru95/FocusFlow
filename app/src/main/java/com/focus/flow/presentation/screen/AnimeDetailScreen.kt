package com.focus.flow.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.focus.flow.presentation.component.VideoPlayer
import com.focus.flow.presentation.component.ExoPlayerManager
import com.focus.flow.presentation.state.AnimeDetailEvent
import com.focus.flow.presentation.viewmodel.AnimeDetailViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    onBackClick: () -> Unit,
    viewModel: AnimeDetailViewModel = hiltViewModel(),
    exoPlayerManager: ExoPlayerManager
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anime Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    state.anime?.let { anime ->
                        IconButton(
                            onClick = { viewModel.onEvent(AnimeDetailEvent.ToggleFavorite) }
                        ) {
                            Icon(
                                imageVector = if (anime.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (anime.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (anime.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.onEvent(AnimeDetailEvent.Retry) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
                
                state.anime != null -> {
                    val anime = state.anime!!
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Video Player or Poster
                        if (!anime.trailer?.youtubeId.isNullOrEmpty()) {
                            VideoPlayer(
                                videoUrl = "https://www.youtube.com/watch?v=${anime.trailer?.youtubeId}",
                                exoPlayerManager = exoPlayerManager,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            AsyncImage(
                                model = anime.images?.jpg?.largeImageUrl,
                                contentDescription = anime.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Anime Details
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = anime.displayTitle,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            if (!anime.titleEnglish.isNullOrEmpty() && anime.titleEnglish != anime.title) {
                                Text(
                                    text = anime.titleEnglish,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Rating and Episodes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Rating",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = anime.score?.let { String.format("%.1f", it) } ?: "N/A",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Episodes",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = anime.episodes?.toString() ?: "Unknown",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Synopsis
                            if (!anime.displaySynopsis.isNullOrEmpty() && anime.displaySynopsis != "No synopsis available") {
                                Text(
                                    text = "Synopsis",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = anime.displaySynopsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 24.sp
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Genres
                            if (!anime.genre.isNullOrEmpty()) {
                                Text(
                                    text = "Genres",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    anime.genre.forEach { genre ->
                                        AssistChip(
                                            onClick = { },
                                            label = { Text(genre.name) }
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Additional Info
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Additional Information",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    InfoRow("Type", anime.displayType)
                                    InfoRow("Status", anime.displayStatus)
                                    InfoRow("Duration", anime.displayDuration)
                                    if (!anime.rating.isNullOrEmpty()) {
                                        InfoRow("Rating", anime.rating)
                                    }
                                    if (anime.year != null) {
                                        InfoRow("Year", anime.year.toString())
                                    }
                                    if (!anime.season.isNullOrEmpty()) {
                                        InfoRow("Season", anime.season)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

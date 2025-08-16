package com.focus.flow.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.focus.flow.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.random.Random
import androidx.hilt.navigation.compose.hiltViewModel
import com.focus.flow.presentation.component.AnimeCard
import com.focus.flow.presentation.state.AnimeListEvent
import com.focus.flow.presentation.viewmodel.AnimeListViewModel

@Composable
fun AnimeListScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: AnimeListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Top Navigation Section
            item {
                TopNavigationSection(
                    onSearchClick = { },
                    onNotificationClick = { },
                    onShuffleClick = { viewModel.onEvent(AnimeListEvent.ShuffleAnime) }
                )
            }
            
            // Featured Anime Section
            item {
                if (state.animeList.isNotEmpty()) {
                    FeaturedAnimeSection(
                        featuredAnime = state.animeList.take(5),
                        onAnimeClick = onAnimeClick
                    )
                }
            }
            
            // Categories Section
            item {
                CategoriesSection(
                    availableGenres = state.availableGenres,
                    selectedGenre = state.selectedGenre,
                    onGenreSelected = { genre ->
                        viewModel.onEvent(AnimeListEvent.FilterByGenre(genre))
                    }
                )
            }
            
            // Popular Anime Grid Section
            item {
                PopularAnimeSection(
                    animeList = state.filteredAnimeList,
                    onAnimeClick = onAnimeClick,
                    onFavoriteToggle = { anime ->
                        viewModel.onEvent(AnimeListEvent.OnFavoriteToggle(anime))
                    },
                    isLoading = state.isLoading && state.animeList.isEmpty(),
                    error = state.error,
                    onRetry = { viewModel.onEvent(AnimeListEvent.Retry) }
                )
            }
        }
        
        // Loading overlay
        if (state.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = DarkPrimary
            )
        }
    }
}

// Top Navigation Component
@Composable
private fun TopNavigationSection(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onShuffleClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://i.pravatar.cc/100?img=1",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Hello, Otaku!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "Discover Anime",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Action Buttons
        Row {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onShuffleClick) {
                Text(
                    text = "🔀",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Featured Anime Carousel (Dribbble Style)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeaturedAnimeSection(
    featuredAnime: List<com.focus.flow.domain.model.Anime>,
    onAnimeClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { featuredAnime.size }
    )
    
    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // Auto scroll every 4 seconds
            val nextPage = (pagerState.currentPage + 1) % featuredAnime.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Column(
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Today",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Sophisticated Pager Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentPadding = PaddingValues(horizontal = 40.dp),
            pageSpacing = (-20).dp // Overlapping effect
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = lerp(0.85f, 1.0f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
            val alpha = lerp(0.6f, 1.0f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
            
            FeaturedCarouselCard(
                anime = featuredAnime[page],
                onClick = { if (featuredAnime[page].id > 0) onAnimeClick(featuredAnime[page].id) },
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Page Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(featuredAnime.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(
                            width = if (isSelected) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .background(
                            color = if (isSelected) DarkPrimary else Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                )
                if (index < featuredAnime.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

// Sophisticated Carousel Card (Dribbble Style)
@Composable
private fun FeaturedCarouselCard(
    anime: com.focus.flow.domain.model.Anime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image with better quality
            AsyncImage(
                model = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl,
                contentDescription = anime.displayTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Sophisticated Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            
            // Top Rating Badge
            if (anime.score != null && anime.score > 0) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700), // Gold color
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", anime.score),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Bottom Content with Enhanced Design
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Genre Tags
                anime.genre?.take(2)?.let { genres ->
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        items(genres) { genre ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = DarkPrimary.copy(alpha = 0.8f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                // Title with better typography
                Text(
                    text = anime.displayTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 28.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Additional Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type and Year
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = anime.displayType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        
                        anime.year?.let { year ->
                            Text(
                                text = " • $year",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    // Episodes count
                    anime.episodes?.let { episodes ->
                        Text(
                            text = "$episodes EP",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Categories Section
@Composable
private fun CategoriesSection(
    availableGenres: List<String>,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit
) {
    if (availableGenres.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                if (selectedGenre != "All") {
                    Text(
                        text = "${availableGenres.size - 1} genres available",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableGenres.take(10)) { genre -> // Limit to 10 for performance
                    CategoryChip(
                        category = genre,
                        isSelected = genre == selectedGenre,
                        onClick = { onGenreSelected(genre) }
                    )
                }
            }
        }
    }
}

// Category Chip
@Composable
private fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkPrimary else CardBackground
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// Popular Anime Section
@Composable
private fun PopularAnimeSection(
    animeList: List<com.focus.flow.domain.model.Anime>,
    onAnimeClick: (Int) -> Unit,
    onFavoriteToggle: (com.focus.flow.domain.model.Anime) -> Unit,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Anime",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            // Show count when filtered
            Text(
                text = "${animeList.size} anime",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkPrimary)
                }
            }
            
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                    ) {
                        Text("Retry")
                    }
                }
            }
            
            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2), // 2 columns for better card visibility
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(800.dp), // Fixed height for LazyColumn compatibility
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    itemsIndexed(
                        items = animeList,
                        key = { _, anime -> "${anime.id}-${anime.displayTitle}" }
                    ) { index, anime ->
                        val animationDelay = (index * 50).coerceAtMost(500)
                        
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = animationDelay,
                                    easing = FastOutSlowInEasing
                                ),
                                initialOffsetY = { it }
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = animationDelay,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            AnimeCard(
                                anime = anime,
                                onAnimeClick = {
                                    if (anime.id > 0) onAnimeClick(anime.id)
                                },
                                onFavoriteToggle = onFavoriteToggle
                            )
                        }
                    }
                }
            }
        }
    }
}

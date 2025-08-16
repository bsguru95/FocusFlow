package com.focus.flow.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.focus.flow.domain.model.Anime
import androidx.compose.ui.res.painterResource
import com.focus.flow.R
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeCard(
    anime: Anime,
    onAnimeClick: (Anime) -> Unit,
    onFavoriteToggle: (Anime) -> Unit,
    modifier: Modifier = Modifier
) {
    val showDialog = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (anime.id > 0) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // Slightly taller for better readability
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (anime.id > 0) {
                    Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null, // Custom animation instead
                        onClick = { onAnimeClick(anime) },
                        onLongClick = { showDialog.value = true }
                    )
                } else {
                    Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { },
                        onLongClick = { showDialog.value = true }
                    )
                }
            ),
        shape = RoundedCornerShape(20.dp), // More rounded for modern look
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AsyncImage(
                model = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl,
                contentDescription = anime.displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_broken_image),
                error = painterResource(id = R.drawable.ic_broken_image)
            )
            
            // Enhanced Gradient Overlay for better readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f), // Subtle top overlay
                                Color.Black.copy(alpha = 0.3f), // Mid gradient
                                Color.Black.copy(alpha = 0.85f) // Strong bottom overlay for text readability
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            
            // Animated Rating Badge (Top Right)
            if (anime.score != null && anime.score > 0) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = tween(300, delayMillis = 200),
                        initialScale = 0.8f
                    ) + fadeIn(animationSpec = tween(300, delayMillis = 200)),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Card(
                        modifier = Modifier.padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = anime.score.roundToInt().toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Animated Favorite Button (Top Left)
            val favoriteScale by animateFloatAsState(
                targetValue = if (anime.isFavorite) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "favoriteScale"
            )
            
            IconButton(
                onClick = { onFavoriteToggle(anime) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .graphicsLayer {
                        scaleX = favoriteScale
                        scaleY = favoriteScale
                    }
            ) {
                Icon(
                    imageVector = if (anime.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (anime.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (anime.isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            // Enhanced Title and Type (Bottom) with better readability
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = anime.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    lineHeight = 18.sp,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Type chip for better visibility
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (anime.id > 0) 
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        else 
                            Color.Red.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = if (anime.id > 0) anime.displayType else "No Details",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // Animated Long Press Dialog
    androidx.compose.animation.AnimatedVisibility(
        visible = showDialog.value,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(
            animationSpec = tween(200),
            targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(200))
    ) {
        AnimeDetailsDialog(
            anime = anime,
            onDismiss = { showDialog.value = false }
        )
    }
}

@Composable
private fun AnimeDetailsDialog(
    anime: Anime,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = anime.displayTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Image
                AsyncImage(
                    model = anime.images?.jpg?.imageUrl,
                    contentDescription = anime.displayTitle,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_broken_image),
                    error = painterResource(id = R.drawable.ic_broken_image)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Details
                DetailRow("Type", anime.displayType)
                DetailRow("Status", anime.displayStatus)
                if (anime.score != null && anime.score > 0) {
                    DetailRow("Rating", "${anime.score} ⭐")
                }
                if (anime.episodes != null && anime.episodes > 0) {
                    DetailRow("Episodes", anime.episodes.toString())
                }
                if (anime.year != null) {
                    DetailRow("Year", anime.year.toString())
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
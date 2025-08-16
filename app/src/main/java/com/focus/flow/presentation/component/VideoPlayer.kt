package com.focus.flow.presentation.component

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.media3.ui.PlayerView
import com.focus.flow.presentation.component.ExoPlayerManager

private fun extractYouTubeVideoId(url: String): String? {
    return when {
        url.contains("youtube.com/watch?v=") -> {
            url.substringAfter("v=").substringBefore("&")
        }
        url.contains("youtu.be/") -> {
            url.substringAfter("youtu.be/").substringBefore("?")
        }
        url.contains("youtube.com/embed/") -> {
            url.substringAfter("embed/").substringBefore("?")
        }
        else -> null
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}

@Composable
fun VideoPlayer(
    videoUrl: String?,
    exoPlayerManager: ExoPlayerManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNetworkAvailable = remember { mutableStateOf(isNetworkAvailable(context)) }
    
    // Check network connectivity periodically
    LaunchedEffect(Unit) {
        while (true) {
            isNetworkAvailable.value = isNetworkAvailable(context)
            kotlinx.coroutines.delay(5000) // Check every 5 seconds
        }
    }
    
    if (!videoUrl.isNullOrEmpty()) {
        when {
            videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be") -> {
                // Use WebView for YouTube videos
                val videoId = remember(videoUrl) { extractYouTubeVideoId(videoUrl) }
                
                if (videoId != null) {
                    if (isNetworkAvailable.value) {
                        AndroidView(
                            factory = { webContext ->
                                WebView(webContext).apply {
                                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.mediaPlaybackRequiresUserGesture = false
                                    webViewClient = WebViewClient()
                                    
                                    val embedHtml = """
                                        <!DOCTYPE html>
                                        <html>
                                        <head>
                                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                            <style>
                                                body { margin: 0; padding: 0; background: black; }
                                                iframe { width: 100%; height: 100%; border: none; }
                                            </style>
                                        </head>
                                        <body>
                                            <iframe src="https://www.youtube.com/embed/$videoId?autoplay=0&controls=1&modestbranding=1&rel=0" 
                                                    frameborder="0" 
                                                    allowfullscreen>
                                            </iframe>
                                        </body>
                                        </html>
                                    """.trimIndent()
                                    
                                    loadData(embedHtml, "text/html", "utf-8")
                                }
                            },
                            modifier = modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        )
                    } else {
                        // Show offline state for YouTube videos
                        Card(
                            modifier = modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📡",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Trailer Unavailable Offline",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Connect to internet to watch",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Fallback for invalid YouTube URLs
                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Invalid Trailer URL",
                                textAlign = TextAlign.Center,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            else -> {
                // Use ExoPlayer for direct video URLs
                DisposableEffect(videoUrl) {
                    exoPlayerManager.preparePlayer(videoUrl)
                    
                    onDispose {
                        exoPlayerManager.stop()
                    }
                }
                
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            player = exoPlayerManager.getExoPlayer()
                            useController = true
                        }
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }
        }
    } else {
        // No video available
        Card(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Trailer Available",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

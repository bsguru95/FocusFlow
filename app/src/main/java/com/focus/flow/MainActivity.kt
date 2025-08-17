package com.focus.flow

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.focus.flow.presentation.component.ExoPlayerManager
import com.focus.flow.presentation.navigation.AnimeNavigation
import com.focus.flow.ui.theme.FocusFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Disable edge-to-edge mode and ensure traditional status/navigation bars
        WindowCompat.setDecorFitsSystemWindows(window, true)
        
        setContent {
            FocusFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AnimeNavigation(
                        navController = navController,
                        exoPlayerManager = exoPlayerManager
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayerManager.release()
    }
}
package com.focus.flow.presentation.component

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    
    fun getExoPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        return exoPlayer!!
    }
    
    fun preparePlayer(videoUrl: String) {
        val player = getExoPlayer()
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
    }
    
    fun play() {
        exoPlayer?.play()
    }
    
    fun pause() {
        exoPlayer?.pause()
    }
    
    fun stop() {
        exoPlayer?.stop()
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
    
    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying == true
    }
    
    fun addListener(listener: Player.Listener) {
        exoPlayer?.addListener(listener)
    }
    
    fun removeListener(listener: Player.Listener) {
        exoPlayer?.removeListener(listener)
    }
}

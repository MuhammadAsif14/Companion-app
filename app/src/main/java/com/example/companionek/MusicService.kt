package com.example.companionek

// MusicService.kt

// MusicService.kt

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentMusicResId: Int? = null
override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    val action = intent.getStringExtra("ACTION")
    val musicResId = intent.getIntExtra("MUSIC_RES_ID", 0)
    val volume = intent.getFloatExtra("VOLUME", 0.5f) // Default to 50% volume

    when (action) {
        "TOGGLE" -> toggleMusic(musicResId)
        "STOP" -> stopSelf()
        "SET_VOLUME" -> setVolume(volume) // Handle volume setting
    }

    return START_STICKY
}

    private fun toggleMusic(musicResId: Int) {
        if (isPlaying && musicResId == currentMusicResId) {
            // Pause music if the same music is playing
            pauseMusic()
        } else {
            // Play new music
            playMusic(musicResId)
        }
    }

    private fun playMusic(musicResId: Int) {
        if (mediaPlayer != null && musicResId != currentMusicResId) {
            // Release existing player for a different music file
            mediaPlayer?.release()
            mediaPlayer = null
        }

        if (mediaPlayer == null) {
            // Initialize new MediaPlayer with the new music file
            mediaPlayer = MediaPlayer.create(this, musicResId).apply {
                isLooping = true
                start()
            }
            isPlaying = true
            currentMusicResId = musicResId
        } else {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
    private fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

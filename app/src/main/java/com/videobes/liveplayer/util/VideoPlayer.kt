package com.videobes.liveplayer.util

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.File

object VideoPlayer {

    fun prepare(context: Context, player: ExoPlayer, file: File) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play(player: ExoPlayer) {
        player.playWhenReady = true
        player.play()
    }

    fun stop(player: ExoPlayer) {
        player.playWhenReady = false
        player.stop()
    }
}

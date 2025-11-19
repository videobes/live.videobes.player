package com.videobes.liveplayer.util

import com.videobes.liveplayer.model.MediaItem
import java.io.File

object MediaUtils {

    private val supportedVideos = listOf("mp4", "mov")
    private val supportedImages = listOf("jpg", "jpeg", "png")

    fun loadMedia(): List<MediaItem> {
        val mediaDir = File("/storage/emulated/0/LiveVideobes/media")
        if (!mediaDir.exists()) mediaDir.mkdirs()

        val files = mediaDir.listFiles() ?: return emptyList()

        val items = files.mapNotNull { file ->
            val ext = file.extension.lowercase()

            when {
                supportedVideos.contains(ext) ->
                    MediaItem(file, "video")

                supportedImages.contains(ext) ->
                    MediaItem(file, "image", duration = 6)

                else -> null
            }
        }

        return items.shuffled()
    }
}

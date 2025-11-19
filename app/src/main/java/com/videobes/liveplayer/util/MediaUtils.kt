package com.videobes.liveplayer.util

import com.videobes.liveplayer.model.MediaFile
import java.io.File

object MediaUtils {

    private val videos = listOf("mp4", "mov", "avi", "mkv")
    private val images = listOf("jpg", "jpeg", "png")

    fun loadMedia(): List<MediaFile> {
        val dir = File("/storage/emulated/0/LiveVideobes/media")
        if (!dir.exists()) dir.mkdirs()

        val files = dir.listFiles() ?: return emptyList()

        val list = files.mapNotNull { f ->
            val ext = f.extension.lowercase()

            when {
                videos.contains(ext) -> MediaFile(f, isVideo = true)
                images.contains(ext) -> MediaFile(f, isVideo = false, duration = 6)
                else -> null
            }
        }

        return list.shuffled()
    }
}

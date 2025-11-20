package com.videobes.liveplayer.model

import java.io.File

data class MediaFile(
    val file: File,
    val isVideo: Boolean,
    val duration: Int = 6      // usado somente para imagens
)

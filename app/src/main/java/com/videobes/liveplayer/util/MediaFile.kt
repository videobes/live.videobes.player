package com.videobes.liveplayer.model

import android.net.Uri

/**
 * Representa um item de mídia universal no Live Videobes.
 *
 * Usado para futura cache, pré-download, metadados,
 * controle de duração e playlists inteligentes.
 *
 * Sempre funciona com URI (SAF), nunca com java.io.File.
 */
data class MediaFile(
    val uri: Uri,
    val name: String,
    val isVideo: Boolean,
    val durationSeconds: Int = 10   // padrão para imagens
)

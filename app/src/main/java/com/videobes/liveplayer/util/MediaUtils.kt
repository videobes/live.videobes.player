package com.videobes.liveplayer.util

import android.net.Uri

object MediaUtils {

    private val videoExt = listOf("mp4", "mov", "mkv", "avi", "webm")
    private val imageExt = listOf("jpg", "jpeg", "png", "webp")

    /**
     * Retorna true se a Uri for de vídeo.
     */
    fun isVideo(uri: Uri): Boolean {
        val name = uri.toString().lowercase()
        return videoExt.any { name.endsWith(it) }
    }

    /**
     * Retorna true se a Uri for de imagem.
     */
    fun isImage(uri: Uri): Boolean {
        val name = uri.toString().lowercase()
        return imageExt.any { name.endsWith(it) }
    }

    /**
     * Define duração padrão para imagens.
     * (Pode virar configurável pelo painel depois)
     */
    fun imageDurationSeconds(): Int = 10

    /**
     * Só retorna a extensão minúscula.
     */
    fun extension(uri: Uri): String {
        val str = uri.toString().lowercase()
        return str.substringAfterLast('.', "")
    }
}

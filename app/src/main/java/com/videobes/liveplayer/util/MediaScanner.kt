package com.videobes.liveplayer.util

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile

object MediaScanner {

    private val videoExt = listOf("mp4", "mov", "mkv", "avi", "webm")
    private val imageExt = listOf("jpg", "jpeg", "png", "webp")

    /**
     * Scaneia a pasta de mídia escolhida pelo usuário via SAF.
     */
    fun scan(ctx: Context, folderUri: Uri): List<Uri> {
        return try {
            val root = DocumentFile.fromTreeUri(ctx, folderUri)
                ?: return emptyList()

            val list = mutableListOf<Uri>()
            collectFilesRecursively(root, list)

            // embaralha tudo antes de enviar ao player
            list.shuffled()
        } catch (e: Exception) {
            Log.e("MediaScanner", "Erro ao escanear mídia", e)
            emptyList()
        }
    }

    /**
     * Varredura recursiva segura usando DocumentFile.
     * Suporta subpastas infinitas.
     */
    private fun collectFilesRecursively(dir: DocumentFile, out: MutableList<Uri>) {
        val children = dir.listFiles()

        for (file in children) {
            if (file.isDirectory) {
                collectFilesRecursively(file, out)
            } else if (file.isFile && !file.name.isNullOrBlank()) {

                val name = file.name!!.lowercase()

                val isVideo = videoExt.any { name.endsWith(it) }
                val isImage = imageExt.any { name.endsWith(it) }

                if (isVideo || isImage) {
                    out.add(file.uri)
                }
            }
        }
    }
}

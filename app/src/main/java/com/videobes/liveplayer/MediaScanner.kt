package com.videobes.liveplayer

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object MediaScanner {

    fun scan(ctx: Context, folderUri: Uri): List<Uri> {
        val root = DocumentFile.fromTreeUri(ctx, folderUri) ?: return emptyList()
        return root.listFiles()
            .filter { it.isFile }
            .filter {
                val name = (it.name ?: "").lowercase()
                name.endsWith(".mp4")
                        || name.endsWith(".mkv")
                        || name.endsWith(".jpg")
                        || name.endsWith(".png")
                        || name.endsWith(".jpeg")
            }
            .shuffled()
            .map { it.uri }
    }
}

package com.videobes.liveplayer.usb

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object UsbMediaImporter {

    fun importFromUri(context: Context, treeUri: Uri): Boolean {
        return try {
            val mediaDir = File(context.filesDir, "media")

            if (mediaDir.exists()) {
                mediaDir.deleteRecursively()
            }
            mediaDir.mkdirs()

            val children = context.contentResolver
                .openFileDescriptor(treeUri, "r") ?: return false

            children.close()

            val docTree = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                ?: return false

            docTree.listFiles().forEach { file ->
                if (file.isFile) {
                    val target = File(mediaDir, file.name ?: return@forEach)
                    copy(context, file.uri, target)
                }
            }

            true
        } catch (e: Exception) {
            Log.e("UsbImporter", "Erro ao importar USB", e)
            false
        }
    }

    private fun copy(context: Context, source: Uri, target: File) {
        context.contentResolver.openInputStream(source)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

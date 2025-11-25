package com.videobes.liveplayer.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract

object MediaScanner {

    private val validExtensions = listOf(
        ".mp4", ".mkv", ".mov", ".avi",
        ".jpg", ".jpeg", ".png", ".webp"
    )

    fun scan(ctx: Context, treeUri: Uri): List<Uri> {
        val result = mutableListOf<Uri>()

        try {
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )

            val resolver: ContentResolver = ctx.contentResolver

            resolver.query(
                childrenUri,
                arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE
                ),
                null, null, null
            )?.use { cursor ->

                val idxName = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val idxMime = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val idxDocId = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)

                while (cursor.moveToNext()) {
                    val name = cursor.getString(idxName)?.lowercase() ?: ""
                    val mime = cursor.getString(idxMime) ?: ""
                    val docId = cursor.getString(idxDocId) ?: continue

                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        docId
                    )

                    // Subpastas
                    if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                        result += scan(ctx, fileUri)
                        continue
                    }

                    // Filtrar somente midias válidas
                    if (validExtensions.any { name.endsWith(it) }) {
                        result += fileUri
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }
}

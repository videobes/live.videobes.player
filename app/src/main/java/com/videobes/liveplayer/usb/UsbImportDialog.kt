package com.videobes.liveplayer.usb

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract

object UsbImportDialog {

    fun show(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Pendrive detectado")
            .setMessage(
                "Deseja importar os arquivos de mídia?\n\n" +
                        "⚠️ Os arquivos atuais serão substituídos."
            )
            .setPositiveButton("Importar") { _, _ ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                )
                context.startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

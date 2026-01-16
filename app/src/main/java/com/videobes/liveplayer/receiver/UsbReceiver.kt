package com.videobes.liveplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.videobes.liveplayer.usb.UsbImportDialog

class UsbReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_MEDIA_MOUNTED) return

        val usbUri: Uri = intent.data ?: return

        // Receiver não roda na UI thread → garante segurança
        Handler(Looper.getMainLooper()).post {
            UsbImportDialog.show(context)
        }
    }
}

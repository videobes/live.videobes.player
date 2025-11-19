package com.videobes.liveplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri

class UsbReceiver(private val onUsbDetected: (Uri) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        val action = intent.action ?: return

        if (action == Intent.ACTION_MEDIA_MOUNTED) {
            val usbUri = intent.data
            if (usbUri != null) {
                onUsbDetected(usbUri)
            }
        }
    }
}

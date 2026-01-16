package com.videobes.liveplayer.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class UsbReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_MEDIA_MOUNTED == intent.action) {
            Toast.makeText(
                context,
                "Pendrive detectado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

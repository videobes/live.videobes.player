package com.videobes.liveplayer

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

object KioskHelper {

    fun apply(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.decorView.systemUiVisibility = flags
    }

    fun monitor(window: Window) {
        window.decorView.setOnSystemUiVisibilityChangeListener {
            apply(window)
        }
    }
}

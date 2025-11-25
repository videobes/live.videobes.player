package com.videobes.liveplayer

import android.content.Context

object Prefs {

    private const val NAME = "live_prefs"
    private const val KEY_SETUP_DONE = "setup_done"
    private const val KEY_MEDIA_URI = "media_uri"
    private const val KEY_KIOSK = "kiosk_accepted"

    fun isKioskAccepted(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_KIOSK, false)

    fun setKioskAccepted(ctx: Context, accepted: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_KIOSK, accepted).apply()


    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isSetupDone(ctx: Context) =
        prefs(ctx).getBoolean(KEY_SETUP_DONE, false)

    fun setSetupDone(ctx: Context, b: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_SETUP_DONE, b).apply()

    fun getMediaUri(ctx: Context) =
        prefs(ctx).getString(KEY_MEDIA_URI, null)

    fun setMediaUri(ctx: Context, uri: String) =
        prefs(ctx).edit().putString(KEY_MEDIA_URI, uri).apply()
}

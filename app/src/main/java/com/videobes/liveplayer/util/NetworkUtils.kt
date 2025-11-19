package com.videobes.liveplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    enum class ConnectionType {
        WIFI, MOBILE, OFFLINE
    }

    fun getConnectionType(context: Context): ConnectionType {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return ConnectionType.OFFLINE
            val capabilities = connectivityManager.getNetworkCapabilities(network)
                ?: return ConnectionType.OFFLINE

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                    ConnectionType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                    ConnectionType.MOBILE
                else -> ConnectionType.OFFLINE
            }

        } catch (e: Exception) {
            return ConnectionType.OFFLINE
        }
    }
}

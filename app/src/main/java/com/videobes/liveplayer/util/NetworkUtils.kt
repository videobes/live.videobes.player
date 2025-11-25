package com.videobes.liveplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    enum class ConnectionType {
        WIFI,
        MOBILE,
        OFFLINE
    }

    /**
     * Retorna o tipo de conexão atual (Wi-Fi, Mobile ou Offline).
     * Projetado para funcionar até em TV Boxes com Android chinês modificado.
     */
    fun getConnectionType(context: Context): ConnectionType {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return ConnectionType.OFFLINE
            val caps = cm.getNetworkCapabilities(network) ?: return ConnectionType.OFFLINE

            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.MOBILE

                // fallback para boxes que mentem nas capabilities
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ->
                    ConnectionType.WIFI

                else -> ConnectionType.OFFLINE
            }
        } catch (e: Exception) {
            ConnectionType.OFFLINE
        }
    }
}

val connection = NetworkUtils.getConnectionType(this)

val connectionText = findViewById<TextView>(R.id.connectionStatus)

when (connection) {
    NetworkUtils.ConnectionType.WIFI -> {
        connectionText.text = "Conexão atual: Wi-Fi"
        connectionText.setTextColor(Color.GREEN)
        // Campo Wi-Fi continua visível
    }
    NetworkUtils.ConnectionType.MOBILE -> {
        connectionText.text = "Conexão atual: 3G/4G"
        connectionText.setTextColor(Color.CYAN)
        // esconder campo de senha Wi-Fi
        findViewById<View>(R.id.inputWifiPassword).visibility = View.GONE
    }
    NetworkUtils.ConnectionType.OFFLINE -> {
        connectionText.text = "Sem internet – Modo somente com mídia local"
        connectionText.setTextColor(Color.YELLOW)
        // manter tudo visível, só alerta
    }
}

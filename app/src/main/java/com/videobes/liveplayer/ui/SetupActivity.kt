package com.videobes.liveplayer.ui

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.videobes.liveplayer.R
import com.videobes.liveplayer.receiver.UsbReceiver
import com.videobes.liveplayer.util.NetworkUtils
import java.io.File
import android.content.Intent
import android.content.IntentFilter

class SetupActivity : AppCompatActivity() {

    private lateinit var usbReceiver: UsbReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // -----------------------------------------------------------
        // 1) DETECTAR REDE (Wi-Fi / 4G / offline)
        // -----------------------------------------------------------
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
            }
        }

        // -----------------------------------------------------------
        // 2) DETECTAR PENDRIVE
        // -----------------------------------------------------------
        usbReceiver = UsbReceiver { usbUri ->
            runOnUiThread {
                showUsbDialog(usbUri)
            }
        }

        val filter = IntentFilter(Intent.ACTION_MEDIA_MOUNTED)
        filter.addDataScheme("file")
        registerReceiver(usbReceiver, filter)

        // -----------------------------------------------------------
        // (em breve) Campos: Wi-Fi, Channel ID, Mídia Local, Botão PLAY
        // -----------------------------------------------------------
    }

    // -----------------------------------------------------------
    // Pop-up: Importar mídia do USB?
    // -----------------------------------------------------------
    private fun showUsbDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Mídia USB detectada")
            .setMessage("Deseja importar os arquivos deste pendrive?")
            .setPositiveButton("Sim") { _, _ ->
                importFromUsb(uri)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    // -----------------------------------------------------------
    // Importar arquivos do USB para /LiveVideobes/media/
    // -----------------------------------------------------------
    private fun importFromUsb(uri: Uri) {
        val usbPath = uri.path ?: return
        val src = File(usbPath)
        val destDir = File("/storage/emulated/0/LiveVideobes/media/")

        if (!destDir.exists()) destDir.mkdirs()

        src.walk().forEach { file ->
            if (file.isFile && (file.extension.lowercase() in listOf("mp4", "jpg", "jpeg", "png"))) {
                val dest = File(destDir, file.name)
                file.copyTo(dest, overwrite = true)
            }
        }

        Toast.makeText(this, "Mídia importada!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }
}

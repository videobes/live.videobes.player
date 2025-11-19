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
    private lateinit var secret: SecretCode
    private lateinit var wifiPasswordInput: EditText
    private lateinit var channelIdInput: EditText
    private lateinit var localMediaCheck: CheckBox
    private lateinit var playButton: Button

 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        secret = SecretCode { code ->
            handleSecretCode(code)
            }

        
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

        // -----------------------------------------------------------------------
        <!-- 3. CAMPOS: Wi-Fi, Channel ID, Mídia Local, Botão PLAY -->
        // -----------------------------------------------------------------------
        wifiPasswordInput = findViewById(R.id.inputWifiPassword)
        channelIdInput = findViewById(R.id.inputChannelId)
        localMediaCheck = findViewById(R.id.checkLocalMedia)
        playButton = findViewById(R.id.btnPlay)

        // Quando marcar "Mídia local", desabilita o Channel ID
        localMediaCheck.setOnCheckedChangeListener { _, isChecked ->
            channelIdInput.isEnabled = !isChecked
            channelIdInput.alpha = if (isChecked) 0.4f else 1.0f
        }

        // Botão PLAY
        playButton.setOnClickListener {
            saveConfig()
            Toast.makeText(this, "Configurações salvas. (Depois ligamos no player)", Toast.LENGTH_SHORT).show()
            // Aqui futuramente: startActivity(Intent(this, PlayerActivity::class.java))
            // finish()
        }
        
        <!-- SENHA DO WI-FI -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Senha do Wi-Fi (opcional)"
            android:textColor="#cccccc"
            android:textSize="14sp"
            android:layout_marginBottom="8dp" />

        <EditText
            android:id="@+id/inputWifiPassword"
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:hint="Digite a senha do Wi-Fi"
            android:textColor="#ffffff"
            android:hintTextColor="#777777"
            android:background="#222222"
            android:padding="12dp"
            android:inputType="textPassword"
            android:layout_marginBottom="24dp" />

        <!-- CHANNEL ID + MÍDIA LOCAL -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Canal (ID do painel)"
            android:textColor="#cccccc"
            android:textSize="14sp"
            android:layout_marginBottom="8dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="24dp">

            <EditText
                android:id="@+id/inputChannelId"
                android:layout_width="0dp"
                android:layout_height="48dp"
                android:layout_weight="1"
                android:hint="Ex: alkuwait"
                android:textColor="#ffffff"
                android:hintTextColor="#777777"
                android:background="#222222"
                android:padding="12dp" />

            <CheckBox
                android:id="@+id/checkLocalMedia"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Mídia local"
                android:textColor="#ffffff"
                android:layout_marginStart="12dp" />
        </LinearLayout>

        <!-- BOTÃO PLAY -->
        <Button
            android:id="@+id/btnPlay"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="PLAY"
            android:textSize="18sp"
            android:background="#810EF8"
            android:textColor="#FFFFFF"
            android:padding="14dp"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="16dp" />

        
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN) {

        val char = when (event.keyCode) {
             KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> '0'
             KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> '1'
             KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> '2'
             KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> '3'
             KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> '4'
             KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> '5'
             KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> '6'
             KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> '7'
             KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> '8'
             KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> '9'
             KeyEvent.KEYCODE_STAR -> '*'
             else -> null
        }

        if (char != null) {
            secret.receive(char)
        }
    }

    return super.dispatchKeyEvent(event)
            }

   // -----------------------------------------------------------
    // Funçao save para gravar tudo
    // -----------------------------------------------------------
        
    private fun saveConfig() {
        val prefs = getSharedPreferences("livevideobes_prefs", MODE_PRIVATE)
        val wifiPassword = wifiPasswordInput.text.toString()
        val channelId = channelIdInput.text.toString()
        val useLocalMedia = localMediaCheck.isChecked

        prefs.edit()
            .putString("wifi_password", wifiPassword)
            .putString("channel_id", channelId)
            .putBoolean("use_local_media", useLocalMedia)
            .apply()
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

package com.videobes.liveplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var setupOverlay: View
    private lateinit var btnWifi: Button
    private lateinit var btnPickFolder: Button
    private lateinit var btnSecretMenu: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var mediaList = listOf<Uri>()
    private var index = 0

    private var backCount = 0
    private var lastBack = 0L

    private val REQ_PICK = 5001

    // controle de restart: só reinicia se NÃO foi saída via menu admin
    private var allowRestart = true

    // segredos por código numérico (*999, *222)
    private var secretSequence: String = ""

    // segredos por toque (5 topo-esq + 3 baixo-dir)
    private var topLeftTaps = 0
    private var bottomRightTaps = 0
    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se você estiver usando SetupActivity para aceitar o Kiosk,
        // pode descomentar este bloco:
        //
        // if (!Prefs.isKioskAccepted(this)) {
        //     startActivity(Intent(this, SetupActivity::class.java))
        //     finish()
        //     return
        // }

        // mantém tela ligada, mostra mesmo com lock e liga a tela
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setContentView(R.layout.activity_player)

        // aplica kiosk visual (fullscreen, barra escondida, etc.)
        KioskHelper.apply(window)
        KioskHelper.monitor(window)

        // tenta tirar o app de otimização de bateria (evita ser morto)
        try {
            BootUtils.requestIgnoreBatteryOptimizations(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        playerView = findViewById(R.id.playerView)
        setupOverlay = findViewById(R.id.setupOverlay)
        btnWifi = findViewById(R.id.btnWifi)
        btnPickFolder = findViewById(R.id.btnPickFolder)
        btnSecretMenu = findViewById(R.id.btnSecretMenu)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Listener único para saber quando um vídeo terminou
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    // quando um vídeo terminar, vai para o próximo
                    playNext()
                }
            }
        })

        btnWifi.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        btnPickFolder.setOnClickListener {
            chooseFolder()
        }

        btnSecretMenu.setOnClickListener {
            showAdmin()
        }

        // Se ainda não foi configurada a pasta de mídia,
        // mostra overlay e deixa intro como fundo
        if (!Prefs.isSetupDone(this)) {
            setupOverlay.visibility = View.VISIBLE
            playIntro()
        } else {
            setupOverlay.visibility = View.GONE
            playIntro(then = { startPlaylist() })
        }
    }

    // Vídeo de introdução Live Videobes
    private fun playIntro(then: (() -> Unit)? = null) {
        val introUri = Uri.parse("rawresource://${packageName}/${R.raw.live_videobes_intro}")
        val item = MediaItem.fromUri(introUri)

        player.stop()
        player.clearMediaItems()
        player.setMediaItem(item)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.playWhenReady = true

        if (then != null) {
            // margem de segurança (caso o estado não notifique por algum motivo)
            handler.postDelayed({
                if (player.playbackState == Player.STATE_ENDED || !player.playWhenReady) {
                    then()
                }
            }, 12_000L) // 10s do vídeo + folga
        }
    }

    // escolha de pasta de mídia via SAF
    private fun chooseFolder() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        startActivityForResult(i, REQ_PICK)
    }

    override fun onActivityResult(code: Int, result: Int, data: Intent?) {
        if (code == REQ_PICK && result == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Prefs.setMediaUri(this, uri.toString())
                Prefs.setSetupDone(this, true)
                setupOverlay.visibility = View.GONE
                startPlaylist()
            }
        }
        super.onActivityResult(code, result, data)
    }

    // inicia playlist local a partir da pasta escolhida
    private fun startPlaylist() {
        val folderUriString = Prefs.getMediaUri(this)
        if (folderUriString == null) {
            setupOverlay.visibility = View.VISIBLE
            return
        }

        mediaList = MediaScanner.scan(this, Uri.parse(folderUriString))

        if (mediaList.isEmpty()) {
            setupOverlay.visibility = View.VISIBLE
            return
        }

        index = 0
        playNext()
    }

    // decide qual mídia tocar
    private fun playNext() {
        if (mediaList.isEmpty()) return

        player.stop()
        player.clearMediaItems()

        val uri = mediaList[index]
        index = (index + 1) % mediaList.size

        val name = fileName(uri)
        val isImage = name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".png") ||
                name.endsWith(".webp")

        if (isImage) {
            playImage()
        } else {
            playVideo(uri)
        }
    }

    private fun playVideo(uri: Uri) {
        playerView.visibility = View.VISIBLE

        val item = MediaItem.fromUri(uri)
        player.stop()
        player.clearMediaItems()
        player.setMediaItem(item)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.playWhenReady = true
    }

    private fun playImage() {
        // esconde player de vídeo, deixa "tela preta" por baixo
        playerView.visibility = View.GONE

        // 10 segundos por imagem (podemos parametrizar depois)
        handler.postDelayed({
            playerView.visibility = View.VISIBLE
            playNext()
        }, 10_000L)
    }

    // tenta pegar o nome do arquivo de maneira robusta
    private fun fileName(uri: Uri): String {
        return try {
            var name: String? = null

            contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) {
                        name = it.getString(idx)
                    }
                }
            }

            name?.lowercase() ?: (uri.lastPathSegment?.lowercase() ?: "file")
        } catch (e: Exception) {
            "file"
        }
    }

    // menu admin (acesso via *999, toques ou CTRL+Z)
    private fun showAdmin() {
        AlertDialog.Builder(this)
            .setTitle("Admin Live Videobes")
            .setItems(
                arrayOf(
                    "Pausar reprodução",
                    "Retomar reprodução",
                    "Trocar pasta de mídia",
                    "Mostrar configurações",
                    "Sair do Live Videobes"
                )
            ) { _, which ->
                when (which) {
                    0 -> player.pause()
                    1 -> player.play()
                    2 -> chooseFolder()
                    3 -> setupOverlay.visibility = View.VISIBLE
                    4 -> {
                        // saída administrativa REAL: não reinicia
                        allowRestart = false
                        finishAffinity()
                    }
                }
            }
            .show()
    }

    // BACK: 3 toques em menos de 2s pra abrir admin (opção extra)
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (now - lastBack < 2000) {
            backCount++
        } else {
            backCount = 1
        }
        lastBack = now

        if (backCount >= 3) {
            showAdmin()
            backCount = 0
        }
        // não chama super, pra não fechar
    }

    // kiosk visual sempre reaplicado quando volta pro foco
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            Handler(Looper.getMainLooper()).postDelayed({
                KioskHelper.apply(window)
            }, 200L)
        }
    }

    override fun onResume() {
        super.onResume()
        KioskHelper.apply(window)
        KioskHelper.monitor(window)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        handler.removeCallbacksAndMessages(null)

        // só reinicia se não foi "Sair do Live Videobes" pelo admin
        if (allowRestart) {
            val i = Intent(this, RestartService::class.java)
            startService(i)
        }
    }

    // intercepta teclas especiais (HOME / RECENTS / CTRL+Z)
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // bloqueia APP_SWITCH (quadrado) e HOME se chegarem na Activity
        if (event.action == KeyEvent.ACTION_DOWN &&
            (event.keyCode == KeyEvent.KEYCODE_APP_SWITCH ||
                    event.keyCode == KeyEvent.KEYCODE_HOME)
        ) {
            return true
        }

        // CTRL+Z → Admin rápido (para testes / teclado externo)
        if (event.action == KeyEvent.ACTION_DOWN &&
            event.keyCode == KeyEvent.KEYCODE_Z &&
            event.isCtrlPressed
        ) {
            showAdmin()
            return true
        }

        return super.dispatchKeyEvent(event)
    }

    // trata códigos numéricos secretos: *999, *222
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_STAR) {
            // inicia sequência com '*'
            secretSequence = "*"
            return true
        }

        if (keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) {
            val digit = (keyCode - KeyEvent.KEYCODE_0).toString()
            secretSequence += digit

            // limita tamanho para não crescer infinito
            if (secretSequence.length > 5) {
                secretSequence = secretSequence.takeLast(5)
            }

            if (secretSequence.endsWith("*999")) {
                secretSequence = ""
                showAdmin()
                return true
            }

            if (secretSequence.endsWith("*222")) {
                secretSequence = ""
                setupOverlay.visibility = View.VISIBLE
                return true
            }

            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    // segredos por toque: 5 toques no canto superior esquerdo
    // e depois 3 no canto inferior direito → abre Admin
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            if (now - lastTapTime > 3000L) {
                // passou de 3s, reseta sequência
                topLeftTaps = 0
                bottomRightTaps = 0
            }
            lastTapTime = now

            val dm = resources.displayMetrics
            val w = dm.widthPixels.toFloat()
            val h = dm.heightPixels.toFloat()
            val x = event.x
            val y = event.y

            val isTopLeft = x < w * 0.25f && y < h * 0.25f
            val isBottomRight = x > w * 0.75f && y > h * 0.75f

            if (isTopLeft) {
                topLeftTaps++
                // não faz nada ainda, só acumula
            } else if (isBottomRight && topLeftTaps >= 5) {
                bottomRightTaps++
                if (bottomRightTaps >= 3) {
                    topLeftTaps = 0
                    bottomRightTaps = 0
                    showAdmin()
                }
            }
        }

        return super.onTouchEvent(event)
    }
}

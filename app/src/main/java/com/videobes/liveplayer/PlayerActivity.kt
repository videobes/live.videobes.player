package com.videobes.liveplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KioskHelper.apply(window)
        setContentView(R.layout.activity_player)

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

        // Listener ÚNICO — evita duplicações
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })

        btnWifi.setOnClickListener { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
        btnPickFolder.setOnClickListener { chooseFolder() }
        btnSecretMenu.setOnClickListener { showAdmin() }

        if (!Prefs.isSetupDone(this)) {
            setupOverlay.visibility = View.VISIBLE
            playIntro()
        } else {
            setupOverlay.visibility = View.GONE
            playIntro(then = { startPlaylist() })
        }
    }

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
            handler.postDelayed({
                if (player.playbackState == Player.STATE_ENDED) {
                    then()
                }
            }, 12000)
        }
    }

    private fun chooseFolder() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
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

    private fun playNext() {
        if (mediaList.isEmpty()) return

        player.stop()
        player.clearMediaItems()

        val uri = mediaList[index]
        index = (index + 1) % mediaList.size

        val name = fileName(uri)
        val isImage = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")

        if (isImage) playImage() else playVideo(uri)
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
        playerView.visibility = View.GONE

        handler.postDelayed({
            playerView.visibility = View.VISIBLE
            playNext()
        }, 10000)
    }

    private fun fileName(uri: Uri): String {
        return try {
            var name: String? = null

            contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) name = it.getString(idx)
                }
            }

            name ?: uri.lastPathSegment ?: "file"
        } catch (e: Exception) {
            "file"
        }
    }

    private fun showAdmin() {
        AlertDialog.Builder(this)
            .setTitle("Admin Live Videobes")
            .setItems(arrayOf(
                "Pausar",
                "Retomar",
                "Trocar pasta",
                "Mostrar configurações",
                "Sair"
            )) { _, which ->
                when (which) {
                    0 -> player.pause()
                    1 -> player.play()
                    2 -> chooseFolder()
                    3 -> setupOverlay.visibility = View.VISIBLE
                    4 -> finish()
                }
            }
            .show()
    }

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (now - lastBack < 2000) backCount++ else backCount = 1
        lastBack = now

        if (backCount >= 3) {
            showAdmin()
            backCount = 0
        }
    }

    override fun onResume() {
        super.onResume()
        KioskHelper.apply(window)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        handler.removeCallbacksAndMessages(null)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_Z && event.isCtrlPressed) {
            showAdmin()
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}

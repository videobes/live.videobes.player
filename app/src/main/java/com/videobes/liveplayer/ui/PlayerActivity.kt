package com.videobes.liveplayer.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.videobes.liveplayer.R
import com.videobes.liveplayer.util.MediaUtils
import com.videobes.liveplayer.model.MediaFile

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var imageHolder: ImageView
    private lateinit var player: ExoPlayer
    private var mediaList: List<MediaFile> = emptyList()

    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.playerView)
        imageHolder = findViewById(R.id.imagePlayer)

        // Oculta overlay
        findViewById<View>(R.id.setupOverlay).visibility = View.GONE

        mediaList = MediaUtils.loadMedia()

        if (mediaList.isEmpty()) {
            // opcional: deixar tela preta
            return
        }

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        playNext()
    }

    private fun playNext() {
        if (mediaList.isEmpty()) return

        val item = mediaList[currentIndex]

        if (item.isVideo) {
            showVideo(item)
        } else {
            showImage(item)
        }

        currentIndex++
        if (currentIndex >= mediaList.size) currentIndex = 0
    }

    private fun showVideo(item: MediaFile) {
        imageHolder.visibility = View.GONE
        playerView.visibility = View.VISIBLE

        val mediaItem = MediaItem.fromUri(Uri.fromFile(item.file))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        player.addListener(object : com.google.android.exoplayer2.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == com.google.android.exoplayer2.Player.STATE_ENDED) {
                    playNext()
                }
            }
        })
    }

    private fun showImage(item: MediaFile) {
        playerView.visibility = View.GONE
        imageHolder.visibility = View.VISIBLE

        imageHolder.setImageURI(Uri.fromFile(item.file))

        handler.postDelayed({
            playNext()
        }, item.duration * 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

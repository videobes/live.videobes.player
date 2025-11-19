package com.videobes.liveplayer.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.videobes.liveplayer.R
import com.videobes.liveplayer.util.MediaUtils
import com.videobes.liveplayer.model.MediaItem

class PlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var imageView: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var mediaList: List<MediaItem> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        videoView = findViewById(R.id.videoPlayer)
        imageView = findViewById(R.id.imagePlayer)

        // Carregar mídia local da pasta /LiveVideobes/media
        mediaList = MediaUtils.loadMedia()

        if (mediaList.isEmpty()) {
            imageView.setBackgroundColor(Color.BLACK)
            return
        }

        playNext()
    }

    private fun playNext() {
        if (mediaList.isEmpty()) return

        val item = mediaList[currentIndex]

        when (item.type) {
            "video" -> playVideo(item)
            "image" -> playImage(item)
        }

        currentIndex++
        if (currentIndex >= mediaList.size) currentIndex = 0
    }

    private fun playVideo(item: MediaItem) {
        imageView.visibility = View.GONE
        videoView.visibility = View.VISIBLE

        videoView.setVideoURI(Uri.fromFile(item.file))
        videoView.setOnCompletionListener {
            playNext()
        }
        videoView.start()
    }

    private fun playImage(item: MediaItem) {
        videoView.visibility = View.GONE
        imageView.visibility = View.VISIBLE

        imageView.setImageURI(Uri.fromFile(item.file))

        handler.postDelayed({
            playNext()
        }, item.duration * 1000L)
    }
}

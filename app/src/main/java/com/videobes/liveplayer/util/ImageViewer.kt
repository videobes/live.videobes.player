package com.videobes.liveplayer.util

import android.widget.ImageView
import android.net.Uri
import java.io.File

object ImageViewer {

    fun showImage(imageView: ImageView, file: File) {
        imageView.setImageURI(Uri.fromFile(file))
    }
}

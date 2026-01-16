package com.videobes.liveplayer.core

data class PlaylistConfig(
    val mode: PlaybackMode = PlaybackMode.RANDOM,
    val items: List<String> = emptyList()
)

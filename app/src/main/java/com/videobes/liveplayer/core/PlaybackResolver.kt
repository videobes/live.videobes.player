package com.videobes.liveplayer.core

import java.time.LocalTime

object PlaybackResolver {

    fun resolveMode(
        playlist: PlaylistConfig,
        schedule: ScheduleConfig?
    ): PlaybackMode {
        if (schedule == null || schedule.slots.isEmpty()) {
            return playlist.mode
        }

        val now = LocalTime.now()

        val active = schedule.slots.firstOrNull {
            val start = LocalTime.parse(it.start)
            val end = LocalTime.parse(it.end)
            now.isAfter(start) && now.isBefore(end)
        }

        return if (active != null) {
            PlaybackMode.SCHEDULED
        } else {
            playlist.mode
        }
    }
}

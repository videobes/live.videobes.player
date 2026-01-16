package com.videobes.liveplayer.core

data class ScheduleSlot(
    val start: String, // "08:00"
    val end: String,   // "12:00"
    val playlist: String
)

data class ScheduleConfig(
    val slots: List<ScheduleSlot> = emptyList()
)

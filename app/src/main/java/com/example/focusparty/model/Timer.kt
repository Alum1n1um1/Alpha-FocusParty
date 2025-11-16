package com.example.focusparty.model

import java.time.Duration
import java.util.Date

data class Timer(
    val state: TimerState = TimerState.NONE,
    val startTime: Date = Date(0),          // Utilisé uniquement en RUNNING
    val durationMs: Long = 0L,              // Durée totale (ms) lors du RUNNING
    val remainingMs: Long = 0L              // Restant lors du PAUSED
) {
    val duration: Duration get() = Duration.ofMillis(durationMs)
    val remaining: Duration get() = Duration.ofMillis(remainingMs)
}

enum class TimerState {
    NONE,
    RUNNING,
    PAUSED
}

package com.example.focusparty.utils

fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0m"
    val totalMinutes = ms / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h${minutes}m" else "${minutes}m"
}
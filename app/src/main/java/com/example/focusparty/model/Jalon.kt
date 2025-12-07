package com.example.focusparty.model

import java.time.LocalDateTime

data class Jalon (
    val name : String = "NameNotFound",
    val isDone : Boolean = false,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
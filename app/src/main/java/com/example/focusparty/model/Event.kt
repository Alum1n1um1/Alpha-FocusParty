package com.example.focusparty.model

import io.reactivex.Notification
import java.nio.channels.MembershipKey
import java.util.Date

data class Event(
    val name:String,
    val date_start: Date,
    val deadline:Date,
    val perodicity:String, // Comment gérer la périodicité ?
    val members:List<String>,
    val notif:List<String>,// "1sem" -> 1 semaine avant, etc... Plusieurs notifs possibles
    val priority:String // Urgent, Peut attendre, ...
)
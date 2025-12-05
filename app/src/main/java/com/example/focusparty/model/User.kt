package com.example.focusparty.model

import com.google.firebase.auth.FirebaseAuth

data class User(
    val uid:String,
    val email:String,
    val level:Int,
    val exp:Int,
    val friends:List<String>,
    val rooms:List<String>,
    val comment:String,
    val points:Int,
    val tempsTotal: Long = 0L,
    val jalonsTermines: Int = 0,
    val isConnected: Boolean = false,

    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val darkMode: Boolean = false,
    val notifications: Boolean = true
)

val auth = FirebaseAuth.getInstance()
val user = auth.currentUser
val uid= user?.uid ?: "KqCVHKRU54hwhAPo7aTj9mTyrur1" // temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

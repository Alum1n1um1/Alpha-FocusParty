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
    val points:Int
)

val auth = FirebaseAuth.getInstance()
val user = auth.currentUser
val uid= user?.uid ?: "KqCVHKRU54hwhAPo7aTj9mTyrur1" // temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

package com.example.focusparty.model

data class User(
    val uid:String,
    val email:String,
    val level:Int,
    val exp:Int,
    val friends:List<String>,
    val rooms:List<String>,
    val comment:String
)
package com.example.focusparty.model

data class Room(
    val id:String="",
    val name:String,
    val owner:String,
    val description:String,
    val status: Int,
    val members: List<String>,
    val jalons: List<Jalon>
)
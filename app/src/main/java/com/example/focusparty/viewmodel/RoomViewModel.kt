package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class RoomViewModel(
    private val db: Database,
    private val navController: NavController,
    private val roomId: String
) : ViewModel() {

    val room = MutableStateFlow<Room?>(null)

    init {
        viewModelScope.launch {
            db.getRoomById(roomId).collect { fetchedRoom  ->
                room.value = fetchedRoom
            }
        }
    }

    fun getCurrentRoom():Room {
        return room.value!!

    }


}
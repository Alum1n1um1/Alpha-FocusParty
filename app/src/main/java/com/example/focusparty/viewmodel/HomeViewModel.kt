package com.example.focusparty.viewmodel

import androidx.lifecycle.*
import com.example.focusparty.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val db: Database
) : ViewModel() {


    val rooms = db.getRooms().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val events = db.getEvents().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )


    fun GoToCalendar(){
    }

    fun GoToUserMenu(){
    }

    fun ShareApp(){
    }
}
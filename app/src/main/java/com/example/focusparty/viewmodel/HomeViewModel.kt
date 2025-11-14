package com.example.focusparty.viewmodel

import androidx.lifecycle.*
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val db: Database
) : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val tempUid = "KqCVHKRU54hwhAPo7aTj9mTyrur1" // temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    val rooms = db.getRoomsOf(user?.uid?:tempUid).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val events = db.getEventsOf(user?.uid?:tempUid).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    private val _availablePoints = MutableStateFlow(0)
    val availablePoints = _availablePoints
    private val _level = MutableStateFlow(0)
    val level = _level
    private val _exp = MutableStateFlow(0)
    val exp = _exp

    fun GoToCalendar(){
    }

    fun GoToUserMenu(){
    }

    fun ShareApp(){
    }

    fun GoToRoom(room: Room){

    }

    fun loadAvailablePoints() {
        viewModelScope.launch {
            _availablePoints.value = db.getUserPoints(user?.uid?:tempUid)
        }
    }

    fun loadLevel() {
        viewModelScope.launch {
            _level.value = db.getUserLevel(user?.uid?:tempUid)
        }
    }

    fun loadExp() {
        viewModelScope.launch {
        _exp.value = db.getUserExp(user?.uid?:tempUid)
        }
    }

    fun createRoom(name: String, description: String, jalons: List<String>) {
        val uid = auth.currentUser?.uid ?: return

        val newRoom = Room(
            name = name,
            owner = uid,
            description = description,
            status = 0,
            members = listOf(uid),
            jalons = jalons
        )

        viewModelScope.launch {
            db.addRoom(newRoom)
        }
    }
}
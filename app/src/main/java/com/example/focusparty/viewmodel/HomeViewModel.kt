package com.example.focusparty.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val db: Database,
    private val navController: NavController
) : ViewModel() {

    val rooms = db.getRoomsOf(uid).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val events = db.getEventsOf(uid).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    private val _availablePoints = MutableStateFlow(0)
    val availablePoints = _availablePoints
    private val _level = MutableStateFlow(0)
    val level = _level
    private val _exp = MutableStateFlow(0)
    val exp = _exp

    fun GoToCalendar(){
        navController.navigate("calendar")
    }

    fun GoToUserMenu(){
    }

    fun ShareApp(){
    }

    fun GoToRoom(room:Room){
        navController.navigate("room/"+room.id)
    }

    fun loadAvailablePoints() {
        viewModelScope.launch {
            _availablePoints.value = db.getUserPoints(uid)
        }
    }

    fun loadLevel() {
        viewModelScope.launch {
            _level.value = db.getUserLevel(uid)
        }
    }

    fun loadExp() {
        viewModelScope.launch {
        _exp.value = db.getUserExp(uid)
        }
    }

    fun createRoom(name: String, description: String, jalons: List<Jalon>) {
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

    fun handleDate(selectedDate: LocalDate) {

    }
}
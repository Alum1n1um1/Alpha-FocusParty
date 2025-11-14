package com.example.focusparty.viewmodel

import androidx.lifecycle.*
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val db: Database
) : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val tempUid = "x8uhlTxY68WKV4J9Dbph3YMbmGk1" // temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    val rooms = db.getRoomsOf(user?.uid?:tempUid).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val events = db.getEvents().stateIn(
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
}
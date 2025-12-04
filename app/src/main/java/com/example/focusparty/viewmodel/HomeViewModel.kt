package com.example.focusparty.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.focusparty.MyApplication
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val connectedCounts: StateFlow<Map<String, Int>> =
        rooms.flatMapLatest { roomList ->

            if (roomList.isEmpty()) {
                flowOf(emptyMap())
            } else {
                // Un flux par room
                val flows = roomList.map { room ->
                    db.getConnectedCount(room.members)
                        .map { count -> room.id to count }
                }

                combine(flows) { array ->
                    array.toMap() // Map<roomId, count>
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyMap()
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
        goToDestination("Calendar")
    }

    fun GoToUserMenu(){
    }

    fun ShareApp(){
    }

    fun GoToRoom(room:Room){
        goToDestination("Room/"+room.id)
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

        val newRoom = Room(
            name = name,
            owner = uid,
            description = description,
            status = 0,
            members = listOf(uid),
            jalons = jalons,
            timer = Timer()
        )

        viewModelScope.launch {
            db.addRoom(newRoom)
        }
    }

    fun handleDate(selectedDate: LocalDate) {

    }

    fun goToDestination(dest: String) {
        navController.navigate(dest)
    }

    fun goToStats() {
        TODO("Not yet implemented")
    }

    fun goToAvatarChanger() {
        TODO("Not yet implemented")
    }

    fun logout() {
        if (uid != null) {
            MyApplication.lifecycleListener.stopHeartbeat()

            CoroutineScope(Dispatchers.IO).launch {
                db.setUserConnected(uid, false)
            }
        }

        auth.signOut()

        navController.navigate("Login") {
            popUpTo("Home") { inclusive = true }
            launchSingleTop = true
        }
    }



    fun goToSettings() {
        navController.navigate("Settings")
    }


}
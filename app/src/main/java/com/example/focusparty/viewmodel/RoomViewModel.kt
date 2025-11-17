package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.time.Duration

class RoomViewModel(
    private val db: Database,
    private val navController: NavController,
    private val roomId: String
) : ViewModel() {

    val room = MutableStateFlow<Room?>(null)
    val roomState: StateFlow<Room?> =
        db.getRoomById(roomId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    private val _remaining = MutableStateFlow(Duration.ZERO)
    val remaining: StateFlow<Duration> = _remaining.asStateFlow()

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            db.getRoomById(roomId).collect { fetchedRoom  ->
                room.value = fetchedRoom
            }
        }
        startCountdownLoop()
    }

    private fun startCountdownLoop() {
        viewModelScope.launch {
            roomState.collect { room ->

                countdownJob?.cancel()
                countdownJob = null

                if (room == null) {
                    _remaining.value = Duration.ZERO
                    return@collect
                }

                val t = room.timer

                when (t.state) {

                    TimerState.NONE -> {
                        _remaining.value = Duration.ZERO
                    }

                    TimerState.PAUSED -> {
                        _remaining.value = Duration.ofMillis(t.remainingMs)
                    }

                    TimerState.RUNNING -> {
                        val start = t.startTime.time
                        val total = t.durationMs

                        countdownJob = launch {
                            while (true) {

                                val now = System.currentTimeMillis()
                                val elapsed = now - start
                                val remaining = total - elapsed

                                _remaining.value =
                                    Duration.ofMillis(remaining.coerceAtLeast(0L))

                                delay(250)
                            }
                        }
                    }
                }
            }
        }
    }


    fun getCurrentRoom():Room {
        return room.value!!

    }

    fun startPomodoro(duration: Duration) = viewModelScope.launch {
        db.startTimer(roomId, duration)
    }

    fun stopPomodoro() = viewModelScope.launch {
        db.stopTimer(roomId)
    }

    fun pausePomodoro() = viewModelScope.launch {
        val timer = roomState.value?.timer ?: return@launch
        db.pauseTimer(roomId, timer)
    }

    fun resumePomodoro() = viewModelScope.launch {
        val timer = roomState.value?.timer ?: return@launch
        db.resumeTimer(roomId, timer)
    }

    fun endJalon(index: Int, jalon: Jalon) = viewModelScope
        .launch{
        db.jalonIsDone(roomId, index, jalon)
    }





}
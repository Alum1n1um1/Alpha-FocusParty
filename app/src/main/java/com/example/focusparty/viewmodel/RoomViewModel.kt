package com.example.focusparty.viewmodel

import android.util.Log
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
    private var lastTimer: Timer? = null
    private var workedMs : Duration = Duration.ZERO

    init {
        viewModelScope.launch {
            db.getRoomById(roomId).collect { fetchedRoom  ->
                val old = lastTimer
                val new = fetchedRoom?.timer
                if (old != null && new != null) {
                    detectTimerEvents(old, new)
                }
                lastTimer = new
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

                                if (remaining <= 0L) {
                                    stopPomodoro()
                                    break
                                }

                                delay(250)
                            }
                        }
                    }
                }
            }
        }
    }

    fun startPomodoro(duration: Duration) = viewModelScope.launch {
        Log.w("DEBUG","")
        db.startTimer(roomId, duration)
    }

    fun stopPomodoro() = viewModelScope.launch {
        Log.w("DEBUG","")
        db.stopTimer(roomId)
    }

    fun pausePomodoro() = viewModelScope.launch {
        Log.w("DEBUG","")
        val timer = roomState.value?.timer ?: return@launch
        db.pauseTimer(roomId, timer)
    }

    fun resumePomodoro() = viewModelScope.launch {
        Log.w("DEBUG","resumePomodoro")
        val timer = roomState.value?.timer ?: return@launch
        db.resumeTimer(roomId, timer)
    }

    fun endJalon(index: Int, jalon: Jalon) = viewModelScope
        .launch{
        db.endJalon(roomId, index, jalon)
    }



    private fun detectTimerEvents(old: Timer, new: Timer) {
        Log.w("DEBUG","detectTimerEvents")

        // Cas où on quitte RUNNING → accumuler le temps écoulé
        if (old.state == TimerState.RUNNING &&
            (new.state == TimerState.PAUSED || new.state == TimerState.NONE)) {

            val now = System.currentTimeMillis()
            val elapsed = now - old.startTime.time
            workedMs = workedMs + Duration.ofMillis(elapsed)

            onTimerPaused()
        }

        // Cas reprise : ne rien accumuler
        if (old.state == TimerState.PAUSED && new.state == TimerState.RUNNING) {
            onTimerResumed()
        }

        // Cas fin timer : appeler la fin, mais ne rien accumuler ici
        if ((old.remainingMs > 0 && new.remainingMs == 0L) ||
            (new.state == TimerState.NONE && old.remainingMs > 0)) {

            onTimerFinishedInternal()
        }

    }

    private fun onTimerPaused() {
        Log.w("DEBUG","server timer paused")
    }

    private fun onTimerResumed() {
        Log.w("DEBUG","server timer resumed")
    }

    private fun onTimerFinishedInternal() {
        Log.w("DEBUG","onTimerFinishedInternal")
        onPomodoroFinished(workedMs.toMillis())
    }

    fun onPomodoroFinished(durationMs: Long) {
        viewModelScope.launch {
            val exp = 2500 * durationMs / 1000 / 60 / 30
            db.addExpToUser(uid, exp)
            db.addWorkedTimeToUser(uid, durationMs)
            db.addWorkedTimeToRoom(roomId, durationMs)
        }

        workedMs = Duration.ZERO
    }



}
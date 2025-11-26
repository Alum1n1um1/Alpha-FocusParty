package com.example.focusparty.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.util.Calendar
import java.util.Date


class CalendarViewModel(
    private val db: Database,
    private val navController: NavController
) : ViewModel() {
    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    fun openAddEventDialog() {
        showAddDialog = true
    }

    fun addEvent(
        name: String,
        dateStart: Date,
        deadline: Date,
        periodicity: String,
        members: List<String>,
        notif: List<String>,
        priority: String
    ) {
        val event = Event(
            name = name,
            date_start = dateStart,
            deadline = deadline,
            perodicity = periodicity,
            members = members,
            notif = notif,
            priority = priority
        )

        events = events + event
        showAddDialog = false
    }

    fun nextOccurrence(event: Event, from: Date = Date()): Date {
        val cal = Calendar.getInstance()
        cal.time = event.date_start

        if (from.after(cal.time)) {
            when (event.perodicity.lowercase()) {
                "daily" -> cal.add(Calendar.DAY_OF_YEAR, 1)
                "weekly" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                "monthly" -> cal.add(Calendar.MONTH, 1)
                "yearly" -> cal.add(Calendar.YEAR, 1)
                else -> {}
            }
        }
        return cal.time
    }

    fun parseNotifOffset(raw: String): Long {
        return when {
            raw.endsWith("min") -> raw.removeSuffix("min").toLong() * 60 * 1000
            raw.endsWith("h")   -> raw.removeSuffix("h").toLong() * 60 * 60 * 1000
            raw.endsWith("j")   -> raw.removeSuffix("j").toLong() * 24 * 60 * 60 * 1000
            raw.endsWith("sem") -> raw.removeSuffix("sem").toLong() * 7 * 24 * 60 * 60 * 1000
            else -> 0L
        }
    }

    fun computeNotifications(event: Event): List<Date> {
        return event.notif.map { raw ->
            val offsetMs = parseNotifOffset(raw)
            Date(event.date_start.time - offsetMs)
        }
    }

    fun selectDate(date: LocalDate) {
        // future: ouvrir panneau des événements du jour
    }

    fun importExternalCalendar() {
        // Implémentation future : lecture ICS,
        // synchronisation Google / Outlook via API ou URL ICS.
    }


}
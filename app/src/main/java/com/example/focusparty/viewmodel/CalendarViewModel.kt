package com.example.focusparty.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.focusparty.model.Database
import com.example.focusparty.model.Event
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CalendarViewModel(
    private val db: Database,
    private val navController: NavController,
    application: Application
) : AndroidViewModel(application) {

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    var filePickerRequest by mutableStateOf<Boolean?>(null)
        private set



    // --------------------
    // UI state
    // --------------------

    fun openAddEventDialog() {
        showAddDialog = true
    }

    fun closeAddEventDialog() {
        showAddDialog = false
    }

    fun importExternalCalendar() {
        // Utilisé par la TopBar : déclenche l'ouverture du picker ICS côté UI
        filePickerRequest = true
    }

    fun clearFilePickerRequest() {
        filePickerRequest = null
    }

    // --------------------
    // Gestion des événements
    // --------------------

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

        // Création dans le calendrier Google local du téléphone
        addEventToGoogleCalendar(event)

        showAddDialog = false
    }

    fun selectDate(date: LocalDate) {
        // Stub pour l’instant, utilisé par CalendarScreen
    }

    // --------------------
    // Import ICS
    // --------------------

    fun importICS(uri: Uri) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val input = ctx.contentResolver.openInputStream(uri) ?: return@launch
            val text = input.bufferedReader().use { it.readText() }

            val parsedEvents = parseICS(text)
            events = events + parsedEvents

            // On peut aussi pousser ces events dans Google Calendar si souhaité
            parsedEvents.forEach { addEventToGoogleCalendar(it) }
        }
    }

    private fun parseICS(ics: String): List<Event> {
        val lines = ics.lines()

        val result = mutableListOf<Event>()

        var name = ""
        var dtStart: Date? = null
        var dtEnd: Date? = null

        lines.forEach { line ->
            when {
                line.startsWith("SUMMARY:") ->
                    name = line.removePrefix("SUMMARY:")

                line.startsWith("DTSTART") -> {
                    dtStart = parseIcsDate(line.substringAfter(":"))
                }

                line.startsWith("DTEND") -> {
                    dtEnd = parseIcsDate(line.substringAfter(":"))
                }

                line == "END:VEVENT" -> {
                    if (dtStart != null && dtEnd != null) {
                        val event = Event(
                            name = name,
                            date_start = dtStart!!,
                            deadline = dtEnd!!,
                            perodicity = "none",
                            members = emptyList(),
                            notif = emptyList(),
                            priority = "Normal"
                        )
                        result.add(event)
                    }
                    name = ""
                    dtStart = null
                    dtEnd = null
                }
            }
        }

        return result
    }

    private fun parseIcsDate(raw: String): Date {
        return try {
            if (raw.endsWith("Z")) {
                // Format type : 20250101T120000Z
                val instant = Instant.parse(
                    raw.substring(0, 4) + "-" +
                            raw.substring(4, 6) + "-" +
                            raw.substring(6, 8) + "T" +
                            raw.substring(9, 11) + ":" +
                            raw.substring(11, 13) + ":" +
                            raw.substring(13, 15) + "Z"
                )
                Date.from(instant)
            } else if (raw.contains("T")) {
                // Format local : 20250101T120000
                val datePart = raw.substring(0, 8)
                val year = datePart.substring(0, 4).toInt()
                val month = datePart.substring(4, 6).toInt()
                val day = datePart.substring(6, 8).toInt()

                val hour = raw.substring(9, 11).toInt()
                val min = raw.substring(11, 13).toInt()
                val sec = raw.substring(13, 15).toInt()

                Calendar.getInstance().apply {
                    set(year, month - 1, day, hour, min, sec)
                }.time
            } else {
                // Format date seule : 20250101
                val year = raw.substring(0, 4).toInt()
                val month = raw.substring(4, 6).toInt()
                val day = raw.substring(6, 8).toInt()

                Calendar.getInstance().apply {
                    set(year, month - 1, day, 0, 0, 0)
                }.time
            }
        } catch (e: Exception) {
            Date()
        }
    }

    // --------------------
    // Intégration Google Calendar (Calendar Provider)
    // --------------------

    private fun addEventToGoogleCalendar(event: Event) {
        val ctx = getApplication<Application>()
        val calId = getGoogleCalendarId(ctx) ?: return

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, event.date_start.time)
            put(CalendarContract.Events.DTEND, event.deadline.time)
            put(CalendarContract.Events.TITLE, event.name)
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = ctx.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        val eventId = uri?.lastPathSegment?.toLongOrNull() ?: return

        // Création des rappels à partir de notif : ["1sem", "2j", "30min", ...]
        event.notif.forEach { raw ->
            val offsetMs = parseNotifOffset(raw)
            if (offsetMs <= 0L) return@forEach

            val minutes = (offsetMs / 1000L / 60L).toInt()

            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.EVENT_ID, eventId)
                put(CalendarContract.Reminders.MINUTES, minutes)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }

            ctx.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
        }
    }

    private fun getGoogleCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val uri = CalendarContract.Calendars.CONTENT_URI
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        ) ?: return null

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val displayName = it.getString(1)
                val accountType = it.getString(2)

                // On privilégie les calendriers Google
                if (accountType == "com.google") {
                    return id
                }
            }
        }

        return null
    }

    private fun parseNotifOffset(raw: String): Long {
        return when {
            raw.endsWith("min") ->
                raw.removeSuffix("min").toLongOrNull()?.times(60_000L) ?: 0L

            raw.endsWith("h") ->
                raw.removeSuffix("h").toLongOrNull()?.times(60L * 60_000L) ?: 0L

            raw.endsWith("j") ->
                raw.removeSuffix("j").toLongOrNull()?.times(24L * 60L * 60_000L) ?: 0L

            raw.endsWith("sem") ->
                raw.removeSuffix("sem").toLongOrNull()?.times(7L * 24L * 60L * 60_000L) ?: 0L

            else -> 0L
        }
    }

    fun loadDeviceCalendarEvents() {
        val ctx = getApplication<Application>()

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DESCRIPTION
        )

        val cursor = ctx.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            null,
            null,
            "${CalendarContract.Events.DTSTART} ASC"
        ) ?: return

        val list = mutableListOf<Event>()

        cursor.use {
            while (it.moveToNext()) {
                val title = it.getString(1) ?: ""
                val dtStart = Date(it.getLong(2))
                val dtEnd = Date(it.getLong(3))

                val event = Event(
                    name = title,
                    date_start = dtStart,
                    deadline = dtEnd,
                    perodicity = "none",
                    members = emptyList(),
                    notif = emptyList(),
                    priority = "Normal"
                )

                list.add(event)
            }
        }

        events = list
    }

}

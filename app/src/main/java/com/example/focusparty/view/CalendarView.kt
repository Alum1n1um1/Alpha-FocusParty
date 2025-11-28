package com.example.focusparty.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.focusparty.model.Event
import com.example.focusparty.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    vm: CalendarViewModel
) {
    val ctx = LocalContext.current

    // Permissions calendrier
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            )
        )
        vm.loadDeviceCalendarEvents()
    }
    LaunchedEffect(Unit) {
        vm.loadDeviceCalendarEvents()
    }


    // Picker ICS
    val icsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) vm.importICS(uri)
    }

    LaunchedEffect(vm.filePickerRequest) {
        if (vm.filePickerRequest == true) {
            icsLauncher.launch("text/calendar")
            vm.clearFilePickerRequest()
        }
    }

    Scaffold(
        topBar = {
            CalendarTopBar(
                onImportClick = { vm.importExternalCalendar() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.openAddEventDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            SimpleCalendar(
                events = vm.events,
                onDayClick = { date -> vm.selectDate(date) }
            )
            if (vm.showAddDialog) {
                AddEventDialog(
                    onDismiss = { vm.closeAddEventDialog() },
                    onValidate = { event ->
                        vm.addEvent(
                            name = event.name,
                            dateStart = event.date_start,
                            deadline = event.deadline,
                            periodicity = event.perodicity,
                            members = event.members,
                            notif = event.notif,
                            priority = event.priority
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SimpleCalendar(
    events: List<Event>,
    onDayClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Recalcul dynamique selon currentMonth
    val firstDay = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayColumn = firstDay.dayOfWeek.value   // 1 = lundi
    val leadingEmptyCells = firstDayColumn - 1
    val totalCells = leadingEmptyCells + daysInMonth

    fun hasEventsFor(date: LocalDate): Boolean {
        return events.any { e ->
            e.date_start.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == date
        }
    }

    Column {

        // HEADER MOIS + BOUTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mois précédent")
            }

            Text(
                text = formatMonth(currentMonth),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mois suivant")
            }
        }

        Spacer(Modifier.height(8.dp))

        // EN-TÊTE SEMAINE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) { Text(label) }
            }
        }

        Spacer(Modifier.height(8.dp))

        // GRILLE
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {

            items(totalCells) { index ->

                if (index < leadingEmptyCells) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                    ) {}
                } else {
                    val day = index - leadingEmptyCells + 1
                    val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
                    val hasEvent = hasEventsFor(date)

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .background(
                                if (hasEvent) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onDayClick(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$day",
                            color = if (hasEvent)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(onImportClick: () -> Unit) {
    TopAppBar(
        title = { Text("Calendrier") },
        actions = {
            IconButton(onClick = onImportClick) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = "Importer calendrier"
                )
            }
        },
        modifier = Modifier.height(70.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onValidate: (Event) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dateStart by remember { mutableStateOf(LocalDate.now()) }
    var deadline by remember { mutableStateOf(LocalDate.now()) }
    var periodicity by remember { mutableStateOf("Aucune") }
    var notifRaw by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") }
    var isTask by remember { mutableStateOf(false) }

    val periodicityOptions = listOf("Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle", "Annuelle")
    val priorityOptions = listOf("Urgent", "Normal", "Peut attendre")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel élément") },
        text = {
            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // DATE DEBUT
                OutlinedTextField(
                    value = dateStart.toString(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* ouvre ton date picker */ },
                    label = { Text("Date de début") },
                    readOnly = true
                )

                Spacer(Modifier.height(8.dp))

                // DEADLINE
                OutlinedTextField(
                    value = deadline.toString(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* ouvre ton date picker */ },
                    label = { Text("Deadline") },
                    readOnly = true
                )

                Spacer(Modifier.height(16.dp))

                // PERIODICITE
                DropdownMenuField(
                    label = "Périodicité",
                    options = periodicityOptions,
                    selected = periodicity,
                    onSelect = { periodicity = it }
                )

                Spacer(Modifier.height(16.dp))

                // NOTIFICATIONS
                OutlinedTextField(
                    value = notifRaw,
                    onValueChange = { notifRaw = it },
                    label = { Text("Notifications (séparateur ;)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // PRIORITE
                DropdownMenuField(
                    label = "Priorité",
                    options = priorityOptions,
                    selected = priority,
                    onSelect = { priority = it }
                )

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isTask,
                        onCheckedChange = { isTask = it }
                    )
                    Text("Tâche")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val notifList = notifRaw.split(";")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    val event = Event(
                        name = title,
                        date_start = Date.from(dateStart.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        deadline = Date.from(deadline.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        perodicity = periodicity,
                        members = emptyList(),
                        notif = notifList,
                        priority = priority
                    )

                    onValidate(event)
                }
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun formatMonth(month: YearMonth): String {
    return when (month.month) {
        java.time.Month.JANUARY -> "Janvier"
        java.time.Month.FEBRUARY -> "Février"
        java.time.Month.MARCH -> "Mars"
        java.time.Month.APRIL -> "Avril"
        java.time.Month.MAY -> "Mai"
        java.time.Month.JUNE -> "Juin"
        java.time.Month.JULY -> "Juillet"
        java.time.Month.AUGUST -> "Août"
        java.time.Month.SEPTEMBER -> "Septembre"
        java.time.Month.OCTOBER -> "Octobre"
        java.time.Month.NOVEMBER -> "Novembre"
        java.time.Month.DECEMBER -> "Décembre"
    } + " " + month.year
}

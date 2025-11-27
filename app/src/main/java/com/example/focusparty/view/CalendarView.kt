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
                    onValidate = { title, date, isTask ->
                        val dateStart = Date.from(
                            date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        )
                        // +30 minutes
                        val deadline = Date(dateStart.time + 30 * 60 * 1000)

                        vm.addEvent(
                            name = title,
                            dateStart = dateStart,
                            deadline = deadline,
                            periodicity = "none",
                            members = emptyList(),
                            notif = emptyList(),
                            priority = if (isTask) "Normal" else "Urgent"
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
    val currentMonth = YearMonth.now()
    val firstDay = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()

    // Jour de la semaine du 1er : 1 = lundi, 7 = dimanche
    val firstDayColumn = firstDay.dayOfWeek.value

    // Nombre de cases vides avant le jour 1
    val leadingEmptyCells = firstDayColumn - 1

    val totalCells = leadingEmptyCells + daysInMonth

    Column {
        var currentMonth by remember { mutableStateOf(YearMonth.now()) }

        val firstDay = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayColumn = firstDay.dayOfWeek.value   // 1 = lundi
        val leadingEmptyCells = firstDayColumn - 1
        val totalCells = leadingEmptyCells + daysInMonth


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Mois précédent"
                )
            }

            Text(
                text = when (currentMonth.month.name){
                    "JANUARY" -> "Janvier"
                    "FEBRUARY" -> "Février"
                    "MARCH" -> "Mars"
                    "APRIL" -> "Avril"
                    "MAY" -> "Mai"
                    "JUNE" -> "Juin"
                    "JULY" -> "Juillet"
                    "AUGUST" -> "Août"
                    "SEPTEMBER" -> "Septembre"
                    "OCTOBER" -> "Octobre"
                    "NOVEMBER" -> "Novembre"
                    "DECEMBER" -> "Décembre"
                    else -> currentMonth.month.name
                }+" " + currentMonth.year.toString(),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)
            }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Mois suivant"
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // en-tête L M M J V S D
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {

            items(totalCells) { index ->

                if (index < leadingEmptyCells) {
                    // case vide avant le 1er
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                    ) {}
                } else {
                    val day = index - leadingEmptyCells + 1
                    val date = LocalDate.of(currentMonth.year, currentMonth.month, day)

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clickable { onDayClick(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$day")
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
    onValidate: (String, LocalDate, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var isTask by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel élément") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") }
                )

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
            TextButton(onClick = { onValidate(title, date, isTask) }) {
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

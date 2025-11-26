package com.example.focusparty.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.focusparty.model.Event
import com.example.focusparty.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    vm: CalendarViewModel
) {
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
        }
    }
}

@Composable
fun SimpleCalendar(
    events: List<Event>,
    onDayClick: (LocalDate) -> Unit
) {
    val currentMonth = YearMonth.now()
    val days = currentMonth.atEndOfMonth().dayOfMonth

    Column {
        Text(
            text = currentMonth.month.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ) {
            items(days) { index ->
                val day = index + 1
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
        }
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
        }
    )
}

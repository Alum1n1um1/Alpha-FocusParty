package com.example.focusparty.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.focusparty.model.Event
import com.example.focusparty.model.Room
import com.example.focusparty.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(vm: HomeViewModel) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        HomeTopBar(vm)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Stats(vm)
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .weight(1f)
            )

            Box(modifier = Modifier.weight(2f)) {
                RoomsSection(vm)
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .weight(1f)
            )

            Box(modifier = Modifier.weight(2f)) {
                EventSection(vm)
            }
        }
    }
}


@Composable
fun HomeTopBar(vm: HomeViewModel) {

    Surface(){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- Zone gauche ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = { vm.GoToCalendar() },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Calendrier")
                }
            }

            // --- Zone centrale ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val date = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                Text(
                    text = date.format(formatter)
                )
            }

            // --- Zone droite ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    IconButton(
                        onClick = { vm.GoToUserMenu() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Compte")
                    }

                    IconButton(
                        onClick = { vm.ShareApp() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Partager")
                    }
                }
            }
        }
    }
}


@Composable
fun Stats(vm: HomeViewModel){
    val level by vm.level.collectAsState()
    val maxExp = 50f*level.toFloat()
    val exp by vm.exp.collectAsState()
    val progress = (exp/maxExp).toFloat()
    val points by vm.availablePoints.collectAsState()
    LaunchedEffect(Unit) {
        vm.loadLevel()
    }
    LaunchedEffect(Unit) {
        vm.loadExp()
    }
    LaunchedEffect(Unit) {
        vm.loadAvailablePoints()
    }
    Surface() {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LVL" + level.toString(),
                        modifier = Modifier.width(48.dp),
                        textAlign = TextAlign.Center
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                    )

                    Text(
                        text = "LVL" + (level + 1).toString(),
                        modifier = Modifier.width(48.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Points disponibles : " + points)
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Temps passé cette semaine : TODO")
                }
            }
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // TODO le graphique du temps passé depuis 1sem
            }
        }
    }
}


@Composable
fun RoomsSection(vm: HomeViewModel) {
    val rooms by vm.rooms.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateRoomDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, description, jalons ->
                vm.createRoom(name, description, jalons)
                showDialog = false
            }
        )
    }

    Surface(color = MaterialTheme.colorScheme.surface) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                AddRoomButton { showDialog = true }
            }

            items(rooms) { room ->
                RoomItem(room,vm)
            }
        }
    }
}


@Composable
fun AddRoomButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Ajouter un salon")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            Icons.Default.Add,
            contentDescription = "Ajouter un salon"
        )
    }
}


@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var jalonsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer un salon") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                OutlinedTextField(
                    value = jalonsText,
                    onValueChange = { jalonsText = it },
                    label = { Text("Jalons (séparés par des virgules)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val jalons = jalonsText
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    onConfirm(name, description, jalons)
                }
            ) {
                Text("Créer")
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
fun RoomItem(room:Room,vm: HomeViewModel){ // 1 salon, TODO : name + nombre de personnes dans le salon + lvl du salon (avec icone d'arbre)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        onClick =  { vm.GoToRoom(room) }
    ){
        Row(

        ){
            Text(text = room.name)
            Row (

            ){
                Column(

                ){
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "personnage"
                    )
                    Text(text="TODO") // TODO : nombre de personnes dans le salon
                }
                Icon(
                    Icons.Default.Nature,
                    contentDescription = "arbre ou plante"
                )
            }
        }

    }
}


@Composable
fun EventSection(vm: HomeViewModel) {
    val events by vm.events.collectAsState()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
    ){
        LazyColumn {
            items(events) { event ->
                EventItem(event)
            }
        }
    }
}


@Composable
fun EventItem(event:Event){ // 1 event, TODO : name + deadline + priorité (urgence ?)
    Surface(
        color = MaterialTheme.colorScheme.surface,
    ){
        Row(){
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text=event.name,
                    color=MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.weight(1f)
            ){
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                Text(
                    text=event.deadline
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(formatter),
                    color=MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text=event.priority,
                    color=MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.focusparty.model.Event
import com.example.focusparty.model.Jalon
import com.example.focusparty.model.Room
import com.example.focusparty.ui.components.CustomSurface
import com.example.focusparty.ui.components.HomeBottomBar
import com.example.focusparty.ui.components.SurfaceLevel
import com.example.focusparty.ui.components.TreeLevelImage
import com.example.focusparty.ui.theme.*
import com.example.focusparty.utils.formatDuration
import com.example.focusparty.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    navController: NavHostController
) {

    Scaffold(
        topBar = {
            HomeTopBar(vm)
        },
        bottomBar = {
            HomeBottomBar(
                currentDestination = "home",
                onNavigate = { dest -> navController.navigate(dest) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                .background(MaterialTheme.colorScheme.background)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    Stats(vm)
                }

                Box(modifier = Modifier.weight(2f)) {
                    RoomsSection(vm)
                }


                Box(modifier = Modifier.weight(2f)) {
                    EventSection(vm)
                }
            }
        }
    }
}



@Composable
fun HomeTopBar(vm: HomeViewModel) {

    Surface(
        color = MaterialTheme.colorScheme.primary
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars),
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
                    text = date.format(formatter),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            // --- Zone droite ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    UserMenuButton(vm)

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
fun UserMenuButton(vm: HomeViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Compte")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Changer avatar") },
                onClick = {
                    expanded = false
                    vm.goToAvatarChanger()
                }
            )
            DropdownMenuItem(
                text = { Text("Stats") },
                onClick = {
                    expanded = false
                    vm.goToStats()
                }
            )
            DropdownMenuItem(
                    text = { Text("Paramètres") },
            onClick = {
                expanded = false
                vm.goToSettings()
            }
            )
            DropdownMenuItem(
                text = { Text("Logout") },
                onClick = {
                    expanded = false
                    vm.logout()
                }
            )
        }
    }
}


@Composable
fun Stats(vm: HomeViewModel){
    val level by vm.level.collectAsState()
    val maxExp = 50f*level.toFloat()
    val exp by vm.exp.collectAsState()
    val progress = (exp/maxExp)
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
    CustomSurface(
        level = SurfaceLevel.High,
        color = colorPurplelight2,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
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
                        text = "LVL$level",
                        modifier = Modifier
                            .width(60.dp)
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                    )

                    Text(
                        text = "LVL" + (level + 1).toString(),
                        modifier = Modifier
                            .width(60.dp)
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Points disponibles : $points",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start=12.dp)
                    )

                }
                val time = vm.workedTime

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Temps révisé : ${formatDuration(time)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start=12.dp))
                }
            }
            //Canvas ??
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

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                AddRoomButton { showDialog = true }
            }
            items(rooms) { room ->
                RoomItem(room, vm)
            }

        }
    }
}

@Composable
fun AddRoomButton(onClick: () -> Unit) {
    Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter un salon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajouter un salon")
        }

}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<Jalon>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var jalonsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer un salon") },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                        .map { Jalon(
                            name = it,
                            isDone = false
                            ) }

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
fun RoomItem(room:Room,vm: HomeViewModel){

    CustomSurface(
        level = SurfaceLevel.High,
        onClick =  { vm.GoToRoom(room) },
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = room.name,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                fontSize = 20.sp
            )
            Row (
                modifier = Modifier
                    .width(72.dp)
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 8.dp)
                ){
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "logo personnage",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    val rooms by vm.rooms.collectAsState()
                    val counts by vm.connectedCounts.collectAsState()
                    Text(
                        text = (counts[room.id] ?: 0).toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                TreeLevelImage(
                    level = room.level,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
fun EventSection(vm: HomeViewModel) {
    val events by vm.events.collectAsState()
    CustomSurface(
        level = SurfaceLevel.Low
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
    CustomSurface(
        level = SurfaceLevel.Normal,
        color = if (event.priority != "URG") {MaterialTheme.colorScheme.surface} else {MaterialTheme.colorScheme.error},
        modifier = Modifier
            .height(40.dp)
            .padding(5.dp)
    ){
        Row {
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text=event.name,
                    color=if (event.priority != "URG") {MaterialTheme.colorScheme.onSurface} else {MaterialTheme.colorScheme.onError},
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
                    color= if (event.priority != "URG") {MaterialTheme.colorScheme.onSurfaceVariant} else {MaterialTheme.colorScheme.onError},
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
                    color=if (event.priority != "URG") {MaterialTheme.colorScheme.onSurface} else {MaterialTheme.colorScheme.onError},
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}








package com.example.focusparty.view

import android.widget.ProgressBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.focusparty.model.Database
import com.example.focusparty.model.Event
import com.example.focusparty.model.Room
import com.example.focusparty.ui.theme.Surface
import com.example.focusparty.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .weight(1f)
            )

            Box(modifier = Modifier.weight(2f)) {
                RoomsSection(vm)
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary,
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
                        contentColor = MaterialTheme.colorScheme.secondary
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
                val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy")
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
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Compte")
                    }

                    IconButton(
                        onClick = { vm.ShareApp() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
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
fun RoomsSection(vm: HomeViewModel){
    val rooms by vm.rooms.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(rooms) { room ->
                RoomItem(room)
            }
        }
    }
}

@Composable
fun EventSection(vm: HomeViewModel) {
    val events by vm.events.collectAsState()
    Surface(
        color = MaterialTheme.colorScheme.surface,
    ){
        LazyColumn {
            items(events) { event ->
                EventItem(event)
            }
        }
    }
}


@Composable
fun RoomItem(room:Room){ // 1 salon, TODO : name + nombre de personnes dans le salon + lvl du salon (avec icone d'arbre)
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
    ){
        Text(text = room.name)
    }

}
@Composable
fun EventItem(event:Event){ // 1 event, TODO : name + deadline + priorité (urgence ?)
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
    ){
        Text(text = event.name)
    }
}

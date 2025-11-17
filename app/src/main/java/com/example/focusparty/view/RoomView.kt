package com.example.focusparty.view

import android.widget.NumberPicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.focusparty.model.Jalon
import com.example.focusparty.model.Room
import com.example.focusparty.viewmodel.RoomViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.Duration
import kotlin.properties.ReadOnlyProperty
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.focusparty.model.TimerState


@Composable
fun RoomScreen(
    vm: RoomViewModel
) {
    val room by vm.room.collectAsState()

    if (room == null) {
        Text("Chargement du salon…")
    } else {
        RoomContent(
            vm=vm,
            room = room!!
        )
    }
}

@Composable
fun RoomContent(
    vm: RoomViewModel,
    room: Room)
{
    Column {
        SalonTopBar(vm, room)
        DashBoard(vm)
        Pomodoro(
            vm=vm,
            room=room
        )
        ActionsMenu(vm)
    }
}
@Composable
fun SalonTopBar(vm: RoomViewModel, room: Room) {

    Surface()
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        )
        {

            // --- Zone gauche ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            )
            {
                Text(room.name)
            }

            // --- Zone centrale ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            )
            {
                Row()
                {
                    Text(room.status.toString())
//                    IconButton(
//                        {},
//                        colors = IconButtonDefaults.iconButtonColors(
//                            contentColor = MaterialTheme.colorScheme.onPrimary
//                        )
//                    )
//                    {
//                        Icon(Icons.Default.Pause, "Changer le statut")
//                    }
                }
            }

            // --- Zone droite ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            )
            {
                Text("Personnes connectées")
                Icon(Icons.Default.CoPresent, "Nombre de personnes connectés")
            }
        }
    }
}

@Composable
fun ActionsMenu(vm: RoomViewModel) {
    Column(){
        RoomStats(vm)
        LazyColumn() {
            items(items=vm.getCurrentRoom().jalons) { jalon ->
                JalonItem(
                    vm,
                    jalon=jalon,
                    onFinish={},
                    onSettings={}
                )
            }
        }
    }
}

@Composable
fun DashBoard(vm: RoomViewModel) {



}

@Composable
fun RoomStats(vm: RoomViewModel) {
}

@Composable
fun JalonItem(
    vm: RoomViewModel,
    jalon: Jalon,
    onFinish: () -> Unit,
    onSettings: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Box(Modifier.weight(2f), Alignment.CenterStart)
        {
            Text(text=jalon.name+"/"+jalon.isDone.toString())
        }
        Box(Modifier.weight(2f), Alignment.Center)
        {
            if(jalon.isDone)
            {
                Text("Terminé")
            }
            else
            {
                Button({vm.endJalon(jalon)})
                {
                    Text("Terminer")
                }
            }
        }
        Box(Modifier.weight(1f), Alignment.CenterEnd)
        {
            IconButton(
                {}
            )
            {
                Icon(Icons.Default.SettingsSuggest, "Paramètre du jalon")
            }
        }
    }
}

@Composable
fun Pomodoro(
    vm: RoomViewModel,
    room: Room
) {
    val remaining by vm.remaining.collectAsState()
    val timer = room.timer
    val state = timer.state

    // ---------------------------------------------------------
    // A. Aucun timer (state = NONE)
    // ---------------------------------------------------------
    if (state == TimerState.NONE) {

        var hours by remember { mutableStateOf(0) }
        var minutes by remember { mutableStateOf(25) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box (
                modifier=Modifier.weight(1f)
            ) {
                NumberPicker(
                    label = "Heures",
                    value = hours,
                    range = 0..5
                ) {
                    hours = it
                }
            }
            Box (
                modifier=Modifier.weight(1f)
            ) {
                NumberPicker(label = "Minutes", value = minutes, range = 0..59) {
                    minutes = it
                }
            }
            Box (
                modifier=Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val totalMinutes = hours * 60 + minutes
                        val duration = Duration.ofMinutes(totalMinutes.toLong())
                        vm.startPomodoro(duration)
                    }
                ) {
                    Text("Lancer")
                }
            }
        }
        return
    }

    // ---------------------------------------------------------
    // Préparation valeurs communes RUNNING / PAUSED
    // ---------------------------------------------------------
    val totalMs = timer.durationMs
    val remainingMs = remaining.toMillis()
    val doneMs = (totalMs - remainingMs).coerceAtLeast(0L)
    val progress = (doneMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)


    val m = remaining.toMinutes()
    val s = remaining.toSeconds() % 60

    // ---------------------------------------------------------
    // B. Timer en pause (state = PAUSED)
    // ---------------------------------------------------------
    if (state == TimerState.PAUSED) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "%02d:%02d".format(m, s),
                style = MaterialTheme.typography.headlineMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { vm.resumePomodoro() }) {
                    Text("Reprendre")
                }
                Button(onClick = { vm.stopPomodoro() }) {
                    Text("Arrêter")
                }
            }
        }
        return
    }

    // ---------------------------------------------------------
    // C. Timer en cours (state = RUNNING)
    // ---------------------------------------------------------
    if (state == TimerState.RUNNING) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "%02d:%02d".format(m, s),
                style = MaterialTheme.typography.headlineMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { vm.pausePomodoro() }) {
                    Text("Pause")
                }
                Button(onClick = { vm.stopPomodoro() }) {
                    Text("Arrêter")
                }
            }
        }

    }
}


@Composable
fun NumberPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(label, style = MaterialTheme.typography.bodyMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            IconButton(
                onClick = {
                    val new = (value - 1).coerceIn(range)
                    onValueChange(new)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = {
                    val new = (value + 1).coerceIn(range)
                    onValueChange(new)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }
        }
    }
}





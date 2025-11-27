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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.focusparty.model.TimerState
import com.example.focusparty.ui.components.*
import com.example.focusparty.ui.theme.*


@Composable
fun RoomScreen(
    vm: RoomViewModel
) {
    val room by vm.roomState.collectAsState()

    if (room == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Text(
                text = "Chargement du salon …",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
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
        ActionsMenu(vm,room)
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
                .background(MaterialTheme.colorScheme.primary)
        )
        {

            // --- Zone gauche ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            )
            {
                Text("")
            }

            // --- Zone centrale ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            )
            {
                Text(room.name)
            }

            // --- Zone droite ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            )
            {
                Text("1")
                Icon(Icons.Default.CoPresent, "Nombre de personnes connectés")
            }
        }
    }
}

@Composable
fun ActionsMenu(vm: RoomViewModel,room : Room) {
    CustomSurface(level = SurfaceLevel.Low) {
        Column() {
            RoomStats(vm)
            LazyColumn() {
                itemsIndexed(items = room.jalons) { index, jalon ->
                    JalonItem(
                        jalon = jalon,
                        onFinish = {
                            vm.endJalon(index, jalon.copy(isDone=true)) },
                        onSettings = {}
                    )
                }
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
    jalon: Jalon,
    onFinish: () -> Unit,
    onSettings: () -> Unit
) {
    val surfaceColor = if (jalon.isDone){ colorGreenlight3 } else {colorYellowlight3}
    val onSurfaceColor = if (jalon.isDone){ colorGreendark3 } else {colorYellowdark3}

    CustomSurface(
        level = SurfaceLevel.High,
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            // Décalage gauche
            Spacer(Modifier.width(8.dp))

            // Zone texte : prend tout l’espace disponible
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = jalon.name,
                    color = onSurfaceColor
                )
            }

            // Zone bouton avec largeur fixe
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (jalon.isDone) {
                    Text(
                        text = "Terminé",
                        color = onSurfaceColor
                    )
                } else {
                    Button(onClick = onFinish) {
                        Text(
                            "Terminer",
                            color = onSurfaceColor
                        )
                    }
                }
            }

            // Zone icône avec largeur fixe
            Box(
                modifier = Modifier
                    .width(35.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onSettings) {
                    Icon(
                        Icons.Default.SettingsSuggest,
                        contentDescription = "Paramètres du jalon"
                    )
                }
            }
        }
    }
}

@Composable
fun Pomodoro(
    vm: RoomViewModel,
    room: Room
) {
    CustomSurface(
        level = SurfaceLevel.High,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ){
        val remaining by vm.remaining.collectAsState()
        val timer = room.timer
        val state = timer.state

        // ---------------------------------------------------------
        // A. Aucun timer (state = NONE)
        // ---------------------------------------------------------
        if (state == TimerState.NONE) {

            var hours by remember { mutableStateOf(0) }
            var minutes by remember { mutableStateOf(30) } /////////////////////CHANGER POUR 25


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    NumberPicker(
                        label = "Heures",
                        value = hours,
                        range = 0..5
                    ) {
                        hours = it
                    }
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    NumberPicker(label = "Minutes", value = minutes, range = 0..59) {
                        minutes = it
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
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





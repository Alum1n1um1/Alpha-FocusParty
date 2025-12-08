package com.example.focusparty.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusparty.model.Jalon
import com.example.focusparty.model.Room
import com.example.focusparty.viewmodel.RoomViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Duration
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.focusparty.model.TimerState
import com.example.focusparty.model.User
import com.example.focusparty.ui.components.*
import com.example.focusparty.ui.theme.*
import com.example.focusparty.utils.formatDuration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
    Column (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ){
        SalonTopBar(vm, room)
        DashBoard(
            vm=vm,
            room=room
        )
        Pomodoro(
            vm=vm,
            room=room
        )
        ActionsMenu(vm,room)
    }
}

@Composable
fun SalonTopBar(
    vm: RoomViewModel,
    room: Room
) {

    val tint = MaterialTheme.colorScheme.onPrimary

    Surface(
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)    // Hauteur standard Material TopBar
                .windowInsetsPadding(WindowInsets.statusBars),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                // bouton retour ?
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = room.name,
                    color = tint,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                val connectedCount by vm.connectedCount.collectAsState()
                val room by vm.roomState.collectAsState()

                // Zone cliquable : nombre + icône
                var showMenu by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { showMenu = true }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "$connectedCount",
                        color = tint,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Icon(
                        imageVector = Icons.Default.CoPresent,
                        contentDescription = "Personnes connectées",
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (showMenu) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                onClick = { showMenu = false },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    ) {
                        MemberList(
                            onDismiss = { showMenu = false },
                            room=room!!,
                            vm=vm
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun MemberList(
    onDismiss: () -> Unit,
    room:Room,
    vm : RoomViewModel
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss
    ) {
        val members = room.members
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(15.dp, 50.dp, 15.dp, 15.dp)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            items(members.size) { index ->
                val uid = members[index]
                val userState by vm.getUserOfId(uid).collectAsState(initial = null)

                val user = userState
                if (user == null) {
                    PlaceholderUserItem()
                } else {
                    MemberItem(
                        user = user,
                        onAddFriend = {
                            vm.addFriend(uid)
                            onDismiss
                        },
                        onMotivate = {
                            vm.motivate(uid)
                            onDismiss
                        }
                    )
                }
                if (index < members.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ActionsMenu(vm: RoomViewModel,room : Room) {
    CustomSurface(level = SurfaceLevel.Low) {
        Column() {
            RoomStats(vm)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items = room.jalons) { index, jalon ->
                    JalonItem(
                        jalon = jalon,
                        onFinish = {
                            vm.endJalon(index, jalon.copy(isDone=true)) },
                        { newJalon ->
                            vm.modifyJalon(index, newJalon)
                        },
                        {
                            vm.deleteJalon(index)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashBoard(
    vm: RoomViewModel,
    room: Room
) {
    CustomSurface(
        level = SurfaceLevel.Low,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        tonalElevation = 10.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TreeLevelImage(
                level = room.level,
                modifier = Modifier.size(140.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Niveau
                Text(
                    text = "Niveau ${room.level}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Calcul progression EXP interne au niveau
                val currentExp = room.exp
                val requiredExp = 50 * room.level
                val expProgress = (currentExp.toFloat() / requiredExp.toFloat())
                    .coerceIn(0f, 1f)

                // Barre d'expérience
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = expProgress,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "$currentExp / $requiredExp EXP",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                // Statuts rapides
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Temps total travaillé
                    StatChip(
                        label = "Temps",
                        value = formatDuration(room.tempsTotal),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Points
                    StatChip(
                        label = "Points",
                        value = room.points.toString(),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Jalons terminés
                    StatChip(
                        label = "Jalons",
                        value = room.jalonsTermines.toString(),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
fun RoomStats(vm: RoomViewModel) {
}

@Composable
fun JalonItem(
    jalon: Jalon,
    onFinish: () -> Unit,
    onModify: (Jalon) -> Unit,
    onDelete: () -> Unit
) {
    val surfaceColor = if (jalon.isDone){ colorGreenlight3 } else {colorYellowlight3}
    val onSurfaceColor = if (jalon.isDone){ colorGreendark3 } else {colorYellowdark3}

    var showDialog by remember{mutableStateOf(false)}

    if(showDialog)
    {
        JalonSettings(
            jalon,
            { showDialog = false },
            onModify,
            onDelete
        )
    }

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

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Deadline :\n" + jalon.timestamp.format(
                        DateTimeFormatter.ofPattern("dd/MM/yy")
                    ),
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
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        Icons.Default.SettingsSuggest,
                        contentDescription = "Paramètres du jalon",
                        tint=onSurfaceColor
                    )
                }
            }
        }
    }
}

@Composable
fun JalonSettings(
    jalon: Jalon,
    onDismiss: ()->Unit,
    onModify: (Jalon)->Unit,
    onDelete: ()->Unit
)
{

    var showModifyDialog by remember{mutableStateOf(false)}

    if(showModifyDialog)
    {
        ModifyJalonDialogue(
            jalon,
            {
                showModifyDialog = false
                onDismiss()
            },
            onModify,
        )
    }

    AlertDialog(
        onDismiss,
        {},
        Modifier.height(250.dp),
        text = {
            Column (
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Button(
                    {
                        showModifyDialog = true
                    },
                    Modifier
                        .width(180.dp)
                        .padding(10.dp)
                )
                {
                    Text("Modifier")
                }
                Button(
                    {
                        onDelete()
                        onDismiss()
                    },
                    Modifier
                        .width(180.dp)
                        .padding(10.dp)
                )
                {
                    Text("Supprimer")
                }
            }
        }
    )
}

@Composable
fun ModifyJalonDialogue(
    jalon: Jalon,
    onDismiss: () -> Unit,
    onModify: (Jalon) -> Unit
) {
    var name by remember { mutableStateOf(jalon.name) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = jalon.timestamp
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )
    val selectedDate = datePickerState.selectedDateMillis
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le jalon") },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    singleLine = true
                )

                DatePicker(state = datePickerState)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedDate!= null)
                    {
                        val newTimestamp = Instant
                            .ofEpochMilli(selectedDate)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()

                        val newJalon = jalon.copy(
                            name = name,
                            timestamp = newTimestamp
                        )

                        onModify(newJalon)
                    }

                    onDismiss()
                }
            ) {
                Text("Confirmer")
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
fun Pomodoro(
    vm: RoomViewModel,
    room: Room
) {
    CustomSurface(
        level = SurfaceLevel.High,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        val remaining by vm.remaining.collectAsState()
        val timer = room.timer
        val state = timer.state
        val onTertiary = MaterialTheme.colorScheme.onTertiaryContainer

        if (state == TimerState.NONE) {

            var hours by remember { mutableStateOf(0) }
            var minutes by remember { mutableStateOf(25) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(Modifier.weight(5f)) {
                    NumberPicker(
                        label = "Heures",
                        value = hours,
                        range = 0..5,
                        color = onTertiary
                    ) { hours = it }
                }

                Box(Modifier.weight(5f)) {
                    NumberPicker(
                        label = "Minutes",
                        value = minutes,
                        range = 0..59,
                        color = onTertiary
                    ) { minutes = it }
                }

                Box(
                    modifier = Modifier.weight(4f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val totalMinutes = hours * 60 + minutes
                            vm.startPomodoro(Duration.ofMinutes(totalMinutes.toLong()))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text("Lancer")
                    }
                }
            }
        }

        val totalMs = timer.durationMs
        val remainingMs = remaining.toMillis()
        val doneMs = (totalMs - remainingMs).coerceAtLeast(0L)
        val progress = (doneMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
        val m = remaining.toMinutes()
        val s = remaining.toSeconds() % 60

        if (state == TimerState.PAUSED) {
            PomodoroRunningUI(
                progress = progress,
                timeText = "%02d:%02d".format(m, s),
                onColor = onTertiary,
                colors=ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                onPrimary = { vm.resumePomodoro() },
                onSecondary = { vm.stopPomodoro() },
                primaryText = "Reprendre",
                secondaryText = "Arrêter"
            )
        }

        if (state == TimerState.RUNNING) {
            PomodoroRunningUI(
                progress = progress,
                timeText = "%02d:%02d".format(m, s),
                onColor = onTertiary,
                colors=ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                onPrimary = { vm.pausePomodoro() },
                onSecondary = { vm.stopPomodoro() },
                primaryText = "Pause",
                secondaryText = "Arrêter"
            )
        }
    }
}

@Composable
private fun PomodoroRunningUI(
    progress: Float,
    timeText: String,
    onColor: Color,
    colors: ButtonColors,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    primaryText: String,
    secondaryText: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)
        )

        Text(
            text = timeText,
            style = MaterialTheme.typography.headlineMedium,
            color = onColor
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onPrimary,
                colors = colors
            ) {
                Text(
                    primaryText)
            }
            Button(
                onClick = onSecondary,
                colors = colors
            ) {
                Text(
                    secondaryText)
            }
        }
    }
}



@Composable
fun NumberPicker(
    label: String,
    value: Int,
    range: IntRange,
    color: Color,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(label, style = MaterialTheme.typography.bodyMedium, color = color)

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
                    contentDescription = null,
                    tint = color
                )
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = color
            )

            IconButton(
                onClick = {
                    val new = (value + 1).coerceIn(range)
                    onValueChange(new)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}


@Composable
fun MemberItem(
    user : User,
    onAddFriend: () -> Unit,
    onMotivate: () -> Unit
) {
    val tint = MaterialTheme.colorScheme.onSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.email,
                color = tint,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 20.sp
            )
        }
        IconButton(
            onClick = onAddFriend,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Ajouter en ami",
                tint = tint
            )
        }
        IconButton(
            onClick = onMotivate,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEmotions,
                contentDescription = "Motiver",
                tint = tint
            )
        }

    }
}

@Composable
fun PlaceholderUserItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        )
    }
}




package com.example.focusparty.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusparty.model.Database
import com.example.focusparty.model.Event
import com.example.focusparty.model.Room
import com.example.focusparty.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(vm: HomeViewModel,uid:String) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        HomeTopBar(vm)
        Spacer(Modifier.height(8.dp))
        Stats(vm)
        Spacer(Modifier.height(8.dp))
        RoomsSection(vm)
        Spacer(Modifier.height(8.dp))
        EventSection(vm)
    }
}


@Composable
fun HomeTopBar(vm: HomeViewModel) {
    Row (

    ){
        IconButton(onClick = { vm.GoToCalendar() }) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Ouvrir calendrier"
            )
        }

        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy")

        Text(text = date.format(formatter))

        IconButton(onClick = { vm.GoToUserMenu() }) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Ouvrir menu utilisateur"
            )
        }

        IconButton(onClick = { vm.ShareApp() }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Partager l'application"
            )
        }
    }
}


@Composable
fun Stats(vm: HomeViewModel){

}

@Composable
fun RoomsSection(vm: HomeViewModel){
    val rooms by vm.rooms.collectAsState()

    LazyColumn {
        items(rooms) { room ->
            RoomItem(room)
        }
    }
}

@Composable
fun RoomItem(room:Room){
    Text(text = room.name)
}

@Composable
fun EventSection(vm: HomeViewModel){
    val events by vm.events.collectAsState()

    LazyColumn {
        items(events) { event ->
            EventItem(event)
        }
    }
}

@Composable
fun EventItem(event:Event){
    Text(text = event.name)
}

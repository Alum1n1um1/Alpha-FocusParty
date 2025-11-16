package com.example.focusparty.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.focusparty.model.Jalon
import com.example.focusparty.model.Room
import com.example.focusparty.viewmodel.RoomViewModel

@Composable
fun RoomScreen(
    vm: RoomViewModel
) {
    val room : Room = vm.getCurrentRoom()

    Column {
        TopRoomBar(vm)
        DashBoard(vm)
        ActionsMenu(vm)

    }


}

@Composable
fun ActionsMenu(vm: RoomViewModel) {
}

@Composable
fun DashBoard(vm: RoomViewModel) {
    Column(){
        RoomStats(vm)
        LazyColumn() {
            items(items=vm.getCurrentRoom().jalons) { jalon ->
                JalonItem(
                    jalon=jalon,
                    onFinish={},
                    onSettings={}
                )
            }
        }
    }
}

@Composable
fun RoomStats(vm: RoomViewModel) {
}

@Composable
fun TopRoomBar(vm: RoomViewModel) {

}












@Composable
fun JalonItem(
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
    ) {
        Text(text=jalon.name+"/"+jalon.id)
    }
}
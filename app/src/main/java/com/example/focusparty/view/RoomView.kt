package com.example.focusparty.view

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


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

@Composable
fun RoomScreen(vm: RoomViewModel, navController: NavHostController)
{
    Column(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        SalonTopBar(vm)
        Text("Todo")
    }
}

@Composable
fun SalonTopBar(vm: RoomViewModel) {

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
                Text("Nom du salon")
            }

            // --- Zone centrale ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            )
            {
                Row()
                {
                    Text("Statut du salon")
                    IconButton(
                        {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    {
                        Icon(Icons.Default.Pause, "Changer le statut")
                    }
                }
            }

            // --- Zone droite ---
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            )
            {
                Text("Personnes connectées")
            }
        }
    }
}
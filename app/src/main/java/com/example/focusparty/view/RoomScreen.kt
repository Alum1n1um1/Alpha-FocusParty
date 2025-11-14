package com.example.focusparty.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.focusparty.viewmodel.RoomViewModel

@Composable
fun RoomScreen(
    vm: RoomViewModel,
    navController: NavHostController
) {
    Text("Salon")
}

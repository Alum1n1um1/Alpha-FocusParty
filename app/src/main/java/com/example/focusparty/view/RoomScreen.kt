package com.example.focusparty.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.focusparty.viewmodel.HomeViewModel
import com.example.focusparty.viewmodel.RoomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
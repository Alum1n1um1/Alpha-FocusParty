package com.example.focusparty.view

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.focusparty.viewmodel.SettingsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    vm: SettingsViewModel
) {
    val darkMode by vm.darkMode.collectAsState()
    val notifications by vm.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Mode sombre
        SettingSwitchRow(
            title = "Mode sombre",
            leadingIcon = Icons.Default.LightMode,
            trailingIcon = Icons.Default.DarkMode,
            checked = darkMode,
            onCheckedChange = { vm.setDarkMode(it) }
        )

        Spacer(Modifier.height(12.dp))

        // Notifications
        SettingSwitchRow(
            title = "Notifications",
            leadingIcon = Icons.Default.NotificationsOff,
            trailingIcon = Icons.Default.NotificationsActive,
            checked = notifications,
            onCheckedChange = { vm.setNotifications(it) }
        )

        Spacer(Modifier.height(32.dp))

        // Effacer statistiques
        SettingActionRow(
            title = "Effacer statistiques",
            icon = Icons.Default.Delete,
            onClick = { vm.clearStats() }
        )

        Spacer(Modifier.height(12.dp))

        // Confidentialité
        SettingActionRow(
            title = "Confidentialité",
            icon = Icons.Default.Lock,
            onClick = {
                // TODO afficher popup rgpd ?
            }
        )

        Spacer(Modifier.height(12.dp))

        // Déconnexion
        SettingActionRow(
            title = "Déconnexion",
            icon = Icons.Default.Logout,
            onClick = { vm.logout() }
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = MaterialTheme.colorScheme.onBackground

        Row (
            verticalAlignment = Alignment.CenterVertically
        ){

            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 15.dp),
                color = tint
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                leadingIcon, contentDescription = null,
                tint=tint
            )
            Spacer(Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                trailingIcon, contentDescription = null,
                tint=tint
            )
        }
    }
}

@Composable
private fun SettingActionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 15.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        TextButton(
            onClick = onClick
        ) {
            Text("Exécuter")
        }
    }
}

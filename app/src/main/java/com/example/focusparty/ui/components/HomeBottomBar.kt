package com.example.focusparty.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.focusparty.ui.theme.*

@Composable
fun HomeBottomBar(
    currentDestination: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 3.dp
    ) {
        NavigationBarItem(
            selected = currentDestination == "leaderboard",
            onClick = { onNavigate("leaderboard") },
            icon = { Icon(Icons.Default.EmojiEvents, "Leaderboard") },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentDestination == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Default.Home, "Home") },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentDestination == "shop",
            onClick = { onNavigate("shop") },
            icon = { Icon(Icons.Default.ShoppingCart, "Boutique") },
            alwaysShowLabel = false
        )
    }
}
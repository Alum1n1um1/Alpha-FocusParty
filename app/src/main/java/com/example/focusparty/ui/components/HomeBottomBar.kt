package com.example.focusparty.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.focusparty.ui.theme.*

@Composable
fun HomeBottomBar(
    currentDestination: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {

        BottomItem(
            selected = currentDestination == "Leaderboard",
            icon = Icons.Default.EmojiEvents,
            onClick = { onNavigate("Leaderboard") }
        )

        BottomItem(
            selected = currentDestination == "Home",
            icon = Icons.Default.Home,
            onClick = { onNavigate("Home") }
        )

        BottomItem(
            selected = currentDestination == "Shop",
            icon = Icons.Default.ShoppingCart,
            onClick = { onNavigate("Shop") }
        )
    }
}

@Composable
private fun RowScope.BottomItem(
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        alwaysShowLabel = false,
        icon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            indicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    )
}
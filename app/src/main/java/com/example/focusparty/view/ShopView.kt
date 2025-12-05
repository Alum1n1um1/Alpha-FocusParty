package com.example.focusparty.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusparty.ui.components.HomeBottomBar
import com.example.focusparty.viewmodel.ShopViewModel

@Composable
fun ShopScreen(
    vm: ShopViewModel
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                currentDestination = "Shop",
                onNavigate = { dest -> vm.goToDestination(dest) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        ) {
                Text(text="TODO")
        }
    }
}
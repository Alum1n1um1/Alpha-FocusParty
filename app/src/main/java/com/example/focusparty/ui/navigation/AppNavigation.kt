package com.example.focusparty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.focusparty.model.Database
import com.example.focusparty.model.factories.CalendarViewModelFactory
import com.example.focusparty.model.factories.HomeViewModelFactory
import com.example.focusparty.model.factories.RoomViewModelFactory
import com.example.focusparty.view.HomeScreen
import com.example.focusparty.view.CalendarScreen
import com.example.focusparty.view.RoomScreen
import com.example.focusparty.viewmodel.CalendarViewModel
import com.example.focusparty.viewmodel.HomeViewModel
import com.example.focusparty.viewmodel.RoomViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    db: Database
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Ici on rajoute les différentes pages

        composable("home") {
            val vm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(db,navController)
            )
            HomeScreen(
                vm = vm,
                navController = navController
            )
        }

        composable("calendar") {
            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController)
            )
            CalendarScreen (
                vm = vm,
                navController = navController
            )
        }

        composable("room") {
            val vm: RoomViewModel = viewModel(
                factory = RoomViewModelFactory(db, navController)
            )
            RoomScreen(
                vm,
                navController)
        }


    }
}

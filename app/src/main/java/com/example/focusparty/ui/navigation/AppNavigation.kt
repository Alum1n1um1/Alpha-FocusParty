package com.example.focusparty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.factories.CalendarViewModelFactory
import com.example.focusparty.viewmodel.factories.HomeViewModelFactory
import com.example.focusparty.viewmodel.factories.RegisterViewModelFactory
import com.example.focusparty.viewmodel.factories.RoomViewModelFactory
import com.example.focusparty.view.HomeScreen
import com.example.focusparty.view.CalendarScreen
import com.example.focusparty.view.LoginScreen
import com.example.focusparty.view.RegisterScreen
import com.example.focusparty.view.RoomScreen
import com.example.focusparty.viewmodel.CalendarViewModel
import com.example.focusparty.viewmodel.HomeViewModel
import com.example.focusparty.viewmodel.LoginViewModel
import com.example.focusparty.viewmodel.RegisterViewModel
import com.example.focusparty.viewmodel.RoomViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    db: Database
) {
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        // Ici on rajoute les différentes pages

        composable("Home") {
            val vm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(db,navController)
            )
            HomeScreen(
                vm = vm,
                navController = navController
            )
        }

        composable("Calendar") {
            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController)
            )
            CalendarScreen (
                vm = vm,
                navController = navController
            )
        }

        composable(
            route = "Room/{roomId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""

            val vm: RoomViewModel = viewModel(
                factory = RoomViewModelFactory(db, navController, roomId)
            )
            RoomScreen(
                vm = vm
            )
        }

        composable("Login") {
            val vm: LoginViewModel = viewModel()

            LoginScreen(
                onLoginClick = { email, pass -> vm.login(email, pass) },
                onRegisterClick = { navController.navigate("Register") }
            )

            val success = vm.loginSuccess.collectAsState().value

            LaunchedEffect(success) {
                if (success) {
                    navController.navigate("Home") {
                        popUpTo("Login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        composable("Register") {
            val vm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(db)
            )

            RegisterScreen(
                onRegisterClick = { email, pass -> vm.register(email, pass) },
                onLoginClick = { navController.popBackStack() }
            )

            val done = vm.registerSuccess.collectAsState().value

            LaunchedEffect(done) {
                if (done) {
                    navController.navigate("Login") {
                        popUpTo("Register") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

package com.example.focusparty.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.focusparty.MyApplication
import com.example.focusparty.model.Database
import com.example.focusparty.model.factories.CalendarViewModelFactory
import com.example.focusparty.model.uid
import com.example.focusparty.viewmodel.factories.*
import com.example.focusparty.view.*
import com.example.focusparty.viewmodel.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    navController: NavHostController,
    db: Database
) {
    NavHost(
        navController = navController,
        startDestination = "Login"  // TODO: Remettre Login avant d'envoyer
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

        composable("Leaderboard") {
            val vm: LeaderboardViewModel = viewModel(
                factory = LeaderboardViewModelFactory(db, navController)
            )
            LeaderboardScreen(
                vm = vm
            )
        }

        // SHOP
        composable("Shop") {
            val vm: ShopViewModel = viewModel(
                factory = ShopViewModelFactory(db, navController)
            )
            ShopScreen(
                vm = vm
            )
        }

        composable("Calendar") {
            val app = LocalContext.current.applicationContext as Application

            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController, app)
            )
            CalendarScreen (
                vm = vm
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
                    db.setUserConnected(uid,true)

                    if (uid != null) {
                        MyApplication.lifecycleListener.startHeartbeatFor(uid)
                    }

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

        composable("Settings") { //TODO :
            val app = LocalContext.current.applicationContext as Application

            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController, app)
            )
            CalendarScreen (
                vm = vm
            )
        }

        composable("Stats") {//TODO :
            val app = LocalContext.current.applicationContext as Application

            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController, app)
            )
            CalendarScreen (
                vm = vm
            )
        }

        composable("AvatarChanger") {//TODO :
            val app = LocalContext.current.applicationContext as Application

            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(db, navController, app)
            )
            CalendarScreen (
                vm = vm
            )
        }
    }
}

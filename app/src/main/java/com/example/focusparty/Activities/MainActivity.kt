package com.example.focusparty.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusparty.model.Database
import com.example.focusparty.model.factories.HomeViewModelFactory
import com.example.focusparty.ui.navigation.AppNavigation
import com.example.focusparty.ui.theme.AppTheme
import com.example.focusparty.view.CalendarScreen
import com.example.focusparty.view.HomeScreen
import com.example.focusparty.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Database.getInstance()

        setContent {
            val navController = rememberNavController()

            AppTheme {
                AppNavigation(
                    navController = navController,
                    db=db
                )
            }
        }
    }
}
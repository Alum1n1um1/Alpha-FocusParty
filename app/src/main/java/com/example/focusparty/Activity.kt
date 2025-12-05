package com.example.focusparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.focusparty.model.Database
import com.example.focusparty.ui.navigation.AppNavigation
import com.example.focusparty.ui.theme.AppTheme
import com.example.focusparty.viewmodel.SettingsViewModel
import com.example.focusparty.viewmodel.factories.SettingsViewModelFactory

class Activity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val db = Database.Companion.getInstance()
            val navController = rememberNavController()

            val settingsVM: SettingsViewModel by viewModels {
                SettingsViewModelFactory(db, navController, application)   // si tu as un factory
            }
            val darkMode by settingsVM.darkMode.collectAsState()
            AppTheme (
                darkTheme = darkMode
            ){
                AppNavigation(
                    navController = navController,
                    db = db
                )
            }
        }
    }
}



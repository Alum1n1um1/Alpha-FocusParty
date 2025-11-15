package com.example.focusparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.focusparty.model.Database
import com.example.focusparty.ui.navigation.AppNavigation
import com.example.focusparty.ui.theme.AppTheme

class Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Database.Companion.getInstance()

        setContent {
            val navController = rememberNavController()

            AppTheme {
                AppNavigation(
                    navController = navController,
                    db = db
                )
            }
        }
    }
}
package com.example.focusparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusparty.model.Database
import com.example.focusparty.model.factories.HomeViewModelFactory
import com.example.focusparty.view.HomeScreen
import com.example.focusparty.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    companion object {
        const val UserID = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Database()
        val factory = HomeViewModelFactory(db)

        setContent {
            val vm: HomeViewModel = viewModel(factory = factory)

            MaterialTheme {
                HomeScreen(vm,UserID)
            }
        }
    }
}
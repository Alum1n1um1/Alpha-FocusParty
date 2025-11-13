package com.example.focusparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = BooksDb()                          // ton SQLiteOpenHelper
        val factory = BooksVMFactory(db)                // ton factory

        setContent {

            val vm: BooksViewModel = viewModel(factory = factory)

            MaterialTheme {
                BooksScreen(vm)                         // ton ecran Compose
            }
        }
    }
}